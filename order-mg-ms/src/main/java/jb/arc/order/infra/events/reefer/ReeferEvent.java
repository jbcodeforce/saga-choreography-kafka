package jb.arc.order.infra.events.reefer;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jb.arc.order.infra.events.EventBase;

@RegisterForReflection
public class ReeferEvent extends EventBase {
    public static final String REEFER_ALLOCATED_TYPE = "ReeferAllocated";
	public String reeferID;
    public ReeferVariablePayload payload;

    public ReeferEvent(){}
}
