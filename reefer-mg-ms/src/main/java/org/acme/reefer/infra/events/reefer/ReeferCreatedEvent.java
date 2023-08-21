package org.acme.reefer.infra.events.reefer;

import org.acme.reefer.domain.Reefer;

public class ReeferCreatedEvent extends ReeferVariablePayload {
    String reeferID;
    String location;
    String type;
    long capacity;

    public ReeferCreatedEvent(Reefer r) {
        reeferID = r.reeferID;
        location = r.location;
        type = r.type;
        capacity = r.capacity;
    }

   

}
