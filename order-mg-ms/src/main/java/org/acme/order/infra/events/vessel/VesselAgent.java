package org.acme.order.infra.events.vessel;

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
import jakarta.transaction.Transactional;

/**
 * Listen to vessel assignement topic to process voyage allocation
 */
@ApplicationScoped
public class VesselAgent {
    
    Logger logger = Logger.getLogger(VesselAgent.class.getName());

    @Inject
    OrderRepository repo;

    @Inject
	public OrderEventProducer producer;
    
    @Incoming("vessels")
    public CompletionStage<Void> processVoyageEvent(Message<VesselEvent> messageWithVoyageEvent){
        logger.info("Received voyage event for : " + messageWithVoyageEvent.getPayload().vesselID);
        VesselEvent oe = messageWithVoyageEvent.getPayload();
        switch( oe.getType()){
            case VesselEvent.TYPE_VESSEL_ASSIGNED:
            VesselEvent re=processVoyageAssignEvent(oe);
                break;
            default:
                break;
        }
        return messageWithVoyageEvent.ack();
    }

    @Transactional
    public VesselEvent processVoyageAssignEvent(VesselEvent ve) {
        VesselAllocated ra = (VesselAllocated)ve.payload;
        ShippingOrder order = repo.findById(ra.orderID);
        if (order != null) {
            order.voyageID = ve.vesselID;     
            if (order.containerID != null) {
                order.status = ShippingOrder.ASSIGNED_STATUS;
                producer.sendOrderUpdateEventFrom(order);
            }
            repo.updateOrder(order);
        } else {
            logger.warning(ra.orderID + " not found in repository");
        }
        
        return ve;
    }


    @Scheduled(cron = "{voyage.cron.expr}")
    void cronJobForVoyageAnswerNotReceived() {
        // badly done - brute force as of now
        for(ShippingOrder o : repo.getAll()) {
            if (o.status.equals(ShippingOrder.PENDING_STATUS)) {
                if (o.containerID != null) {
                    o.status = ShippingOrder.ONHOLD_STATUS;
                    producer.sendOrderUpdateEventFrom(o);
                }
            } 
        }
    }
}
