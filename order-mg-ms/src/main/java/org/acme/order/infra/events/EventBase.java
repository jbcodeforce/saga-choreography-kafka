package org.acme.order.infra.events;

/**
 * This is the common part of the order events. 
 * Events are data element, so limit inheritance and polymorphism.
 * @author jerome boyer
 *
 */
public class EventBase {

     public static final String DEFAULT_VERSION = "1.0.0";
     public static final String TYPE_VESSEL_ASSIGNED = "VesselAssigned"; // from vessel ms
     public static final String TYPE_VESSEL_NOT_FOUND = "VesselNotFound"; // from vessel ms

	    
    public long timestampMillis;
    public String eventType;
    public String version;

    public EventBase() {
    }

    public EventBase(long timestampMillis, String type, String version) {
        this.timestampMillis = timestampMillis;
        this.eventType = type;
        this.version = version;
    }
    
    
    
    public long getTimestampMillis() {
        return timestampMillis;
    }

    
    public void setTimestampMillis(long timestampMillis) {
        this.timestampMillis = timestampMillis;
    }

    
    public String getEventType() {
        return eventType;
    }

    
    public void setEventType(String type) {
        this.eventType = type;
    }

    
    public void setVersion(String version) {
        this.version = version;
    }

    
    public String getVersion() {
        return version;
    }
}
