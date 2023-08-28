package org.acme.vessel.infra.events.vessels;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use=Id.CLASS,defaultImpl= VesselAllocated.class)
@JsonSubTypes({
    @Type(value=VesselCreatedEvent.class,name="VesselCreatedEvent"),
    @Type(value=VesselAllocated.class,name="VesselAllocated")
})
public class VesselVariablePayload {
    
}
