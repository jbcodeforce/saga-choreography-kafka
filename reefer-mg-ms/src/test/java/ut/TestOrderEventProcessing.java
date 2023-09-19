package ut;

import static org.awaitility.Awaitility.await;

import java.util.List;
import java.util.concurrent.Callable;

import org.acme.reefer.domain.Reefer;
import org.acme.reefer.infra.events.order.OrderCreatedEvent;
import org.acme.reefer.infra.events.order.OrderEvent;
import org.acme.reefer.infra.events.reefer.ReeferEvent;
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
@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)

@TestMethodOrder(OrderAnnotation.class)
public class TestOrderEventProcessing {

    private static String inChannelName = "orders";
    private static String outChannelName = "reefers";

    @Inject @Any
    InMemoryConnector connector;

    private Callable<Boolean> newReeferEventAdded( InMemorySink<ReeferEvent> reefers, int size) {
        return () -> reefers.received().size() >= size;
    }

    // need to add this method as with mvn test, there is parallel execution of all the tests, and 
    // we can see the ReeferEvent: ab10a491-254c-4ea4-b84e-7dcac100069f of NewReeferCreated from it tests
    private boolean assessIfOneOfTheEventIsExpected(InMemorySink<ReeferEvent> reefers, String expectedType) {
        boolean found = false;
        for (int i=0;i<reefers.received().size();i++) {
            ReeferEvent reeferEvent = reefers.received().get(i).getPayload();
            System.out.println(reeferEvent.toString());
            if (expectedType.equals(reeferEvent.getEventType())) {
                found = true;
                break;
            }

        }        
        return found;
    }

    @Test
    @Order(1)
    public void orderCreatedShouldGenerateReeferAllocated(){
        InMemorySource<OrderEvent> ordersChannel = connector.source(inChannelName);
        InMemorySink<ReeferEvent> reefers = connector.sink(outChannelName);
        OrderEvent oe = prepareOrderEvent(ordersChannel,"test-01",OrderEvent.ORDER_CREATED_TYPE);
        ordersChannel.send(oe);
       
        await().<List<? extends Message<ReeferEvent>>>until(newReeferEventAdded(reefers,1));
        Assert.assertTrue(assessIfOneOfTheEventIsExpected(reefers,ReeferEvent.REEFER_ALLOCATED_TYPE));
        reefers.clear();
    }



    @Order(2)
    @Test
    public void shouldGetReeferNotFound() {
        InMemorySource<OrderEvent> ordersChannel = connector.source(inChannelName);
        InMemorySink<ReeferEvent> reefers = connector.sink(outChannelName);

        OrderEvent oe = prepareOrderEvent(ordersChannel,"test-02",OrderEvent.ORDER_CREATED_TYPE);
        oe.productID="P15";
        ordersChannel.send(oe);
        await().<List<? extends Message<ReeferEvent>>>until(newReeferEventAdded(reefers,1));
        Assert.assertTrue(assessIfOneOfTheEventIsExpected(reefers,ReeferEvent.REEFER_NOT_FOUND_TYPE));
        reefers.clear();
    }

    private OrderEvent prepareOrderEvent( InMemorySource<OrderEvent> ordersChannel,String orderID, String evtType) {
        OrderCreatedEvent oce =  new OrderCreatedEvent("San Francisco","Shanghai",50);
        OrderEvent oe = new OrderEvent(evtType,oce);
        oe.orderID= orderID;
        oe.customerID = "C01";
        oe.productID = "P02";
        oe.quantity = 20;
        oe.status=OrderEvent.PENDING_STATUS;   
        return oe;
    }
}
