package com.example.tze.tourismapptwo;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;
import com.example.tze.tourismapptwo.LocationEdge.TravelType;

/**
 * Created by Gabriel on 4/12/2017.
 */

public class SmartSolver {
    private static final String TAG = "SMART_SOLVER";
    private static final Random r = new Random();
    private static final int COMPUTE_TRAVEL_COST = 0;
    private static final int COMPUTE_TRAVEL_DURATION = 1;
    private static double temp = 100000;
    private static double coolingRate = 0.003;

    public static ArrayList<LocationEdge> solve(Location sourceLocation, Location destLocation, ArrayList<Location> locationsToVisit, double budget) {
        ArrayList<LocationEdge> solution = solve(sourceLocation, destLocation, locationsToVisit);
        while (computeWeight(solution,COMPUTE_TRAVEL_COST) > budget) solution = solve(sourceLocation, destLocation, locationsToVisit);
        return solution;
    }

    private static ArrayList<LocationEdge> solve(Location sourceLocation, Location destLocation, ArrayList<Location> locationsToVisit) {
        ArrayList<LocationEdge> initialRandomSolution = getInitialRandomSolution(sourceLocation, destLocation, locationsToVisit);
        printRoute(initialRandomSolution);

        ArrayList<LocationEdge> bestSolution = initialRandomSolution;
        ArrayList<LocationEdge> currentSolution = initialRandomSolution;
        while (temp > 1) {
            ArrayList<LocationEdge> newSolution = new ArrayList<>(currentSolution);
            int n = r.nextInt(newSolution.size()-2);
            LocationEdge edge1 = newSolution.get(n);
            LocationEdge edge2 = newSolution.get(n+1);
            LocationEdge edge3 = newSolution.get(n+2);
            // a -> b -> c -> d
            Location a = edge1.sourceLocation;
            Location b = edge1.destLocation;
            Location c = edge2.destLocation;
            Location d = edge3.destLocation;
            // a -> c -> b -> d
            LocationEdge newEdge1 = new LocationEdge(a, c, getRandomTravelType());
            LocationEdge newEdge2 = new LocationEdge(c, b, getRandomTravelType());
            LocationEdge newEdge3 = new LocationEdge(b, d, getRandomTravelType());
            // replace the edges
            newSolution.remove(edge1);
            newSolution.remove(edge2);
            newSolution.remove(edge3);
            newSolution.add(n, newEdge3);
            newSolution.add(n, newEdge2);
            newSolution.add(n, newEdge1);
            // check if we should accept the new one
            double currentWeight = computeWeight(currentSolution,COMPUTE_TRAVEL_DURATION);
            double newWeight = computeWeight(newSolution,COMPUTE_TRAVEL_DURATION);
            if (newWeight < currentWeight) currentSolution = newSolution; // if new solution performs better, use it for next iteration
            else { // otherwise randomize whether or not to use it for next iteration
                if (Math.exp((currentWeight-newWeight)/temp) > r.nextDouble()) currentSolution = newSolution;
            }
            // if the current solution is global best, store it
            if (computeWeight(currentSolution,COMPUTE_TRAVEL_DURATION) < computeWeight(bestSolution,COMPUTE_TRAVEL_DURATION)) bestSolution = newSolution;
            // move on
            temp*= 1-coolingRate;
        }
        return bestSolution;
    }

    private static ArrayList<LocationEdge> getInitialRandomSolution(Location sourceLocation, Location destLocation, ArrayList<Location> locationsToVisit) {
        ArrayList<LocationEdge> out = new ArrayList<>();
        ArrayList<Location> remainingLocations = new ArrayList<>(locationsToVisit);
        remainingLocations.remove(sourceLocation);
        remainingLocations.remove(destLocation);
        Location prevLocation = sourceLocation;
        while (remainingLocations.size() > 0) {
            int n = r.nextInt(remainingLocations.size());
            Location nextLocation = remainingLocations.get(n);

            out.add(new LocationEdge(prevLocation, nextLocation, getRandomTravelType()));
            prevLocation = nextLocation;
            remainingLocations.remove(n);
        }
        // add the last one
        out.add(new LocationEdge(prevLocation, destLocation, getRandomTravelType()));
        return out;
    }

    private static double computeWeight(ArrayList<LocationEdge> route, int type) {
        double total = 0;
        for (int i=0; i<route.size(); i++) {
            LocationEdge edge = route.get(i);
            try {
                double weight;
                if (type == COMPUTE_TRAVEL_COST) { // compute travelCosts
                    if (edge.travelType == TravelType.PUBLIC_TRANSPORT)
                        weight = edge.sourceLocation.travelCostsPublicT.get(edge.destLocation);
                    else if (edge.travelType == TravelType.TAXI)
                        weight = edge.sourceLocation.travelCostsTaxi.get(edge.destLocation);
                    else weight = 0;
                } else { // compute travelTimes
                    if (edge.travelType == TravelType.PUBLIC_TRANSPORT)
                        weight = edge.sourceLocation.travelTimesPublicT.get(edge.destLocation);
                    else if (edge.travelType == TravelType.TAXI)
                        weight = edge.sourceLocation.travelTimesTaxi.get(edge.destLocation);
                    else weight = edge.sourceLocation.travelTimesWalking.get(edge.destLocation);
                }
                total += weight;
            }
            catch (NullPointerException e) { Log.d("NullPointerException", edge.sourceLocation.toString() + " " + edge.destLocation.toString() + " " + edge.travelType.toString()); }
        }
        return total;
    }

    public static void printRoute(ArrayList<LocationEdge> route) {
        String s = route.get(0).sourceLocation.toString();
        for (int i=1; i<route.size(); i++) {
            LocationEdge edge = route.get(i);
            s+= "\n (" + edge.travelType.toString() + ") ";
            s+= edge.destLocation.toString();
        }
        Log.d(TAG, s);
        Log.d(TAG, "Total travelling duration: " + computeWeight(route,COMPUTE_TRAVEL_DURATION) + " Total cost: " + computeWeight(route,COMPUTE_TRAVEL_COST));
    }

    private static TravelType getRandomTravelType() { return TravelType.values()[r.nextInt(TravelType.values().length)]; }
}
