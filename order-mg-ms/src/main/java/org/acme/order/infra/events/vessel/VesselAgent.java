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
 * Listen to vessel assignement topic to process vessel allocation
 */
@ApplicationScoped
public class VesselAgent {
    
    Logger logger = Logger.getLogger(VesselAgent.class.getName());

    @Inject
    OrderRepository repo;

    @Inject
	public OrderEventProducer producer;
    
    @Incoming("vessels")
    public CompletionStage<Void> processVesselEvent(Message<VesselEvent> messageWithVesselEvent){
        logger.info("Received vessel event for : " + messageWithVesselEvent.getPayload().vesselID);
        VesselEvent ve = messageWithVesselEvent.getPayload();
        switch( ve.getEventType()){
            case VesselEvent.TYPE_VESSEL_ASSIGNED:
                processVesselAssignEvent(ve);
                break;
            case VesselEvent.TYPE_VESSEL_NOT_FOUND:
                processVesselNotFound(ve);
            default:
                break;
        }
        return messageWithVesselEvent.ack();
    }

    @Transactional
    public VesselEvent processVesselAssignEvent(VesselEvent ve) {
        VesselAllocated ra = (VesselAllocated)ve.payload;
        ShippingOrder order = repo.findById(ra.orderID);
        if (order != null) {
            order.vesselID = ve.vesselID;     
            if (order.containerIDs != null) {
                order.status = ShippingOrder.ASSIGNED_STATUS;
                producer.sendOrderUpdateEventFrom(order);
            }
            repo.updateOrder(order);
        } else {
            logger.warning(ra.orderID + " not found in repository");
        }
        
        return ve;
    }

    @Transactional
    public void processVesselNotFound(VesselEvent ve){
        VesselNotFound ra = (VesselNotFound)ve.payload;
        logger.info("Order: " + ra.orderID + " " + ra.message );
        ShippingOrder order = repo.findById(ra.orderID);
        if (order != null) {
            order.status = ShippingOrder.ONHOLD_STATUS;
            repo.updateOrder(order);
            producer.sendOrderUpdateEventFrom(order);
        }
        
    }

    @Scheduled(cron = "{vessel.cron.expr}")
    void cronJobForVesselAnswerNotReceived() {
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
