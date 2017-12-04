package com.example.tze.tourismapptwo;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class BruteForceSolver {

    private double[] costTimeAndSteps = new double[]{1000000,300};
    private ArrayList<Location> optimalRoute= new ArrayList<>();
    private ArrayList<Integer> transportationHistory;

    public ArrayList<Integer> getTransportationHistory() {
        return transportationHistory;
    }

    public double[] getCostTimeAndSteps() {
        return costTimeAndSteps;
    }

    public void fastestRoute(Location currLocation, Location finalLocation, ArrayList<Location> listLocationsToVisit) {
        Log.d("Debrief","Starting location is " + currLocation.toString());
        Log.d("Debrief","Final Location is "+ finalLocation.toString());
        Log.d("Debrief","Intermediate destinations are "+listLocationsToVisit.toString());
        fastestRoute(currLocation, finalLocation, listLocationsToVisit, new ArrayList<Location>(), new double[]{0.0,0.0}, 3,new ArrayList<Integer>());
    }

    private void fastestRoute(Location currLocation, Location finalLocation,
                              ArrayList<Location> listLocationsToVisit,
                              ArrayList<Location> listLocationsSoFar,
                              double[] soFar,int modeOfTransportation,
                              ArrayList<Integer> TransportationHistory) {
        //if current route's cost has exceeded $20 break the recursion
        //soFar[0] is the duration so far, soFar[1] is the cost so far, soFar[2] is the step number.
        //nothing more to visit, return to final location
        if (listLocationsToVisit.size() == 0) {
            for (int i = 0; i < 3; i++) {
                toFinalLoc(currLocation, listLocationsSoFar, soFar, i, finalLocation, TransportationHistory);
            }
            return;
        }

        //save an immutable version so we can have several different array lists each missing ONE element for the purpose of recursion.
        final ArrayList<Location> restorePoint = (ArrayList<Location>) listLocationsToVisit.clone();
        for (int j = 0; j < restorePoint.size(); j++) {
            Location nextDestination = listLocationsToVisit.get(j);
            boolean visitedBefore = false;
            for (Location location : listLocationsSoFar) {
                if (location == nextDestination) {
                    visitedBefore = true;
                }
            }
            //if we've visited this place before, or the next destination is our current location, continue.
            if (visitedBefore||nextDestination == currLocation) continue;
            //we have our next Destination, now we get the time and cost for this rabbit hole - through 3 possible paths
            double timeSoFar;
            double costSoFar;
            if (modeOfTransportation == 0) {
                //Cabbing
//                Log.d("TAZDINGOOCar",currLocation.travelTimesTaxi.toString());
                Log.d("TAZDINGO", currLocation.toString() + " Next Destination " + nextDestination.toString() +
                        " takes " + String.valueOf(currLocation.travelTimesTaxi.get(nextDestination)) + " seconds via Taxi");
                timeSoFar = soFar[0] + currLocation.travelTimesTaxi.get(nextDestination);
                costSoFar = soFar[1] + currLocation.travelCostsTaxi.get(nextDestination);
                TransportationHistory.add(0);
            } else if (modeOfTransportation == 1) {
                Log.d("TAZDINGO", currLocation.toString() + " Next Destination " + nextDestination.toString() +
                        " takes " + String.valueOf(currLocation.travelTimesPublicT.get(nextDestination)) + " seconds via Public Transport");
                timeSoFar = soFar[0] + currLocation.travelTimesPublicT.get(nextDestination);
                costSoFar = soFar[1] + currLocation.travelCostsPublicT.get(nextDestination);
                TransportationHistory.add(1);
            } else if (modeOfTransportation == 2) {
//                Log.d("TAZDINGOOWalk",currLocation.travelTimesWalking.toString());
                timeSoFar = soFar[0] + currLocation.travelTimesWalking.get(nextDestination);
                //walking is free
                costSoFar = soFar[1];
                TransportationHistory.add(2);
            } else {
                timeSoFar = 0;
                costSoFar = 0;
            }
            double stepSoFar = soFar[2] + 1;
            //Assembling all three in one double[] for the next recursive iteration
            double[] toFeed = new double[]{timeSoFar, costSoFar};
            //remove current location from list to explore, indicating we're going down THIS specific rabbit hole; add it to history of travels
            listLocationsSoFar.add(nextDestination);
            Log.d("TAZDINGO", "Exploring " + nextDestination.toString());
            listLocationsToVisit.remove(nextDestination);
            for (int i = 0; i < 3; i++) {
                fastestRoute(nextDestination, finalLocation, listLocationsToVisit, listLocationsSoFar, toFeed, i, TransportationHistory);
            }
            listLocationsToVisit.add(nextDestination);
        }
    }
    private void toFinalLoc(Location currLocation, ArrayList<Location> listLocationsSoFar, double[] soFar,int modeOfTransportation, Location finalLocation,ArrayList<Integer> TransportationHistory){
        double[] answer = new double[2];
        if(modeOfTransportation==0){
            answer[0] = soFar[0]+currLocation.travelTimesTaxi.get(finalLocation);
            answer[1] = soFar[1]+currLocation.travelCostsTaxi.get(finalLocation);
            answer[2] = soFar[2]+1;
            TransportationHistory.add(0);
        }
        else if (modeOfTransportation==1){
            answer[0] = soFar[0]+currLocation.travelTimesPublicT.get(finalLocation);
            answer[1] = soFar[1]+currLocation.travelCostsPublicT.get(finalLocation);
            answer[2] = soFar[2]+1;
            TransportationHistory.add(1);
        }
        else{
            answer[0] = soFar[0]+currLocation.travelTimesWalking.get(finalLocation);
            answer[1] = soFar[1];
            answer[2] = soFar[2]+1;
            TransportationHistory.add(2);
        }
        if(this.costTimeAndSteps[0]>answer[0]&&answer[1]<20){
            this.costTimeAndSteps = answer;
            costTimeAndSteps = answer;
            optimalRoute = listLocationsSoFar;
            this.transportationHistory = TransportationHistory;
        }
//        Log.d("TAZDINGO","Our optimal route is "+ optimalRoute.toString());
//        Log.d("TAZDINGO","takes " + String.valueOf(costTimeAndSteps[0]) + " seconds and costs " + String.valueOf(costTimeAndSteps[1]));
        return;
    }
}
