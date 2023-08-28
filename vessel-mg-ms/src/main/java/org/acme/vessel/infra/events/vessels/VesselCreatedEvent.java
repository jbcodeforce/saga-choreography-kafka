package org.acme.vessel.infra.events.vessels;

import org.acme.vessel.domain.Vessel;

public class VesselCreatedEvent extends VesselVariablePayload{

    public String vesselID;
    public long capacity;
    public String type; 
    public String status;
    public String departurePort;
    public String arrivalPort;

	public VesselCreatedEvent(Vessel v) {
        super();
        vesselID = v.vesselID;
        capacity = v.capacity;
        type = v.type;
        status = v.status;
        departurePort = v.departurePort;
        arrivalPort= v.arrivalPort;
	}

}
