package org.acme.order.infra.events.vessel;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use=Id.NAME,
    include = As.PROPERTY,
    property = "@type")
@JsonSubTypes({
@Type(value=VesselAllocated.class, name="VesselAllocated"),
@Type(value=VesselNotFound.class, name="VesselNotFound")})
public abstract class VesselVariablePayload {
    
}
