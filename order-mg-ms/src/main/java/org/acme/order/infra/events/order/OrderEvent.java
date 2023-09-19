package org.acme.order.infra.events.order;

import java.util.Date;

import org.acme.order.infra.events.EventBase;

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


    public static final String PENDING_STATUS = "pending";
    public static final String CANCELLED_STATUS = "cancelled";
    public static final String ONHOLD_STATUS = "onHold";
    public static final String ASSIGNED_STATUS = "assigned";
    public static final String REJECTED_STATUS = "rejected";
    public static final String COMPLETED_STATUS = "completed";


    public String orderID;
    public String productID;
    public String customerID;
    public int quantity;
    public String status;
	
    public OrderVariablePayload  payload;

    public OrderEvent(long timestampMillis, 
                String type, 
                String version, 
                OrderVariablePayload payload) {
        super(timestampMillis, type, version);
        this.payload = payload;
    }

    public OrderEvent( String aType, OrderVariablePayload payload) {
        this.payload = payload;
        this.eventType = aType;
        this.timestampMillis = new Date().getTime();
        this.version = DEFAULT_VERSION;
    }

    public OrderEvent(){
        this.timestampMillis = new Date().getTime();
        this.version = DEFAULT_VERSION;
    }

    public String toString(){
        return "OrderEvent: "+this.eventType+" status: " + this.status+" "+this.orderID+" "+this.productID+" "+this.customerID+" "+this.timestampMillis;
    }

}
