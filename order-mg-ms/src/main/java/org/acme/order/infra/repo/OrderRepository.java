package org.acme.order.infra.repo;

import org.acme.order.domain.ShippingOrder;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public interface OrderRepository {
    public Multi<ShippingOrder> getAll();
    public void addOrder(ShippingOrder entity);
    public void updateOrder(ShippingOrder entity);
    public Uni<ShippingOrder> findById(String key);
}
