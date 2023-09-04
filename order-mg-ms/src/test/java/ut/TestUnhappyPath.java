package ut;

import static org.awaitility.Awaitility.await;

import java.util.List;
import java.util.concurrent.Callable;

import org.acme.order.domain.Address;
import org.acme.order.domain.OrderService;
import org.acme.order.domain.ShippingOrder;
import org.acme.order.infra.events.order.OrderEvent;
import org.acme.order.infra.events.order.OrderUpdatedEvent;
import org.acme.order.infra.events.vessel.VesselEvent;
import org.acme.order.infra.events.vessel.VesselNotFound;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.common.constraint.Assert;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;


@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
public class TestUnhappyPath {
    
    private static String vesselChannelName = "vessels";
    private static String reeferChannelName = "reefers";
    private static String outChannelName = "orders";

    @Inject
    OrderService service;


    @Inject @Any
    InMemoryConnector connector;
 
    private Callable<Boolean> newOrderAdded( InMemorySink<OrderEvent> orders) {
      return () -> orders.received().size() >= 1;
    }
    
    private ShippingOrder createTestOrder(){
      Address pickup = new Address("1st street","Oakland","USA", "CA", "94000");
      Address destination = new Address("main street","Shanghai", "CH","CH1","09000");
      ShippingOrder order = new ShippingOrder("T01","P01", "C01", 20, pickup, "09/05/2023", destination, "10/30/2023", ShippingOrder.PENDING_STATUS);
      return order;
    }

    @Test
    @Order(1)
    public void noVesselEventShouldPurOrderOnHold(){
        ShippingOrder order = createTestOrder();
        service.createOrder(order);

        InMemorySink<OrderEvent> orders = connector.sink(outChannelName);
      
        // simulate vessel event
        InMemorySource<VesselEvent> vessels = connector.source(vesselChannelName);
        VesselEvent ve = new VesselEvent();
        ve.eventType = VesselEvent.TYPE_VESSEL_NOT_FOUND;
        VesselNotFound vnf = new VesselNotFound();
        vnf.orderID=order.orderID;;
        ve.payload=vnf;
        vessels.send(ve);

        await().<List<? extends Message<OrderEvent>>>until(newOrderAdded(orders));
        OrderEvent oe = orders.received().get(1).getPayload();
        Assert.assertNotNull(oe);
        Assert.assertTrue(oe.eventType.equals(OrderEvent.ORDER_UPDATED_TYPE));
        Assert.assertNotNull(oe.payload); 
        OrderUpdatedEvent oue = (OrderUpdatedEvent)oe.payload;
        Assert.assertTrue(oe.status == OrderEvent.ONHOLD_STATUS);
        Assert.assertTrue(oe.orderID == order.orderID);
    }
}
