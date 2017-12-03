package com.example.tze.tourismapptwo;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tze on 4/12/2017.
 */

public class BruteForceSolver {

    private ArrayList<Location> listOfLocationsToVisit;

    private double[] costTimeAndSteps;
    private ArrayList<Location> optimalRoute;

    public BruteForceSolver(ArrayList<Location> listOfLocationsToVisit){
        this.listOfLocationsToVisit = listOfLocationsToVisit;
    }

    public double[] getCostTimeAndSteps() {
        return costTimeAndSteps;
    }
    public ArrayList<Location> getOptimalRoute() {
        return optimalRoute;
    }

    public double[] fastestRoute(Location currLocation, ArrayList<Location> listLocationsToVisit, ArrayList<Location> listLocationsSoFar, double[] soFar){
        //if current route's cost has exceeded $20 break the recursion
        if(soFar[1]>20) return new double[]{999999,999,20};
        //if all locations are exhausted this is the path time and cost
        if(listLocationsSoFar.size()==0) return soFar;

        //save an immutable version so we can have several different array lists each missing ONE element for the purpose of recursion.
        final ArrayList<Location> restorePoint = listLocationsToVisit;
        double[] bestSoFar = new double[]{99999,99999,0};
        for(Location nextDestination:restorePoint){
            //we have our next Destination
            //now we get the time and cost for this rabbit hole - through 3 possible paths
            for(int i=0;i<3;i++){
                final ArrayList<Location> restorePointTwo = listLocationsToVisit;
                double timeSoFar;
                double costSoFar;
                if(i==0){
                    //Cabbing
                    timeSoFar = soFar[0] + currLocation.travelTimesTaxi.get(nextDestination);
                    costSoFar = soFar[1] + currLocation.travelCostsTaxi.get(nextDestination);
                }
                else if(i==1){
                    //Training
                    timeSoFar = soFar[0] + currLocation.travelTimesPublicT.get(nextDestination);
                    costSoFar = soFar[1] + currLocation.travelCostsPublicT.get(nextDestination);
                }
                else{
                    timeSoFar = soFar[0] + currLocation.travelTimesWalking.get(nextDestination);
                    //walking is free
                    costSoFar = soFar[1];
                }
                double stepSoFar = soFar[2] + 1;
                //Assembling all three in one double[] for the next recursive iteration
                double[] toFeed = new double[]{timeSoFar,costSoFar,stepSoFar};
                //remove current location from list to explore, indicating we're going down THIS specific rabbit hole; add it to history of travels
                listLocationsSoFar.add(nextDestination);
                listLocationsToVisit.remove(nextDestination);
                double[] routeTimeAndCost = fastestRoute(nextDestination, listLocationsToVisit, listLocationsSoFar, toFeed);
                //if route time and cost is on the same timeline as our calculations, compare calculations with route
                if(routeTimeAndCost[2]==bestSoFar[2]){
                    if(bestSoFar[0]<routeTimeAndCost[0]){
                        bestSoFar=routeTimeAndCost;
                        optimalRoute = listLocationsSoFar;
                        costTimeAndSteps = bestSoFar;
                    }
                }
                //otherwise route time and cost is AFTER our calculations; route time and cost is adopted.
                else{
                    bestSoFar = routeTimeAndCost;
                }
            }
            listLocationsToVisit=restorePoint;
        }
        //STILL FLAWED: need to append journey BACK TO MBS after the list is exhausted.
        return bestSoFar;
    }
}