package org.acme.order.infra.repo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.acme.order.domain.ShippingOrder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Singleton;

@Singleton
public class OrderRepositoryMem implements OrderRepository {
    Logger logger = Logger.getLogger(OrderRepositoryMem.class.getName());
    private static ConcurrentHashMap<String,ShippingOrder> repo = new ConcurrentHashMap<String,ShippingOrder>();

    private static ObjectMapper mapper = new ObjectMapper();
    

    public OrderRepositoryMem() {
        super();
        InputStream is = getClass().getClassLoader().getResourceAsStream("orders.json");
        if (is == null) 
            throw new IllegalAccessError("file not found for order json");
        try {
            List<ShippingOrder> currentDefinitions = mapper.readValue(is, mapper.getTypeFactory().constructCollectionType(List.class, ShippingOrder.class));
            currentDefinitions.stream().forEach( (t) -> repo.put(t.getOrderID(),t));
        } catch (IOException e) {
            e.printStackTrace();
        }
        repo.values().stream().forEach(v -> System.out.println(v.toString()));
    }

    public Multi<ShippingOrder> getAll(){
        List<ShippingOrder> allItems = new ArrayList<ShippingOrder>(repo.values());
        return Multi.createFrom().items(allItems.stream());
    }

    public void addOrder(ShippingOrder entity) {
        logger.info("Save in repository " + entity.orderID);
        repo.put(entity.getOrderID(), entity);
    }

    public void updateOrder(ShippingOrder entity) {
        repo.put(entity.getOrderID(), entity);
    }

    @Override
    public Uni<ShippingOrder> findById(String key) {
        ShippingOrder o = repo.get(key);
       logger.info(o.toString());
        return Uni.createFrom().item(o);
    }
}
