package org.acme.vessel.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.acme.vessel.infra.events.vessels.VesselCreatedEvent;
import org.acme.vessel.infra.events.vessels.VesselEvent;
import org.acme.vessel.infra.events.vessels.VesselEventProducer;
import org.acme.vessel.infra.repo.VesselRepository;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class VesselService {
    private static Logger logger = Logger.getLogger("VesselService");

    @Inject
    public VesselRepository repository;

    @Inject
    public VesselEventProducer eventProducer;

    public VesselService(VesselRepository repo) {
        this.repository = repo;
    }

    public Vessel getVesselById(String id) {
        return repository.getById(id);
    }

  
    public List<Vessel> getAllVessels() {
        return repository.getAll();
    }

    @Transactional
    public Vessel saveVessel(Vessel r){
        if (r.vesselID == null) {
            r.vesselID = UUID.randomUUID().toString();
        }
        if (r.creationDate == null) {
			r.creationDate = LocalDate.now().toString();
		}
        repository.addVessel(r);
        VesselEvent evt = new VesselEvent(r.vesselID,VesselEvent.VESSEL_CREATED_TYPE,new VesselCreatedEvent(r));
        eventProducer.sendEvent(r.vesselID, evt);
        return r;
    }

    public Vessel updateVessel(Vessel newVessel) {
        return repository.updateVessel(newVessel);
    }

    public Vessel getAllVesselsForTransaction(String txid) {
        return repository.getVesselsForTransaction(txid);
    }

}
