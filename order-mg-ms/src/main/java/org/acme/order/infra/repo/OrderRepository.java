package org.acme.order.infra.repo;

import java.util.List;

import org.acme.order.domain.ShippingOrder;

public interface OrderRepository {
    public List<ShippingOrder> getAll();
    public void addOrder(ShippingOrder entity);
    public void updateOrder(ShippingOrder entity);
    public ShippingOrder findById(String key);
}
