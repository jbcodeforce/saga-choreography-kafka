package org.acme.vessel.infra.events.vessels;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VesselEventProducer {

    Logger logger = Logger.getLogger(VesselEventProducer.class.getName());
    
    @Channel("vessels")
    public Emitter<VesselEvent> eventProducer;

    public void sendEvent(String key, VesselEvent vesselEvent){
		logger.info("Send vessel message --> " + vesselEvent.vesselID + " type:" + vesselEvent.getEventType() + " ts: " + vesselEvent.getTimestampMillis());
		eventProducer.send(Message.of(vesselEvent).addMetadata(OutgoingKafkaRecordMetadata.<String>builder()
			.withKey(key).build())
			.withAck( () -> {
				
				return CompletableFuture.completedFuture(null);
			})
			.withNack( throwable -> {
				return CompletableFuture.completedFuture(null);
			}));
	}
}
