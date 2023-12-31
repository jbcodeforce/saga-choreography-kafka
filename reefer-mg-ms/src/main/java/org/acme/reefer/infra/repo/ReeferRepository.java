package org.acme.reefer.infra.repo;

import java.util.List;

import org.acme.reefer.domain.Reefer;

public interface ReeferRepository {
    public List<Reefer> getAll();
    public Reefer addReefer(Reefer entity);
    public Reefer updateReefer(Reefer entity);
    public Reefer getById(String key);
    public List<Reefer>  getReefersForOrder(String transactionID,String pickupLocation,long expectedCapacity);
    public List<Reefer> getReefersForTransaction(String transactionID);
    public void cleanTransaction(String transactionID);
}
