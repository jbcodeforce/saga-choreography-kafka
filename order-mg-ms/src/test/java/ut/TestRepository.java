package ut;

import java.time.Duration;

import org.acme.order.domain.ShippingOrder;
import org.acme.order.infra.repo.OrderRepository;
import org.acme.order.infra.repo.OrderRepositoryMem;
import org.junit.jupiter.api.Test;
import org.wildfly.common.Assert;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.Multi;

@QuarkusTest
public class TestRepository {
    public static OrderRepository repo  = new OrderRepositoryMem();
   

    @Test
    public void shouldGetPredefinedOrder() {
        Uni<ShippingOrder> o = repo.findById("Order01");
        ShippingOrder so = o.await().atMost(Duration.ofSeconds(1));
        Assert.assertNotNull(so);
        Assert.assertTrue("P01".equals(so.productID));
    }

    @Test
    public void shouldHaveMultipleOrders() {
        Multi<ShippingOrder> orders = repo.getAll();

        // cannot have two subscriber orders.subscribe().with(System.out::println); 
        Iterable<ShippingOrder> iso = orders.subscribe().asIterable();
        Assert.assertNotNull(iso);
        iso.forEach( so -> System.out.println(so.productID));

    }

    @Test
    public void shouldProcessOrder() {
        Uni<ShippingOrder> o = repo.findById("Order01")
        .onFailure().invoke(f -> System.out.println("order not found in repository"))
        .onItem().invoke( order -> {
            System.out.println("order to process");
            order.status = ShippingOrder.ONHOLD_STATUS;
        });
        o.subscribe().with(order -> {
            System.out.println("order processed " + order.status);
         
        });

    }


}
