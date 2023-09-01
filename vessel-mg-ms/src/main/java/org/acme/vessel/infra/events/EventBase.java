package org.acme.vessel.infra.events;

/**
 * This is the common part of the order events. 
 * Events are data element, so limit inheritance and polymorphism.
 * @author jerome boyer
 *
 */
public class EventBase {

    public static final String DEFAULT_VERSION = "1.0.0";


    protected long timestampMillis;
    protected String eventType;
    protected String version;

    public EventBase() {
    }

    public EventBase(String type, long timestampMillis,  String version) {
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
