package org.acme.order.infra.events.order;

import java.time.LocalDate;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class OrderUpdatedEvent extends OrderVariablePayload  {
    public String vesselID;
    public String reeferIDs;
    public String updateDate;
    public String destinationCity;
	public String pickupCity;
    public String pickupDate;
    public long expectedCapacity;

    public OrderUpdatedEvent(){
        updateDate = LocalDate.now().toString();
    }

    public String toString(){
        return "Vessel:" + vesselID + " reeferIDs:" + reeferIDs + " quantity:" + expectedCapacity;
    }
}
