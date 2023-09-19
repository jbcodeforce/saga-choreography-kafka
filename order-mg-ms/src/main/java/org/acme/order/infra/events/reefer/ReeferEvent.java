package org.acme.order.infra.events.reefer;

import org.acme.order.infra.events.EventBase;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ReeferEvent extends EventBase {
    public static final String REEFER_ALLOCATED_TYPE = "ReeferAllocated";
    public static final String REEFER_NOT_FOUND_TYPE = "ReeferNotFound";
    
	public String reeferID;
    public ReeferVariablePayload payload;

    public ReeferEvent(){}
}
