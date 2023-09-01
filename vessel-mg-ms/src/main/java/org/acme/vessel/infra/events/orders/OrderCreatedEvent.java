package org.acme.vessel.infra.events.orders;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class OrderCreatedEvent extends OrderVariablePayload {
    public String destinationCity;
	public String pickupCity;
	public long expectedCapacity;
	public String creationDate;

    public OrderCreatedEvent(){}

	public OrderCreatedEvent(String pickupCity , String destinationCity, long expectedCapacity) {
		this.destinationCity = destinationCity;
		this.pickupCity = pickupCity;
		this.expectedCapacity = expectedCapacity;	
	}	
}
