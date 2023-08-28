package org.acme.vessel.infra.events.vessels;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class VessetEventDeserializer extends ObjectMapperDeserializer<VesselEvent> {
    public VessetEventDeserializer(){
        // pass the class to the parent.
        super(VesselEvent.class);
    }
    
}
