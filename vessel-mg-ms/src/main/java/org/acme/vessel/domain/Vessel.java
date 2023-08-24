package org.acme.vessel.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Vessel {
   public static String ASSIGNED = "assign";

   public String vesselID;
   public long capacity;
   public String type; 
   public String status;
   public String departurePort;
   public String arrivalPort;
   public long currentFreeCapacity;
   public String creationDate;

   public Vessel(){}

   public String toString(){
      return "Vessel: " + vesselID + " capacity: " + capacity + " status: " + status;
   }
}
