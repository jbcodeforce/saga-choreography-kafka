package org.acme.order.infra.api;

import org.acme.order.domain.OrderService;
import org.acme.order.domain.ShippingOrder;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;

import jakarta.ws.rs.core.MediaType;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;

@RequestScoped
@Path("/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ShippingOrderResource {
    
    @Inject
    OrderService serv;

    @POST
    @APIResponse(
        responseCode = "404",
        description = "Missing description",
        content = @Content(mediaType = "application/json"))
    @APIResponseSchema(value = ShippingOrder.class,
        responseDescription = "Create order with UUID",
        responseCode = "200")
    @Operation(
            summary = "Get JVM system properties for particular host",
            description = "Retrieves and returns the JVM system properties from the system "
            + "service running on the particular host.")
    public Uni<ShippingOrder> createOrder(ShippingOrder order) {
        return serv.createOrder(order);
    }

    @GET
    public Multi<ShippingOrder> getAll() {
        return serv.getAllOrders();
    }

    @GET
    @Path("/{id}")
    public Uni<ShippingOrder> getOrderByID(@PathParam("id") String id) {
        return serv.getOrderById(id);
    }

    @PUT
    @Path("/cancel/{id}")
    public Uni<ShippingOrder> cancelOrderByID(@PathParam("id") String id) {
         return serv.getOrderById(id)
        .onFailure().invoke(failure -> {new WebApplicationException("Order with id of " + id + " does not exist.", 404);})
        .onItem().invoke(i -> serv.cancelOrder(i));
      
    }

    @PUT
    @Path("/{id}")
    public Uni<ShippingOrder> updateShippingOrder(@PathParam("id") String id, ShippingOrder order) {
        return serv.getOrderById(id)
        .onFailure().invoke(failure -> {new WebApplicationException("Order with id of " + id + " does not exist.", 404);})
        .onItem().invoke( i-> {
                        order.status=i.status; 
                        serv.updateOrder(order);});
    
    }
}
