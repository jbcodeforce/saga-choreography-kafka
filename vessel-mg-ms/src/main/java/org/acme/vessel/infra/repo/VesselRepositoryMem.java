package org.acme.vessel.infra.repo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.acme.vessel.domain.Vessel;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.inject.Singleton;


@Singleton
public class VesselRepositoryMem implements VesselRepository {
    // real life will map those hash maps to different tables
    public  static ConcurrentHashMap<String,List<Vessel>> byDeparturePorts = new ConcurrentHashMap<String,List<Vessel>>();
    private static ConcurrentHashMap<String, Vessel> currentOrderBacklog = new ConcurrentHashMap<String,Vessel>();
    private static ConcurrentHashMap<String,Vessel> vessels = new ConcurrentHashMap<String,Vessel>();

    private static ObjectMapper mapper = new ObjectMapper();
    

    public VesselRepositoryMem() {
        super();
        InputStream is = getClass().getClassLoader().getResourceAsStream("vessels.json");
        if (is == null) 
            throw new IllegalAccessError("file not found for vessel json");
        try {
            List<Vessel> currentDefinitions = mapper.readValue(is, mapper.getTypeFactory().constructCollectionType(List.class, Vessel.class));
            currentDefinitions.stream().forEach( (v) -> { 
                vessels.put(v.vesselID,v);
                if (v.departurePort != null) {
                    if (byDeparturePorts.get(v.departurePort) == null) 
                        byDeparturePorts.put(v.departurePort, new ArrayList<Vessel>());
                    byDeparturePorts.get(v.departurePort).add(v);
                }
                
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        vessels.values().stream().forEach(v -> System.out.println(v.toString()));
    }

    public List<Vessel> getAll(){
        return new ArrayList<Vessel>(vessels.values());
    }

    public Vessel addVessel(Vessel entity) {
        if (entity.vesselID == null) {
            entity.vesselID = UUID.randomUUID().toString();
        }
        vessels.put(entity.vesselID, entity);
        return entity;
    }

    @Override
    public Vessel updateVessel(Vessel entity) {
        vessels.put(entity.vesselID, entity);
        return entity;
    }

    @Override
    public void assignVesselToOrder(String orderID, Vessel v){
        currentOrderBacklog.put(orderID, v);
        
    }

    @Override
    public Vessel getById(String key) {
        return vessels.get(key);
    }

    /**
     * Search a vessel from departure location that has support capacity
     * 
     */
    public  Vessel  getVesselForOrder(String transactionID,
                                String departure,
                                String destination,
                                long expectedCapacity) {
        List<Vessel> vessels = byDeparturePorts.get(departure);
        if (vessels == null) return null;
        for (Vessel v : vessels) {
            if (v.arrivalPort.equals(destination)) {
                // add capacity management
                v.currentFreeCapacity -= expectedCapacity;
                return v;
            }
        }
        return null;
    }

    public  Vessel getVesselsForTransaction(String transactionID) {
        return currentOrderBacklog.get(transactionID);
    }

    public String cleanTransaction(String transactionID , long capacity) {
        Vessel allocatedVessel = getVesselsForTransaction(transactionID);
        if (allocatedVessel != null) {
                allocatedVessel.currentFreeCapacity+=capacity;
        }
        this.currentOrderBacklog.remove(transactionID);
        return allocatedVessel.vesselID;
    }

    
}
