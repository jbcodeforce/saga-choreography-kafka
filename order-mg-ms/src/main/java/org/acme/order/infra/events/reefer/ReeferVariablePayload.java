package org.acme.order.infra.events.reefer;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use=Id.CLASS,include = As.PROPERTY,property = "@class")
@JsonSubTypes({@Type(ReeferAllocated.class)})
public abstract class ReeferVariablePayload {
    
}
