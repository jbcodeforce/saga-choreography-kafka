package org.acme.order.domain;

import java.time.LocalDate;
import java.util.UUID;
import java.util.logging.Logger;

import org.acme.order.infra.events.order.OrderEventProducer;
import org.acme.order.infra.repo.OrderRepository;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;



/**
 * main service for the order management. Produce events and use persistence to keep orders
 * created via the APIs.
 */
@ApplicationScoped
public class OrderService {
    Logger logger = Logger.getLogger(OrderService.class.getName());
    @Inject
	public OrderRepository repository;

	@Inject
	public OrderEventProducer producer;

    public Multi<ShippingOrder> getAllOrders() {
		return repository.getAll();
	}

    // some level of integrity between saving to the repository and sending to the messaging via reactive messaging
    @Transactional
    public Uni<ShippingOrder> createOrder(ShippingOrder order) {
        if (order.orderID == null) {
            order.orderID = UUID.randomUUID().toString();
        }
        if (order.creationDate == null) {
			order.creationDate = LocalDate.now().toString();
		}
        order.status = ShippingOrder.PENDING_STATUS;
		order.updateDate= order.creationDate;
        logger.info("create order for " + order.orderID);
        repository.addOrder(order);
		producer.sendOrderCreatedEventFrom(order); 
        return Uni.createFrom().item(order);
    }


    public Uni<ShippingOrder> getOrderById(String id) {
        return repository.findById(id);
    }


    @Transactional
    public void cancelOrder(ShippingOrder order) {
        order.status = ShippingOrder.CANCELLED_STATUS;
        producer.sendOrderUpdateEventFrom(order);
        repository.updateOrder(order);
    }

    @Transactional
    public void updateOrder(ShippingOrder order) {
        producer.sendOrderUpdateEventFrom(order);
        repository.updateOrder(order);
    }
}
