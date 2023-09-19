package ut;

import org.acme.order.domain.Address;
import org.acme.order.domain.ShippingOrder;
import org.acme.order.infra.events.order.OrderEvent;

import io.smallrye.reactive.messaging.memory.InMemorySink;

public class CommonToAll {

    protected static String vesselChannelName = "vessels";

    protected static String reeferChannelName = "reefers";
    protected static String outChannelName = "orders";

    protected ShippingOrder createTestOrder(){
        Address pickup = new Address("1st street","Oakland","USA", "CA", "94000");
        Address destination = new Address("main street","Shanghai", "CH","CH1","09000");
        ShippingOrder order = new ShippingOrder("T01","PT01", "CT01", 10, pickup, "09/05/2023", destination, "10/30/2023", ShippingOrder.PENDING_STATUS);
        return order;
      }
  
      protected int assessIfOneOfTheEventIsExpected(InMemorySink<OrderEvent> orders, String expectedType) {
        for (int i=0;i<orders.received().size();i++) {
          OrderEvent orderEvent = orders.received().get(i).getPayload();
            System.out.println(i +" -> " +orderEvent.toString());
            if (expectedType.equals(orderEvent.getEventType())) {
                return i;
            }
  
        }        
        return -1;
      }
}
