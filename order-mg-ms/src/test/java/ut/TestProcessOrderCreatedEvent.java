package ut;



import org.acme.order.infra.events.order.OrderCreatedEvent;
import org.acme.order.infra.events.order.OrderEvent;
import org.junit.jupiter.api.Test;

import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TestProcessOrderCreatedEvent {
    
 
 
    @Test
    public void shouldBeAbleToSerialize(){
      OrderCreatedEvent oce = new OrderCreatedEvent("Sydney","San Francisco");

      OrderEvent oe = new OrderEvent(OrderEvent.ORDER_CREATED_TYPE,oce);
      oe.orderID = "Test01";
      oe.quantity = 80;
      ObjectMapperSerializer<OrderEvent> mapper = new ObjectMapperSerializer<OrderEvent>();
      byte[] inMessage = mapper.serialize("orders", oe);
      mapper.close();
    }

}
