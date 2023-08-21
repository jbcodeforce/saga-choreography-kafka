package org.acme.reefer.infra.events.order;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

import org.acme.reefer.domain.Reefer;
import org.acme.reefer.infra.events.reefer.ReeferAllocated;
import org.acme.reefer.infra.events.reefer.ReeferEvent;
import org.acme.reefer.infra.events.reefer.ReeferEventProducer;
import org.acme.reefer.infra.repo.ReeferRepository;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Listen to the orders topic and processes event from order service:
 * - order created event
 * - order updated event
 */
@ApplicationScoped
public class OrderAgent {
    Logger logger = Logger.getLogger(OrderAgent.class.getName());

    @Inject
    ReeferRepository repo;

    @Inject
    ReeferEventProducer reeferEventProducer;

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
                // todo only compensate if cancelled
                compensateOrder(oe.orderID);
                break;
            default:
                break;
        }
        return messageWithOrderEvent.ack();
    }

    /**
     * When order created, search for reefers close to the pickup location,
     * add them in the container ids and send an event as ReeferAllocated
     */
    public ReeferEvent processOrderCreatedEvent( OrderEvent oe){
        OrderCreatedEvent oce = (OrderCreatedEvent)oe.payload;
        List<Reefer> reefers = repo.getReefersForOrder(oe.orderID, 
                                oce.pickupCity, 
                                oe.quantity);
        if (reefers.size() > 0) {
            ReeferAllocated reeferAllocatedEvent = new ReeferAllocated(reefers,oe.orderID);
            ReeferEvent re = new ReeferEvent(reeferAllocatedEvent.reeferIDs,ReeferEvent.REEFER_ALLOCATED_TYPE,reeferAllocatedEvent);   
            reeferEventProducer.sendEvent(re.reeferID,re);  
            return re;
        }
        return null;
    }
 
    public void compensateOrder(String oid) {
        repo.cleanTransaction(oid);
    }
}
