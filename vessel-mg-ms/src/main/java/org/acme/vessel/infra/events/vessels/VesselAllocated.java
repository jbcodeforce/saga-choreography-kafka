package org.acme.vessel.infra.events.vessels;

public class VesselAllocated extends VesselVariablePayload{
    public String orderID;
    public String vesselID;
    public long capacityAllocated;

    public VesselAllocated(String oid, String vid, long c) {
        super();
        orderID = oid;
        vesselID = vid;
        capacityAllocated = c;
    }

    public String toString() {
        if (capacityAllocated == 0) {
            return "Vessel:" + vesselID + " desallocated for " + orderID;
        } else {
            return "Vessel:" + vesselID + " allocated to " + orderID;
        }
       
    }
}
