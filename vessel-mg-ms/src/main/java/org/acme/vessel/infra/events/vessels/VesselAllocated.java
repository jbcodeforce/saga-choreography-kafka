package org.acme.vessel.infra.events.vessels;

public class VesselAllocated extends VesselVariablePayload{
    public String orderID;

    public VesselAllocated(String id) {
        super();
        orderID = id;
    }
}
