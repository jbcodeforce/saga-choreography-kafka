package org.acme.reefer.infra.events.reefer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class ReeferEventDeserializer extends ObjectMapperDeserializer<ReeferEvent> {
    static ObjectMapper om = new ObjectMapper();
    public ReeferEventDeserializer(){
        // pass the class to the parent.
        
       
        super(ReeferEvent.class,om);
        om.disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
    }
    
}
