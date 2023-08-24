package org.acme.order.infra.events.reefer;

import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

import jakarta.inject.Inject;

import org.acme.order.domain.ShippingOrder;
import org.acme.order.infra.events.order.OrderEventProducer;
import org.acme.order.infra.repo.OrderRepository;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Listen to the reefer topic and processes event from reefer service:
 * - reefer allocated event
 * - reefer unavailable event
 */
@ApplicationScoped
public class ReeferAgent {
    Logger logger = Logger.getLogger(ReeferAgent.class.getName());

    @Inject
    OrderRepository repo;

    @Inject
	public OrderEventProducer producer;
    
    @Incoming("reefers")
    public CompletionStage<Void> processReeferEvent(Message<ReeferEvent> messageWithReeferEvent){
        ReeferEvent oe = messageWithReeferEvent.getPayload();
        switch( oe.getType()){
            case ReeferEvent.REEFER_ALLOCATED_TYPE:
                ReeferEvent re=processReeferAllocatedEvent(oe);
                break;
            default:
                break;
        }
        return messageWithReeferEvent.ack();
    }

    /**
     * When order created, search for reefers close to the pickup location,
     * add them in the container ids and send an event as ReeferAllocated
     */
    public ReeferEvent processReeferAllocatedEvent( ReeferEvent re){
        ReeferAllocated ra = (ReeferAllocated)re.payload;
        logger.info("Received reefer event for : " + re.reeferID + " oid:" + ra.orderID);
               
        ShippingOrder order = repo.findById(ra.orderID);
        if (order != null) {
            order.containerID = ra.reeferIDs;
            if (order.vesselID != null) {
                order.status = ShippingOrder.ASSIGNED_STATUS;
                producer.sendOrderUpdateEventFrom(order);
            }
            repo.updateOrder(order);
        } else {
            logger.warning(ra.orderID + " not found in repository");
        }
        
        return re;
    }

    @Scheduled(cron = "{reefer.cron.expr}")
    void cronJobForReeferAnswerNotReceived() {
        // badly done - brute force as of now
        for(ShippingOrder o : repo.getAll()) {
            if (o.status.equals(ShippingOrder.PENDING_STATUS)) {
                if (o.vesselID != null) {
                    o.status = ShippingOrder.ONHOLD_STATUS;
                    producer.sendOrderUpdateEventFrom(o);
                }
            } 
        }
    }
 
}
