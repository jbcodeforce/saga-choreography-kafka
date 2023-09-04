package org.acme.vessel.infra.api;

import java.util.logging.Logger;

import org.acme.vessel.domain.Vessel;
import org.acme.vessel.domain.VesselService;

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
import jakarta.ws.rs.core.MediaType;


@RequestScoped
@Path("/vessels")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VesselResource {
    private static Logger logger = Logger.getLogger("VesselResource");

    @Inject
    public VesselService service;
    
    @GET
    public Multi<Vessel> getAll() {
        return Multi.createFrom().items(service.getAllVessels().stream());
    }

    @GET
    @Path("/{id}")
    public Uni<Vessel> getVesselById(@PathParam("id") String id) {
        return Uni.createFrom().item(service.getVesselById(id));
    }

    @POST
    public Uni<Vessel> saveNewVessel( Vessel newVessel) {
        logger.info("Save new Vessel " + newVessel.toString());
        return Uni.createFrom().item(service.saveVessel(newVessel));
    }

    @PUT
    public Uni<Vessel> updateVessel( Vessel newVessel) {
        logger.info("Update Vessel " + newVessel.toString());
        return Uni.createFrom().item(service.updateVessel(newVessel));
    }


}
