package org.acme.reefer.infra.events.reefer;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;


@JsonTypeInfo(use=Id.NAME,
        include = As.PROPERTY,
        property = "@type")
@JsonSubTypes({
    @Type(value=ReeferCreatedEvent.class,name="ReeferCreatedEvent"),
    @Type(value=ReeferAllocated.class,name="ReeferAllocated")
})
public abstract class ReeferVariablePayload {
    
}
