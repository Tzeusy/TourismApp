package com.example.tze.tourismapptwo;

import java.util.HashMap;

public class Location {

    public HashMap<Location,Integer> travelTimes=null;
    public HashMap<Location, Double> travelCosts=null;
    private Location location=null;
    public Location(Location location, HashMap<Location,Integer> travelTimes, HashMap<Location,Double> travelCosts){
        this.location = location;
        this.travelTimes = travelTimes;
        this.travelCosts = travelCosts;
    }
    //Are there issues here with circular logic? Like, I need locations to be defined before I can build Locations with HashMaps?
    //Made a blank constructor in case the above is the case.
    public Location(Location location){
        this.location=location;
    }
}