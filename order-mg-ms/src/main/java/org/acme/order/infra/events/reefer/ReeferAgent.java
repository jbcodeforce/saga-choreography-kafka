package org.acme.order.infra.events.reefer;

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
        switch( oe.getEventType()){
            case ReeferEvent.REEFER_ALLOCATED_TYPE:
                processReeferAllocatedEvent(oe);
                break;
            case ReeferEvent.REEFER_NOT_FOUND_TYPE:
                processReeferNotFound(oe);
                break;
            default:
                break;
        }
        return messageWithReeferEvent.ack();
    }

   
    /**
     * The order can have one to many containers allocated.
     */
    @Transactional
    public ReeferEvent processReeferAllocatedEvent( ReeferEvent re){
        ReeferAllocated reeferEvent = (ReeferAllocated)re.payload;
        logger.info("Received reefer allocated event for : " + re.reeferID + " oid:" + reeferEvent.orderID);
        
        Uni<ShippingOrder> so = repo.findById(reeferEvent.orderID)
        .onFailure().invoke(failure -> logger.warning(reeferEvent.orderID + " not found in repository"))
        .onItem().invoke( order -> {
            logger.info("Order: " + reeferEvent.orderID + " found let modify its reefer" );  
            order.containerIDs = reeferEvent.reeferIDs;
            if (order.vesselID != null) {
                order.status = ShippingOrder.ASSIGNED_STATUS;
                
            }
            
        });
        so.subscribe().with( o -> {
            producer.sendOrderUpdateEventFrom(o);
            repo.updateOrder(o);
        });
        return re;
    }

    @Transactional
    private void processReeferNotFound(ReeferEvent oe) {
        ReeferNotFound reeferEvent = (ReeferNotFound)oe.payload;
        logger.info("Received reefer not found event for : " + reeferEvent.message);
        
        Uni<ShippingOrder> so= repo.findById(reeferEvent.orderID)
        .onFailure().invoke(failure -> logger.warning(reeferEvent.orderID + " not found in repository"))
        .onItem().invoke( order -> {
            order.status = ShippingOrder.ONHOLD_STATUS;
        });
        so.subscribe().with( o -> {
            producer.sendOrderUpdateEventFrom(o);
            repo.updateOrder(o);
        });
    }


    @Scheduled(cron = "{reefer.cron.expr}")
    void cronJobForReeferAnswerNotReceived() {
        // badly done - brute force as of now
        Iterable<ShippingOrder> orders = repo.getAll().subscribe().asIterable();
        for(ShippingOrder o : orders) {
            if (o.status.equals(ShippingOrder.PENDING_STATUS)) {
                if (o.containerIDs != null) {
                    o.status = ShippingOrder.ONHOLD_STATUS;
                    producer.sendOrderUpdateEventFrom(o);
                }
            } 
        }
    }
 
}
