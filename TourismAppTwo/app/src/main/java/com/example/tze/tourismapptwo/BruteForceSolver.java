package com.example.tze.tourismapptwo;


import java.util.ArrayList;

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
            try{
                //now we get the time and cost for this rabbit hole
                double timeSoFar = soFar[0] + currLocation.travelTimes.get(nextDestination);
                double costSoFar = soFar[1] + currLocation.travelCosts.get(nextDestination);
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
            catch(Exception ex){
                return bestSoFar;
            }
            listLocationsToVisit=restorePoint;
        }
        //STILL FLAWED: need to append journey BACK TO MBS after the list is exhausted.
        return bestSoFar;
    }
}