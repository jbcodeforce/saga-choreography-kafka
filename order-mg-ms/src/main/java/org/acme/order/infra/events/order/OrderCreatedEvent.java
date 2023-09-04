package org.acme.order.infra.events.order;

import java.util.Date;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class OrderCreatedEvent extends OrderVariablePayload {
    public String destinationCity;
	public String pickupCity;
    public String pickupDate;
    public long expectedCapacity;
	public String creationDate;

    public OrderCreatedEvent(){}

    public OrderCreatedEvent(String destinationCity, String pickupCity, String pickupDate, long expectedCapacity) {
        this.destinationCity = destinationCity;
        this.pickupCity = pickupCity;
        this.pickupDate = pickupDate;
        this.creationDate = new Date().toString();
        this.expectedCapacity = expectedCapacity;
    }

    public String toString(){
        return "From:" + pickupCity  + " to:" + destinationCity + " quantity:" + expectedCapacity;
    }
}
