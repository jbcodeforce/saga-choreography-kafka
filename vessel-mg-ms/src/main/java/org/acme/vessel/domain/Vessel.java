package org.acme.vessel.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Vessel {
   public static String ASSIGNED = "assigned";

   public String vesselID;
   public long capacity;
   public String shipType; 
   public String status;
   public String departurePort;
   public String arrivalPort;
   public long currentFreeCapacity;
   public String creationDate;

   public Vessel(){}

   

   public Vessel(String vesselID, long capacity, String type, String status, String departurePort, String arrivalPort) {
      this.vesselID = vesselID;
      this.capacity = capacity;
      this.shipType = type;
      this.status = status;
      this.departurePort = departurePort;
      this.arrivalPort = arrivalPort;
      this.currentFreeCapacity = capacity;
   }



   public String toString(){
      return "Vessel: " + vesselID + " port:" + departurePort +  " capacity: " + capacity + " status: " + status;
   }
}
