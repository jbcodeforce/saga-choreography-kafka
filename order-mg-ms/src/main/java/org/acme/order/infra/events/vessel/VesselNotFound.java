package org.acme.order.infra.events.vessel;

public class VesselNotFound extends VesselVariablePayload{
    public String orderID;
    public String message;

    public VesselNotFound() {
    }

    public String toString() {  
            return "OrderID:" + orderID + " no Vessel found";
        
    }
}
