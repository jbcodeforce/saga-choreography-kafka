package org.acme.vessel.infra.events.orders;

import java.util.Date;

import org.acme.vessel.infra.events.EventBase;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Order event for the state change of a shipping order
 * 
 * Events are data element, so limit inheritance and polymorphism to the minimum
 * 
 * @author jeromeboyer
 *
 */
@RegisterForReflection
public class OrderEvent extends EventBase {
    public static final String ORDER_CREATED_TYPE = "OrderCreated";
    public static final String ORDER_UPDATED_TYPE = "OrderUpdated";
    public static final String ORDER_REJECTED_TYPE = "OrderRejected";
    public static final String ORDER_CANCELLED_TYPE = "OrderCancelled";
    public static final String ORDER_ON_HOLD_TYPE = "OrderOnHold";
    public String orderID;
    public String productID;
    public String customerID;
    public int quantity;
    public String status;
	
    public OrderVariablePayload  payload;

    public OrderEvent() {
    }

    public OrderEvent( String aType, OrderVariablePayload payload) {
        this.payload = payload;
        this.type = aType;
        this.timestampMillis = new Date().getTime();
        this.version = DEFAULT_VERSION;
    }
}
