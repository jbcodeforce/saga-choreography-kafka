package org.acme.vessel.infra.events.orders;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class OrderCreatedEvent extends OrderVariablePayload {
    public String destinationCity;
	public String pickupCity;
	public String creationDate;

    public OrderCreatedEvent(){}

	public OrderCreatedEvent(String destinationCity, String pickupCity) {
		this.destinationCity = destinationCity;
		this.pickupCity = pickupCity;
		
	}	
}
