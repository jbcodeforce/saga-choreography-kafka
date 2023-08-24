package org.acme.vessel.domain;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import org.acme.vessel.infra.repo.VesselRepository;

@ApplicationScoped
public class VesselService {
    private static Logger logger = Logger.getLogger("VesselService");

    @Inject
    public VesselRepository repository;

    public VesselService(VesselRepository repo) {
        this.repository = repo;
    }

    public Vessel getVesselById(String id) {
        return repository.getById(id);
    }

  
    public List<Vessel> getAllVessels() {
        return repository.getAll();
    }

    public Vessel saveVessel(Vessel r){
        repository.addVessel(r);
        return r;
    }

    public Vessel updateVessel(Vessel newVessel) {
        return repository.updateVessel(newVessel);
    }

    public Vessel getAllVesselsForTransaction(String txid) {
        return repository.getVesselsForTransaction(txid);
    }

}
