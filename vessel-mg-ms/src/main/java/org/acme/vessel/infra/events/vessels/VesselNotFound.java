package org.acme.vessel.infra.events.vessels;

public class VesselNotFound extends VesselVariablePayload{
    public String orderID;
    public String message;

    public VesselNotFound(String oid) {
        super();
        this.orderID = oid;
        this.message = "Vessel not found around pickup area around pickup date for: " + orderID;
    }

    public String toString() {  
            return message;
        
    }
}
