package org.acme.order.infra.events.reefer;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use=Id.NAME,
    include = As.PROPERTY,
    property = "@type")
@JsonSubTypes({
@Type(value=ReeferAllocated.class, name="ReeferAllocated"),
@Type(value=ReeferNotFound.class, name="ReeferNotFound")})
public abstract class ReeferVariablePayload {
    
}
