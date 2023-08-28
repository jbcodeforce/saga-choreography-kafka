package org.acme.vessel.infra.events.vessels;

import java.util.Date;

import org.acme.vessel.domain.Vessel;
import org.acme.vessel.infra.events.EventBase;

public class VesselEvent extends EventBase {

    public static final String TYPE_VESSEL_ASSIGNED = "VesselAssigned"; 
    public static final String TYPE_VESSEL_NOT_FOUND = "VesselNotFound"; 
    public String vesselID;
    public VesselVariablePayload payload;

    public VesselEvent() {
        super();
        this.timestampMillis = new Date().getTime();
        this.version = DEFAULT_VERSION;
    }

    public VesselEvent(Vessel v) {
        super();
        this.timestampMillis = new Date().getTime();
        this.version = DEFAULT_VERSION;
        this.vesselID = v.vesselID;
    }
}
