package ut;



import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

import org.acme.order.domain.Address;
import org.acme.order.domain.OrderService;
import org.acme.order.domain.ShippingOrder;
import org.acme.order.infra.api.ShippingOrderResource;
import org.acme.order.infra.events.order.OrderCreatedEvent;
import org.acme.order.infra.events.order.OrderEvent;
import org.acme.order.infra.events.order.OrderUpdatedEvent;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.wildfly.common.Assert;

import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
public class TestProcessOrderCreatedEvent extends CommonToAll {
    
    @Inject
    OrderService service;

    @Inject
    ShippingOrderResource orderAPIs;

    @Inject @Any
    InMemoryConnector connector;
 
    private Callable<Boolean> newOrderAdded( InMemorySink<OrderEvent> orders) {
      return () -> orders.received().size() >= 1;
    }

    @Test
    @Order(1)
    public void shouldBeAbleToSerialize(){
      OrderCreatedEvent oce = new OrderCreatedEvent("Sydney","San Francisco","10/20/2023",10);

      OrderEvent oe = new OrderEvent(OrderEvent.ORDER_CREATED_TYPE,oce);
      oe.orderID = "Test01";
      oe.quantity = 80;
      ObjectMapperSerializer<OrderEvent> mapper = new ObjectMapperSerializer<OrderEvent>();
      byte[] inMessage = mapper.serialize("orders", oe);
      Assert.assertNotNull(inMessage);
      mapper.close();
    }

    @Test
    @Order(2)
    public void testNewOrderShouldGenerateOrderCreatedEvent() {
      Address pickup = new Address("1st street","Oakland","USA", "CA", "94000");
      Address destination = new Address("main street","Shanghai", "CH","CH1","09000");
      ShippingOrder order = new ShippingOrder("TestHappy01","P01", "C01", 20, pickup, "09/05/2023", destination, "10/30/2023", ShippingOrder.PENDING_STATUS);
      service.createOrder(order);

      InMemorySink<OrderEvent> orders = connector.sink(outChannelName);
        
      await().<List<? extends Message<OrderEvent>>>until(newOrderAdded(orders));
      int idx = assessIfOneOfTheEventIsExpected(orders,OrderEvent.ORDER_CREATED_TYPE);
        
      OrderEvent oe = orders.received().get(idx).getPayload();
        
    
      Assert.assertNotNull(oe);
      Assert.assertNotNull(oe.payload); 
      OrderCreatedEvent oce = (OrderCreatedEvent)oe.payload;
      Assert.assertTrue(destination.getCity().equals(oce.destinationCity)); 
      Assert.assertTrue(pickup.getCity().equals(oce.pickupCity)); 
      Assert.assertTrue(oce.expectedCapacity == order.quantity);
      Assert.assertTrue(oce.pickupDate == order.pickupDate);
      Assert.assertNotNull(oce.creationDate);
      orders.clear();
    }

}
