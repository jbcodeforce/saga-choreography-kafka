package org.acme.vessel.infra.repo;

import java.util.List;

import org.acme.vessel.domain.Vessel;

public interface VesselRepository {
    public List<Vessel> getAll();
    public Vessel addVessel(Vessel entity);
    public Vessel updateVessel(Vessel entity);
    public Vessel getById(String key);
    public Vessel  getVesselForOrder(String transactionID, String origin, String destination, long capacity);
    public  Vessel getVesselsForTransaction(String transactionID);
    public String cleanTransaction(String transactionID, long capacity);
    public void assignVesselToOrder(String orderID, Vessel v);
}
