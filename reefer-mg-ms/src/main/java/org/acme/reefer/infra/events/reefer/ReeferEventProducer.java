package org.acme.reefer.infra.events.reefer;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;

import org.acme.reefer.domain.Reefer;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

@ApplicationScoped
public class ReeferEventProducer {
	Logger logger = Logger.getLogger(ReeferEventProducer.class.getName());
    @Channel("reefers")
	public Emitter<ReeferEvent> reeferEventProducer;


    public void sendEvent(String key, ReeferEvent reeferEvent){
		logger.info("Send event -> " + reeferEvent.reeferID + " type of " + reeferEvent.getEventType() +  " ts:" + reeferEvent.getTimestampMillis());
		reeferEventProducer.send(Message.of(reeferEvent).addMetadata(OutgoingKafkaRecordMetadata.<String>builder()
			.withKey(key).build())
			.withAck( () -> {
				
				return CompletableFuture.completedFuture(null);
			})
			.withNack( throwable -> {
				return CompletableFuture.completedFuture(null);
			}));
	}


    public void sendReeferCreatedEventFrom(Reefer r) {
		ReeferCreatedEvent rce = new ReeferCreatedEvent(r);
		ReeferEvent re = new ReeferEvent(rce.reeferID,ReeferEvent.NEW_REEFER_TYPE, rce);
		sendEvent(rce.reeferID, re);
    }

}
