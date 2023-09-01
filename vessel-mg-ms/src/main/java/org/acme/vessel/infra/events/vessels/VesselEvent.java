package org.acme.vessel.infra.events.vessels;

import java.util.Date;

import org.acme.vessel.infra.events.EventBase;

public class VesselEvent extends EventBase {

    public static final String VESSEL_ALLOCATED_TYPE = "VesselAllocated"; 
    public static final String VESSEL_DESALLOCATED_TYPE = "VesselDesallocated"; 
    public static final String VESSEL_NOT_FOUND_TYPE = "VesselNotFound"; 
    public static final String VESSEL_CREATED_TYPE = "VesselCreated"; 
    public String vesselID;
    public VesselVariablePayload payload;

    public VesselEvent() {
        super(VESSEL_CREATED_TYPE, new Date().getTime(), EventBase.DEFAULT_VERSION);
    }

    public VesselEvent(String key, String etype, VesselVariablePayload p) {
        super(etype, new Date().getTime(), EventBase.DEFAULT_VERSION);
        this.vesselID = key;
        this.payload = p;
    }
}
