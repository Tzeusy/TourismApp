package com.example.tze.tourismapptwo;

/**
 * Created by Gabriel on 4/12/2017.
 */

public class LocationEdge {
    public enum TravelType { PUBLIC_TRANSPORT, TAXI, WALKING }

    public final Location sourceLocation, destLocation;
    public final TravelType travelType;

    public LocationEdge(Location sourceLocation, Location destLocation, TravelType travelType) {
        this.sourceLocation = sourceLocation;
        this.destLocation = destLocation;
        this.travelType = travelType;
    }

    public String toString() {
        return "Source: " + sourceLocation.toString() + " Destination: " + destLocation.toString() + " Travel by: " + travelType.toString();
    }
}