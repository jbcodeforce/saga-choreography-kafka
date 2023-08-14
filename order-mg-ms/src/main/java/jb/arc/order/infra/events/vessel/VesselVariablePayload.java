package jb.arc.order.infra.events.vessel;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use=Id.DEDUCTION)
@JsonSubTypes({@Type(VesselAllocated.class)})
public abstract class VesselVariablePayload {
    
}
