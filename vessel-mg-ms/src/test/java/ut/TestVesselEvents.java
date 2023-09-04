package ut;

import java.nio.charset.StandardCharsets;

import org.acme.vessel.domain.Vessel;
import org.acme.vessel.infra.events.vessels.VesselAllocated;
import org.acme.vessel.infra.events.vessels.VesselCreatedEvent;
import org.acme.vessel.infra.events.vessels.VesselEvent;
import org.junit.jupiter.api.Test;
import org.wildfly.common.Assert;

import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TestVesselEvents {
  
 
    @Test
    public void vesselCreatedEventshouldBeAbleToSerialize(){
      Vessel v = new Vessel("V01", 1000, "VT01","AtPort","Oakland","Singapore");
      VesselCreatedEvent vce = new VesselCreatedEvent(v);

      VesselEvent ve = new VesselEvent(v.vesselID,VesselEvent.VESSEL_CREATED_TYPE,vce);
      ObjectMapperSerializer<VesselEvent> mapper = new ObjectMapperSerializer<VesselEvent>();
      byte[] inMessage = mapper.serialize("vessels", ve);
      Assert.assertNotNull(inMessage);
      System.out.println(new String(inMessage, StandardCharsets.UTF_8));
      mapper.close();
    }

    @Test
    public void vesselAllocatedEventshouldBeAbleToSerialize(){
      VesselAllocated vce = new VesselAllocated("Order-1","V01",50);

      VesselEvent ve = new VesselEvent("V01",VesselEvent.VESSEL_ALLOCATED_TYPE,vce);
      ObjectMapperSerializer<VesselEvent> mapper = new ObjectMapperSerializer<VesselEvent>();
      byte[] inMessage = mapper.serialize("vessels", ve);
      Assert.assertNotNull(inMessage);
      System.out.println(new String(inMessage, StandardCharsets.UTF_8));
      mapper.close();
    }

}
