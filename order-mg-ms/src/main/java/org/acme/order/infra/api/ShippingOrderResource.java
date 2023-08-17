package org.acme.order.infra.api;

import java.util.List;

import jakarta.inject.Inject;

import org.acme.order.domain.OrderService;
import org.acme.order.domain.ShippingOrder;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;

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
    public ShippingOrder createOrder(ShippingOrder order) {
        return serv.createOrder(order);
    }

    @GET
    public List<ShippingOrder> getAll() {
        return serv.getAllOrders();
    }

    @GET
    @Path("/{id}")
    public ShippingOrder getOrderByID(@PathParam("id") String id) {
        ShippingOrder order = serv.getOrderById(id);
        if (order == null) {
            throw new WebApplicationException("Order with id of " + id + " does not exist.", 404);
     
        }
        return order;
    }

    @PUT
    @Path("/cancel/{id}")
    public ShippingOrder cancelOrderByID(@PathParam("id") String id) {
        ShippingOrder order = serv.getOrderById(id);
        if (order == null) {
            throw new WebApplicationException("Order with id of " + id + " does not exist.", 404);
     
        }
        serv.cancelOrder(order);
        return order;
    }

    @PUT
    @Path("/{id}")
    public ShippingOrder updateShippingOrder(@PathParam("id") String id, ShippingOrder order) {
        ShippingOrder orderOrigin = serv.getOrderById(id);
        if (orderOrigin == null) {
            throw new WebApplicationException("Order with id of " + id + " does not exist.", 404);
        }
        order.status=orderOrigin.status; // hack from now.
        serv.updateOrder(order);
        return order;
    }
}
