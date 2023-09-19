package org.acme.order.infra.events.vessel;

import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

import org.acme.order.domain.ShippingOrder;
import org.acme.order.infra.events.order.OrderEventProducer;
import org.acme.order.infra.repo.OrderRepository;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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
        VesselAllocated vesselEvent = (VesselAllocated)ve.payload;
        repo.findById(vesselEvent.orderID)
        .onFailure().invoke(failure -> logger.warning(vesselEvent.orderID + " not found in repository"))
        .onItem().invoke( order -> {
            logger.info("Order: " + vesselEvent.orderID + " found let modify its vessel" );
            order.vesselID = ve.vesselID;     
            if (order.containerIDs != null) {
                order.status = ShippingOrder.ASSIGNED_STATUS;
                producer.sendOrderUpdateEventFrom(order);
            }
            repo.updateOrder(order);
            });
        
        return ve;
    }

    @Transactional
    public void processVesselNotFound(VesselEvent ve){
        VesselNotFound vesselEvent = (VesselNotFound)ve.payload;
        logger.info("Order: " + vesselEvent.orderID + " " + vesselEvent.message );
        Uni<ShippingOrder> order = repo.findById(vesselEvent.orderID)
        .onFailure().invoke(f -> logger.severe("order " + vesselEvent.orderID + " not found in repository"))
        .onItem().invoke( o -> {
            logger.info("order to process");
            o.status = ShippingOrder.ONHOLD_STATUS;
        });
        order.subscribe().with(o -> {repo.updateOrder(o);
        producer.sendOrderUpdateEventFrom(o);});
    }

    @Scheduled(cron = "{vessel.cron.expr}")
    void cronJobForVesselAnswerNotReceived() {
        // badly done - brute force as of now
        Iterable<ShippingOrder> orders = repo.getAll().subscribe().asIterable();
        for(ShippingOrder o : orders) {
            if (o.status.equals(ShippingOrder.PENDING_STATUS)) {
                if (o.vesselID != null) {
                    o.status = ShippingOrder.ONHOLD_STATUS;
                    producer.sendOrderUpdateEventFrom(o);
                }
            } 
        }
    }
}
