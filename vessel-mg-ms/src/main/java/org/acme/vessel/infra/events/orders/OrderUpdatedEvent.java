package org.acme.vessel.infra.events.orders;

import java.time.LocalDate;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class OrderUpdatedEvent extends OrderVariablePayload  {
    public String vesselID;
    public String orderID;
    public String reeferIDs;
    public String updateDate;

    public OrderUpdatedEvent(){
        this.updateDate = LocalDate.now().toString();
    }

    public OrderUpdatedEvent(String vesselID, String orderID) {
        this.vesselID = vesselID;
        this.orderID = orderID;
        this.updateDate = LocalDate.now().toString();
    }

    
}
