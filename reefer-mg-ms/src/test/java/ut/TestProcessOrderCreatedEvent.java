package ut;



import java.util.List;

import jakarta.inject.Inject;

import org.acme.reefer.domain.Reefer;
import org.acme.reefer.infra.events.order.OrderAgent;
import org.acme.reefer.infra.events.order.OrderCreatedEvent;
import org.acme.reefer.infra.events.order.OrderEvent;
import org.acme.reefer.infra.events.reefer.ReeferAllocated;
import org.acme.reefer.infra.events.reefer.ReeferEvent;
import org.acme.reefer.infra.events.reefer.ReeferEventDeserializer;
import org.acme.reefer.infra.repo.ReeferRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest

public class TestProcessOrderCreatedEvent {
    
    @Inject
    OrderAgent agent;

    @Inject
    ReeferRepository repo;

    /**
     * Freezers are loaded from classpath from json file
     */
    @Test
    public void shouldHaveTwoReefersForAcapacityOf120(){
        List<Reefer> f = repo.getReefersForOrder("T01", "New York", 120);
        Assertions.assertEquals(2,f.size());
    }


    /**
     * Validate polymorphism on the payload of reefer event
     */
    @Test
    public void shouldBeAbleToCast(){
        ReeferAllocated oce = new ReeferAllocated("RE01","Order01");

        ReeferEvent oe = new ReeferEvent();
        oe.payload = oce;
        ObjectMapperSerializer<ReeferEvent> mapper = new ObjectMapperSerializer<ReeferEvent>();
        byte[] inMessage = mapper.serialize("reefers", oe);
        ReeferEventDeserializer deserialize = new ReeferEventDeserializer();
        ReeferEvent oe2 = deserialize.deserialize("reefers", inMessage);
        ReeferAllocated oce2 = (ReeferAllocated)oe2.payload;
        Assertions.assertEquals("Order01", oce2.orderID);
        mapper.close();
        deserialize.close();
    }

    /**
     * Test at the agent class level, but the processOrder will publish events too 
     */
    @Test
    public void shouldGetAReeferAllocatedEventFromAGoodOrderCreatedEvent(){

        OrderCreatedEvent oce = new OrderCreatedEvent("San Francisco","Singapour",30);

        OrderEvent oe = new OrderEvent(OrderEvent.ORDER_CREATED_TYPE,oce);
        oe.orderID = "Test01";
        oe.quantity = 80;
        ReeferEvent re = agent.processOrderCreatedEvent(oe);
        Assertions.assertEquals(ReeferEvent.REEFER_ALLOCATED_TYPE,re.getEventType());
        Assertions.assertEquals(oe.orderID,((ReeferAllocated)re.payload).orderID);
        Assertions.assertNotNull(((ReeferAllocated)re.payload).reeferIDs);
        System.out.println("Reefer -> " + ((ReeferAllocated)re.payload).reeferIDs);
    }

    @Test
    public void shouldNotGetAnyEventFromUnfulliableOrderCreatedEvent(){

        OrderCreatedEvent oce = new OrderCreatedEvent("Sydney","San Juan",20);

        OrderEvent oe = new OrderEvent(OrderEvent.ORDER_CREATED_TYPE,oce);
        oe.orderID = "Test02";
        oe.quantity = 80;
        ReeferEvent re = agent.processOrderCreatedEvent(oe);
        Assertions.assertNull(re);

    }

   
}
