package org.acme.reefer.infra.events.reefer;

public class ReeferNotFound extends ReeferVariablePayload {
    public String orderID;
    public String message;

    public ReeferNotFound(String oid){
        this.orderID=oid;
        this.message = "Reefer not found for " + oid;
    }

    public String toString() {  
        return message;
    
    }
}
