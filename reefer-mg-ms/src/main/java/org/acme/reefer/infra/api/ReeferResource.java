package org.acme.reefer.infra.api;

import java.util.List;
import java.util.logging.Logger;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.acme.reefer.domain.Reefer;
import  org.acme.reefer.domain.ReeferService;
import  org.acme.reefer.infra.api.dto.OrderDTO;


@RequestScoped
@Path("/reefers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReeferResource {
    private static Logger logger = Logger.getLogger("FreezerResource");

    @Inject
    public ReeferService service;
    
    @GET
    public List<Reefer> getAll() {
        return service.getAllReefers();
    }

    @GET
    @Path("/transaction/{txid}")
    public List<Reefer> getAllFreezerForAtransaction(@PathParam("txid") String txid) {
        return service.getAllReefersForTransaction(txid);
    }

    @GET
    @Path("/{reeferId}")
    public Reefer getReeferById(@PathParam("reeferId") String reeferId) {
        return service.getReeferById(reeferId);
    }

    @POST
    public Reefer saveNewFreezer( Reefer newFreezer) {
        logger.info("Save new freezer " + newFreezer.toString());
        return service.saveReefer(newFreezer);
    }

    @PUT
    public Reefer updateFreezer( Reefer newFreezer) {
        logger.info("Update freezer " + newFreezer.toString());
        return service.updateFreezer(newFreezer);
    }

    @POST
    @Path("/assignOrder")
    public Response processOrder( OrderDTO order) {
        logger.info("processOrder " + order.toString());
        // for demo purpose!
        if (order.destinationCity.equals("ABadDestination")) {
            return Response.serverError().build();
        }
        OrderDTO updatedOrder = service.computeBestFreezerToShip(order);
        return Response.ok().entity(updatedOrder).build();
    }

    
    @Path("/compensateOrder")
    @PUT
    public Response compensateOrder(OrderDTO order) {
        OrderDTO updatedOrder = service.compensate(order);
        return Response.ok().entity(updatedOrder).build();
    }

}
