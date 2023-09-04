package ut;



import java.util.List;
import java.util.concurrent.Callable;

import org.acme.order.domain.Address;
import org.acme.order.domain.OrderService;
import org.acme.order.domain.ShippingOrder;
import org.acme.order.infra.events.order.OrderCreatedEvent;
import org.acme.order.infra.events.order.OrderEvent;
import org.acme.order.infra.events.order.OrderUpdatedEvent;
import org.apache.http.HttpStatus;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.wildfly.common.Assert;

import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import io.smallrye.reactive.messaging.memory.InMemorySink;
import io.smallrye.reactive.messaging.memory.InMemorySource;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import static org.awaitility.Awaitility.await;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
public class TestProcessOrderCreatedEvent {
    
    private static String vesselChannelName = "vessels";
    private static String reeferChannelName = "reefers";
    private static String outChannelName = "orders";
 
    @Inject
    OrderService service;


    @Inject @Any
    InMemoryConnector connector;
 
    private Callable<Boolean> newOrderAdded( InMemorySink<OrderEvent> orders) {
      return () -> orders.received().size() == 1;
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
      ShippingOrder order = new ShippingOrder("T01","P01", "C01", 20, pickup, "09/05/2023", destination, "10/30/2023", ShippingOrder.PENDING_STATUS);
      ShippingOrder out = service.createOrder(order);
      Assert.assertNotNull(out);
      Assert.assertNotNull(out.orderID);
      InMemorySink<OrderEvent> orders = connector.sink(outChannelName);
        
      await().<List<? extends Message<OrderEvent>>>until(newOrderAdded(orders));
      OrderEvent oe = orders.received().get(0).getPayload();
      Assert.assertNotNull(oe);
      Assert.assertTrue(oe.eventType.equals(OrderEvent.ORDER_CREATED_TYPE));
      Assert.assertNotNull(oe.payload); 
      OrderCreatedEvent oce = (OrderCreatedEvent)oe.payload;
      Assert.assertTrue(destination.getCity().equals(oce.destinationCity)); 
      Assert.assertTrue(pickup.getCity().equals(oce.pickupCity)); 
      Assert.assertTrue(oce.expectedCapacity == order.quantity);
      Assert.assertTrue(oce.pickupDate == order.pickupDate);
      Assert.assertNotNull(oce.creationDate);
    }

    @Test
    @Order(3)
    public void getCurrentOrdersInRepository(){
        await().untilAsserted(() -> {
          given()
                  .when().get("/api/v1/orders")
                  .then()
                  .statusCode(HttpStatus.SC_OK)
                  .body("size()", greaterThanOrEqualTo(1));

        });
    }

    @Test
    @Order(4)
    public void updateOrderGeneratesOrderUpdateEvent(){ 
        ShippingOrder o1 = get("/api/v1/orders/Order01")
                    .then()
                    .statusCode(HttpStatus.SC_OK)
                    .extract()
                    .as(ShippingOrder.class);

        Assert.assertNotNull(o1);
        o1.quantity+=10;
        service.updateOrder(o1);
        InMemorySink<OrderEvent> orders = connector.sink(outChannelName);
        
        await().<List<? extends Message<OrderEvent>>>until(newOrderAdded(orders));
        OrderEvent oe = orders.received().get(0).getPayload();
        Assert.assertNotNull(oe);
        Assert.assertTrue(oe.eventType.equals(OrderEvent.ORDER_UPDATED_TYPE));
        Assert.assertNotNull(oe.payload); 
        OrderUpdatedEvent oue = (OrderUpdatedEvent)oe.payload;
        Assert.assertTrue(o1.destinationAddress.getCity().equals(oue.destinationCity)); 
        Assert.assertTrue(o1.pickupAddress.getCity().equals(oue.pickupCity)); 
        Assert.assertTrue(oue.pickupDate == o1.pickupDate);
        Assert.assertNotNull(oue.updateDate);
        Assert.assertTrue(oue.expectedCapacity == o1.quantity);
    }
}
