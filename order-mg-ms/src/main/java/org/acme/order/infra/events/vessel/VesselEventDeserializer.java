package org.acme.order.infra.events.vessel;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class VesselEventDeserializer extends ObjectMapperDeserializer<VesselEvent> {
    public VesselEventDeserializer(){
        // pass the class to the parent.
        super(VesselEvent.class);
    }
    
}
