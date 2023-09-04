package org.acme.vessel.infra.events.vessels;

public class VesselNotFound extends VesselVariablePayload{
    public String orderID;
    public String message;

    public VesselNotFound(String oid) {
        super();
        orderID = oid;
        message = "Vessel not found around pickup area around pickup date.";
    }

    public String toString() {  
            return "OrderID:" + orderID + " no Vessel found";
        
    }
}
