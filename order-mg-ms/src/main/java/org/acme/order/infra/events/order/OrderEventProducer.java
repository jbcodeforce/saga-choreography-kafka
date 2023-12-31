package org.acme.order.infra.events.order;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import org.acme.order.domain.ShippingOrder;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderEventProducer {
    Logger logger = Logger.getLogger(OrderEventProducer.class.getName());
    
    @Channel("orders")
	public Emitter<OrderEvent> eventProducer;

    public void sendOrderCreatedEventFrom(ShippingOrder order) {
        OrderEvent oe = createOrderEvent(order);
        oe.eventType = OrderEvent.ORDER_CREATED_TYPE;
        OrderCreatedEvent oce = new OrderCreatedEvent(order.getDestinationAddress().getCity(),
                                    order.getPickupAddress().getCity(),
                                    order.pickupDate,
                                    order.quantity);
		oe.payload = oce;
        sendOrder(oe.orderID,oe);
    }

    public void sendOrderUpdateEventFrom(ShippingOrder order) {
        OrderEvent oe = createOrderEvent(order);
        oe.eventType = OrderEvent.ORDER_UPDATED_TYPE;
        oe.status = order.getStatus();
        oe.quantity = order.getQuantity();
        OrderUpdatedEvent oue = new OrderUpdatedEvent();
        oue.reeferIDs = order.containerIDs;
        oue.vesselID = order.vesselID;
        oue.destinationCity=order.getDestinationAddress().getCity();
        oue.pickupCity=order.getPickupAddress().getCity();
        oue.pickupDate=order.getPickupDate();
        oue.expectedCapacity=order.getQuantity();
		oe.payload = oue;
        sendOrder(oe.orderID,oe);
    }


    public void sendOrder(String key, OrderEvent orderEvent){
        logger.info("key " + key + " order event " + orderEvent.orderID 
                    + " etype:" + orderEvent.eventType 
                    + " status:" + orderEvent.status
                    + " ts: " + orderEvent.timestampMillis);
		eventProducer.send(Message.of(orderEvent).addMetadata(OutgoingKafkaRecordMetadata.<String>builder()
			.withKey(key).build())
			.withAck( () -> {
				
				return CompletableFuture.completedFuture(null);
			})
			.withNack( throwable -> {
				return CompletableFuture.completedFuture(null);
			}));
	}

    private OrderEvent createOrderEvent(ShippingOrder order){
        OrderEvent oe = new OrderEvent();
        oe.customerID = order.customerID;
        oe.orderID = order.orderID;
        oe.productID = order.productID;
        oe.quantity = order.quantity;
        oe.status = order.status;
        return oe;

    }
}
