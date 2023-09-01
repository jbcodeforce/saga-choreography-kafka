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
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

/**
 * Listen on orders topic to process OrderEvents such as
 * - order created
 * - order updated
 */
@Singleton
public class OrderAgent {
    Logger logger = Logger.getLogger(OrderAgent.class.getName());

    @Inject
    VesselRepository repository;

    @Inject
    VesselEventProducer eventProducer;

    @Incoming("orders")
    public CompletionStage<Void> processOrder(Message<OrderEvent> messageWithOrderEvent){
        logger.info("OrderAgent - Received order : " + messageWithOrderEvent.getPayload().orderID);
        OrderEvent oe = messageWithOrderEvent.getPayload();
        switch( oe.getEventType()){
            case OrderEvent.ORDER_CREATED_TYPE:
                processOrderCreatedEvent(oe);
                break;
            case OrderEvent.ORDER_UPDATED_TYPE:
                logger.info("Receive order update " + oe.status);
                if (oe.status.equals(OrderEvent.ONHOLD_STATUS)) {
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
    @Transactional
    public VesselEvent processOrderCreatedEvent( OrderEvent oe){
        logger.info("processOrderCreatedEvent ");
        OrderCreatedEvent oce = (OrderCreatedEvent)oe.payload;
        Vessel vessel = repository.getVesselForOrder(oe.orderID, 
                                oce.pickupCity, 
                                oce.destinationCity,
                                oe.quantity);
        VesselEvent ve = null;
        if (vessel == null) {
            // normally do nothing
            logger.info("No vessel found for " + oce.pickupCity);
        } else {
            // TODO add more logic to process other event types 
            VesselAllocated vesselAssignedEvent = new VesselAllocated(oe.orderID,vessel.vesselID,oe.quantity);
            ve = new VesselEvent(vessel.vesselID, VesselEvent.VESSEL_ALLOCATED_TYPE, vesselAssignedEvent);
            vessel.currentFreeCapacity = vessel.currentFreeCapacity - oe.quantity;
            repository.updateVessel(vessel);
            repository.assignVesselToOrder(oe.orderID, vessel);
            eventProducer.sendEvent(ve.vesselID,ve);
        }
        return ve;
    }
 
    @Transactional
    public void compensateOrder(String txid,long capacity) {
        logger.info("Compensate on order " + txid);
        String vesselID = repository.cleanTransaction(txid,capacity);
        // a capacity set to 0, represents removing the order capacity from the vessel's payload.
        VesselAllocated voyageAssignedEvent = new VesselAllocated(txid,vesselID,0);
        VesselEvent ve = new VesselEvent(vesselID, VesselEvent.VESSEL_DESALLOCATED_TYPE, voyageAssignedEvent);
        eventProducer.sendEvent(vesselID, ve);
    }
}
