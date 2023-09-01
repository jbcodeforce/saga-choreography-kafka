package ut;

import java.util.List;
import java.util.concurrent.Callable;

import org.acme.vessel.domain.Vessel;
import org.acme.vessel.domain.VesselService;
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
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import static org.awaitility.Awaitility.await;
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
@QuarkusTestResource(KafkaTestResourceLifecycleManager.class)
public class VesselEventProcessingTest {

    @Inject
    public VesselService service;

    @Inject @Any
    InMemoryConnector connector;
    
    private static String outChannelName = "vessels";

    private Callable<Boolean> newVesselAdded( InMemorySink<VesselEvent> vessels) {
        return () -> vessels.received().size() == 1;
    }
    
    @Test
    @Order(1)
    public void shouldGenerateVesselCreateEvent() {
        Vessel v = new Vessel("V11", 1000, "VT01","AtPort","Oakland","Singapore");
        Vessel out = service.saveVessel(v);
        Assert.assertNotNull(out);
        InMemorySink<VesselEvent> vessels = connector.sink(outChannelName);
        
        await().<List<? extends Message<Vessel>>>until(newVesselAdded(vessels));

        VesselEvent vesselEvent = vessels.received().get(0).getPayload();
        Assert.assertNotNull(vesselEvent);
        System.out.println(vesselEvent.payload.toString());
        Assert.assertTrue(vesselEvent.vesselID.equals("V11"));
        Assert.assertTrue(vesselEvent.getEventType().equals(VesselEvent.VESSEL_CREATED_TYPE));
        vessels.clear();
    }

    
}
