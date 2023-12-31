package org.acme.order.infra.events.vessel;


import org.acme.order.infra.events.EventBase;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class VesselEvent extends EventBase {
    public static final String TYPE_VESSEL_ASSIGNED = "VesselAssigned"; // from vessel mgr ms
    public static final String TYPE_VESSEL_NOT_FOUND = "VesselNotFound"; // from vessel mgr ms
    public String vesselID;
    public VesselVariablePayload payload;

   public VesselEvent(){} 
}
