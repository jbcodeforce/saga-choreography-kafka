package ut;

import static org.awaitility.Awaitility.await;

import java.util.List;
import java.util.concurrent.Callable;

import org.acme.vessel.domain.Vessel;
import org.acme.vessel.domain.VesselService;
import org.acme.vessel.infra.events.orders.OrderCreatedEvent;
import org.acme.vessel.infra.events.orders.OrderEvent;
import org.acme.vessel.infra.events.orders.OrderUpdatedEvent;
import org.acme.vessel.infra.events.vessels.VesselEvent;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.wildfly.common.Assert;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
public class OrderEventProcessingTest {

    @Inject
    public VesselService service;

    @Inject @Any
    InMemoryConnector connector;

    private static String inChannelName = "orders";
    private static String outChannelName = "vessels";


    @Test
    @Order(2)
    public void shouldGetVesselAllocatedEvent() throws InterruptedException{
        InMemorySource<OrderEvent> ordersChannel = connector.source(inChannelName);
        InMemorySink<VesselEvent> vessels = connector.sink(outChannelName);

        OrderEvent oe =prepareOrderEvent(ordersChannel,"O02",OrderEvent.ORDER_CREATED_TYPE);
        ordersChannel.send(oe);
        await().<List<? extends Message<Vessel>>>until(newVesselAdded(vessels,1));
        
        VesselEvent vesselEvent = vessels.received().get(0).getPayload();
        Assert.assertTrue(vesselEvent.getEventType().equals(VesselEvent.VESSEL_ALLOCATED_TYPE));
        System.out.println(vesselEvent.payload.toString());
        vessels.clear();
    }

    private Callable<Boolean> newVesselAdded( InMemorySink<VesselEvent> vessels, int size) {
        return () -> vessels.received().size() == size;
    }
   
    @Test
    @Order(3)
    public void shouldGenerateCompensationOnOrder() throws InterruptedException{
        // Get an order
        InMemorySource<OrderEvent> ordersChannel = connector.source(inChannelName);
        InMemorySink<VesselEvent> vessels = connector.sink(outChannelName);

        OrderEvent oe = prepareOrderEvent(ordersChannel,"O44",OrderEvent.ORDER_CREATED_TYPE);
        ordersChannel.send(oe);
        // a vessel is allocated
        await().<List<? extends Message<Vessel>>>until(newVesselAdded(vessels,1));
        VesselEvent vesselEvent = vessels.received().get(0).getPayload();
        Assert.assertTrue(vesselEvent.getEventType().equals(VesselEvent.VESSEL_ALLOCATED_TYPE));
        System.out.println(vesselEvent.payload.toString());
       
        // now send an onHold on the same order to compensate the vessel
        OrderUpdatedEvent oue = new OrderUpdatedEvent(vesselEvent.vesselID,"O44");
        oe = new OrderEvent(OrderEvent.ORDER_UPDATED_TYPE, oue);
        oe.orderID= "O44";
        oe.customerID = "C01";
        oe.status = OrderEvent.ONHOLD_STATUS;
        ordersChannel.send(oe);


        vesselEvent = vessels.received().get(1).getPayload();
        Assert.assertNotNull(vesselEvent);
        System.out.println(vesselEvent.payload.toString());
        Assert.assertTrue(vesselEvent.getEventType().equals(VesselEvent.VESSEL_DESALLOCATED_TYPE));
        vessels.clear();
    }
    
    private OrderEvent prepareOrderEvent( InMemorySource<OrderEvent> ordersChannel,String orderID, String evtType) {
        OrderCreatedEvent oce =  new OrderCreatedEvent("Oakland","Shanghai",50);
        OrderEvent oe = new OrderEvent(evtType,oce);
        oe.orderID= orderID;
        oe.customerID = "C01";
        oe.quantity = 20;
        oe.status=OrderEvent.PENDING_STATUS;   
        return oe;
    }

    @Order(4)
    @Test
    public void shouldGetVesselNotFound() {
        InMemorySource<OrderEvent> ordersChannel = connector.source(inChannelName);
        InMemorySink<VesselEvent> vessels = connector.sink(outChannelName);

        OrderEvent oe = prepareOrderEvent(ordersChannel,"Order16",OrderEvent.ORDER_CREATED_TYPE);
        oe.productID="P16";
        ordersChannel.send(oe);
        await().<List<? extends Message<Vessel>>>until(newVesselAdded(vessels,1));
        
        VesselEvent vesselEvent = vessels.received().get(0).getPayload();
        Assert.assertTrue(vesselEvent.getEventType().equals(VesselEvent.VESSEL_NOT_FOUND_TYPE));
        System.out.println(vesselEvent.payload.toString());
        vessels.clear();
    }
}
