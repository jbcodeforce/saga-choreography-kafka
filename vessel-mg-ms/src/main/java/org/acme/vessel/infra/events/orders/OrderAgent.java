package org.acme.vessel.infra.events.orders;

import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

import org.acme.vessel.domain.Vessel;
import org.acme.vessel.infra.events.vessels.VesselAllocated;
import org.acme.vessel.infra.events.vessels.VesselEvent;
import org.acme.vessel.infra.events.vessels.VesselEventProducer;
import org.acme.vessel.infra.repo.VesselRepository;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import jakarta.inject.Inject;

/**
 * Listen on orders topic to process OrderEvents such as
 * - order created
 * - order updated
 */
public class OrderAgent {
    Logger logger = Logger.getLogger(OrderAgent.class.getName());

    @Inject
    VesselRepository repository;

    @Inject
    VesselEventProducer eventProducer;

    @Incoming("orders")
    public CompletionStage<Void> processOrder(Message<OrderEvent> messageWithOrderEvent){
        logger.info("Received order : " + messageWithOrderEvent.getPayload().orderID);
        OrderEvent oe = messageWithOrderEvent.getPayload();
        switch( oe.getType()){
            case OrderEvent.ORDER_CREATED_TYPE:
                processOrderCreatedEvent(oe);
                break;
            case OrderEvent.ORDER_UPDATED_TYPE:
                logger.info("Receive order update " + oe.status);
                if (oe.status.equals(OrderEvent.ORDER_ON_HOLD_TYPE)) {
                    compensateOrder(oe.orderID,oe.quantity);
                } else {
                    logger.info("Do future processing in case of order update");
                }
                    
                break;
            default:
                break;
        }
        return messageWithOrderEvent.ack();
    }

     /**
     * When order created, search for a vessel trip close to the pickup location, and a distination close
     * for a given date
     */
    public VesselEvent processOrderCreatedEvent( OrderEvent oe){
        OrderCreatedEvent oce = (OrderCreatedEvent)oe.payload;
        Vessel vessel = repository.getVesselForOrder(oe.orderID, 
                                oce.pickupCity, 
                                oce.destinationCity,
                                oe.quantity);
        VesselEvent ve = new VesselEvent();
        if (vessel == null) {
            // normally do nothing
            logger.info("No vessel found for " + oce.pickupCity);
        } else {
            VesselAllocated voyageAssignedEvent = new VesselAllocated(oe.orderID);
            ve.vesselID = vessel.vesselID;
            ve.setType(VesselEvent.TYPE_VESSEL_ASSIGNED);
            ve.payload = voyageAssignedEvent;
           
            eventProducer.sendEvent(ve.vesselID,ve);
        }
        return ve;
    }
 
    public void compensateOrder(String txid,long capacity) {
        logger.info("Compensate on order " + txid);
        repository.cleanTransaction(txid,capacity);
    }
}
