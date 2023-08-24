package org.acme.reefer.infra.events.order;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class OrderEventDeserializer extends ObjectMapperDeserializer<OrderEvent> {
    static ObjectMapper om = new ObjectMapper();
    
    public OrderEventDeserializer(){
        // pass the class to the parent.
        super(OrderEvent.class);
        om.disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
    }
    
}
