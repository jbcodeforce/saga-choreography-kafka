package ut;

import static org.awaitility.Awaitility.await;

import java.util.List;
import java.util.concurrent.Callable;

import org.acme.order.domain.Address;
import org.acme.order.domain.OrderService;
import org.acme.order.domain.ShippingOrder;
import org.acme.order.infra.events.order.OrderEvent;
import org.acme.order.infra.events.order.OrderUpdatedEvent;
import org.acme.order.infra.events.reefer.ReeferEvent;
import org.acme.order.infra.events.reefer.ReeferNotFound;
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
public class UnhappyPathTest extends CommonToAll{
    
    @Inject
    OrderService service;

    @Inject @Any
    InMemoryConnector connector;
 
    private Callable<Boolean> newOrderAdded( InMemorySink<OrderEvent> orders, int size) {
      return () -> orders.received().size() >= size;
    }
    
    @Test
    @Order(1)
    public void noVesselEventShouldPutOrderOnHold(){
        ShippingOrder order = createTestOrder();
        service.createOrder(order);

        // ensure the order is processed
        InMemorySink<OrderEvent> orders = connector.sink(outChannelName);
        await().<List<? extends Message<OrderEvent>>>until(newOrderAdded(orders,1));
      
        // simulate vessel event
        InMemorySource<VesselEvent> vesselOut = connector.source(vesselChannelName);
       
        VesselEvent ve = new VesselEvent();
        ve.eventType = VesselEvent.TYPE_VESSEL_NOT_FOUND;
        VesselNotFound vnf = new VesselNotFound();
        vnf.orderID=order.orderID;
        vnf.message = "order not found";
        ve.payload=vnf;
        vesselOut.send(ve);
  
        // index 0 has order created event for T1, idx 1 should have order updated
        await().<List<? extends Message<OrderEvent>>>until(newOrderAdded(orders,2));
        int idx = assessIfOneOfTheEventIsExpected(orders,OrderEvent.ORDER_UPDATED_TYPE);
        
        OrderEvent oe = orders.received().get(idx).getPayload();
        Assert.assertNotNull(oe);
        Assert.assertTrue(oe.eventType.equals(OrderEvent.ORDER_UPDATED_TYPE));
        Assert.assertNotNull(oe.payload); 
        OrderUpdatedEvent oue = (OrderUpdatedEvent)oe.payload;
        Assert.assertTrue(oe.status == OrderEvent.ONHOLD_STATUS);
        Assert.assertTrue(oe.orderID == order.orderID);
        orders.clear();
    }


    @Test
    @Order(2)
    public void noReeferEventShouldPutOrderOnHold(){
        ShippingOrder order = createTestOrder();
        order.orderID = "T02";
        order.productID = "PT02";
        service.createOrder(order);
        InMemorySink<OrderEvent> orders = connector.sink(outChannelName);
        await().<List<? extends Message<OrderEvent>>>until(newOrderAdded(orders,1));
        // simulate reefer event
        InMemorySource<ReeferEvent> reefers = connector.source(reeferChannelName);
        ReeferEvent re = new ReeferEvent();
        re.eventType = ReeferEvent.REEFER_NOT_FOUND_TYPE;
        ReeferNotFound rnf = new ReeferNotFound();
        rnf.orderID=order.orderID;;
        re.payload=rnf;
        reefers.send(re);
      // index 2 has order created event for T2
        await().<List<? extends Message<OrderEvent>>>until(newOrderAdded(orders,2));
        Assert.assertTrue( -1 != assessIfOneOfTheEventIsExpected(orders,OrderEvent.ORDER_UPDATED_TYPE));
        
        orders.clear();
    }
}
