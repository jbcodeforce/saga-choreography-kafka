package org.acme.vessel.infra.events.orders;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class OrderEventDeserializer extends ObjectMapperDeserializer<OrderEvent> {
    public OrderEventDeserializer(){
        // pass the class to the parent.
        super(OrderEvent.class);
    }
    
}
