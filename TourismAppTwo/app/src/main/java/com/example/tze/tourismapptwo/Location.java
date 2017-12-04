package com.example.tze.tourismapptwo;

import java.util.HashMap;

public class Location {
    public static class Genre {
        public static final String ENTERTAINMENT = "Entertainment";
        public static final String FOOD = "Food";
        public static final String MUSEUM = "Museum";
        public static final String OUTDOOR = "Outdoor";
        public static final String PLACEOFWORSHIP = "Place of Worship";
    }

    public HashMap<Location,Double> travelTimesTaxi, travelCostsTaxi, travelTimesPublicT, travelCostsPublicT, travelTimesWalking;
    private String location;

    //Are there issues here with circular logic? Like, I need locations to be defined before I can build Locations with HashMaps?
    //Made a blank constructor in case the above is the case.
    public Location(String location){
        this.location=location;
    }

    public String toString() {
        return location;
    }
}