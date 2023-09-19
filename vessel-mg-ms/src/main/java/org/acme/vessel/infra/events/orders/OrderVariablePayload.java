package org.acme.vessel.infra.events.orders;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use=Id.NAME,
        include = As.PROPERTY,
        property = "@type")
@JsonSubTypes({
    @Type(value=OrderCreatedEvent.class, name="OrderCreatedEvent"),
    @Type(value=OrderUpdatedEvent.class, name="OrderUpdatedEvent")})

public abstract class  OrderVariablePayload {

}

