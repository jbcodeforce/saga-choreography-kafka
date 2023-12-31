package org.acme.reefer.infra.events.reefer;

import java.util.Date;

import org.acme.reefer.infra.events.EventBase;
import org.apache.kafka.common.protocol.types.Field.Str;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ReeferEvent extends EventBase {
    public static final String REEFER_ALLOCATED_TYPE = "ReeferAllocated";
    public static final String NEW_REEFER_TYPE = "NewReeferCreated";
    public static final String REEFER_UPDATE_TYPE = "ReeferUpdated";
    public static final String REEFER_NOT_FOUND_TYPE = "ReeferNotFound";
    
	public String reeferID;
    public ReeferVariablePayload payload;

    public ReeferEvent(long timestampMillis, 
                String id,
                String type, 
                String version, 
                ReeferVariablePayload payload) {
        super(timestampMillis, type, version);
        this.reeferID = id;
        this.payload = payload;
    }

    public ReeferEvent( String id, String aType, ReeferVariablePayload payload) {
        this.payload = payload;
        this.eventType = aType;
        this.reeferID = id;
        this.timestampMillis = new Date().getTime();
        this.version = DEFAULT_VERSION;
    }

    public ReeferEvent(){
        this.timestampMillis = new Date().getTime();
        this.version = DEFAULT_VERSION;
    }

    public String toString(){
        return "ReeferEvent: " + reeferID + " of " + eventType;
    }
}
