package jb.arc.order.infra.events.vessel;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * 
 */
@RegisterForReflection
public class VesselAllocated extends VesselVariablePayload {
	public String orderID;

	public VesselAllocated() {}

	public VesselAllocated(String oid) {
		this.orderID = oid;
	}

}
