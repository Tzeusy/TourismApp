package com.example.tze.tourismapptwo;

import java.util.HashMap;

public class Location {

    public HashMap<Location,Integer> travelTimesTaxi=null;
    public HashMap<Location, Double> travelCostsTaxi=null;
    public HashMap<Location,Integer> travelTimesPublicT=null;
    public HashMap<Location, Double> travelCostsPublicT=null;
    public HashMap<Location,Integer> travelTimesWalking=null;
    private String location=null;
    public Location(String location,
                    HashMap<Location,Integer> travelTimesTaxi, HashMap<Location,Double> travelCostsTaxi,
                    HashMap<Location,Integer> travelTimesPublicT, HashMap<Location,Double> travelCostsPublicT,
                    HashMap<Location,Integer> travelTimesWalking){
        this.location = location;
        this.travelTimesTaxi = travelTimesTaxi;
        this.travelCostsTaxi = travelCostsTaxi;
        this.travelTimesPublicT = travelTimesPublicT;
        this.travelCostsPublicT = travelCostsPublicT;
        this.travelTimesWalking = travelTimesWalking;
    }
    //Are there issues here with circular logic? Like, I need locations to be defined before I can build Locations with HashMaps?
    //Made a blank constructor in case the above is the case.
    public Location(String location){
        this.location=location;
    }
}