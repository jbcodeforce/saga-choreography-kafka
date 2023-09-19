package org.acme.order.infra.events.reefer;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ReeferNotFound extends ReeferVariablePayload {
    public String orderID;
    public String message;

    public ReeferNotFound(){}

    public String toString() {  
        return "OrderID:" + orderID + " no Reefer found";
    
    }
}
