package org.acme.vessel.infra.api;

import java.util.List;
import java.util.logging.Logger;

import org.acme.vessel.domain.Vessel;
import org.acme.vessel.domain.VesselService;

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
    public List<Vessel> getAll() {
        return service.getAllVessels();
    }

    @GET
    @Path("/{id}")
    public Vessel getVesselById(@PathParam("id") String id) {
        return service.getVesselById(id);
    }

    @POST
    public Vessel saveNewVessel( Vessel newVessel) {
        logger.info("Save new Vessel " + newVessel.toString());
        return service.saveVessel(newVessel);
    }

    @PUT
    public Vessel updateVessel( Vessel newVessel) {
        logger.info("Update Vessel " + newVessel.toString());
        return service.updateVessel(newVessel);
    }


}
