package org.acme.vessel.infra.repo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.acme.vessel.domain.Vessel;

import jakarta.enterprise.context.ApplicationScoped;

import com.fasterxml.jackson.databind.ObjectMapper;


@ApplicationScoped
public class VesselRepositoryMem implements VesselRepository {
    public  static ConcurrentHashMap<String,List<Vessel>> byDeparturePort = new ConcurrentHashMap<String,List<Vessel>>();
    private ConcurrentHashMap<String, Vessel> currentOrderBacklog = new ConcurrentHashMap<String,Vessel>();
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
                    if (byDeparturePort.get(v.departurePort) == null) 
                        byDeparturePort.put(v.departurePort, new ArrayList<Vessel>());
                    byDeparturePort.get(v.departurePort).add(v);
                }
                
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        vessels.values().stream().forEach(v -> System.out.println(v.vesselID));
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

    public Vessel updateVessel(Vessel entity) {
        vessels.put(entity.vesselID, entity);
        return entity;
    }

    @Override
    public Vessel getById(String key) {
        return vessels.get(key);
    }

    /**
     * Search vessels from departure location that has support capacity
     * @return list of freezers support this expected catacity and at the expected location
     */
    public  Vessel  getVesselForOrder(String transactionID,
                                String departure,
                                String destination,
                                long expectedCapacity) {
        List<Vessel> vessels = byDeparturePort.get(departure);
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

    public void cleanTransaction(String transactionID , long capacity) {
        Vessel allocatedVessel = this.currentOrderBacklog.get(transactionID);
        if (allocatedVessel != null) {
                allocatedVessel.currentFreeCapacity+=capacity;
        }
        this.currentOrderBacklog.remove(transactionID);
    }

    
}
