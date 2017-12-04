package com.example.tze.tourismapptwo;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Tze on 4/12/2017.
 */

public class BruteForceSolver {

    private ArrayList<Location> listOfLocationsToVisit;

    private double[] costTimeAndSteps;
    private ArrayList<Location> optimalRoute;
    public Location MBS = new Location("MBS");

    public BruteForceSolver(ArrayList<Location> listOfLocationsToVisit){
        this.listOfLocationsToVisit = listOfLocationsToVisit;
    }

    public double[] getCostTimeAndSteps() {
        return costTimeAndSteps;
    }
    public ArrayList<Location> getOptimalRoute() {
        return optimalRoute;
    }

    public double[] toMBS(Location currLocation, ArrayList<Location> listLocationsSoFar, double[] soFar,int modeOfTransportation){
        double[] answer = new double[3];
        if(modeOfTransportation==0){
            answer[0] = soFar[0]+currLocation.travelTimesTaxi.get(MBS);
            answer[1] = soFar[1]+currLocation.travelCostsTaxi.get(MBS);
            answer[2] = soFar[3]+1;
            return answer;
        }
        else if (modeOfTransportation==1){
            answer[0] = soFar[0]+currLocation.travelTimesPublicT.get(MBS);
            answer[1] = soFar[1]+currLocation.travelCostsPublicT.get(MBS);
            answer[2] = soFar[3]+1;
            return answer;
        }
        else{
            answer[0] = soFar[0]+currLocation.travelTimesWalking.get(MBS);
            answer[1] = soFar[1]+0;
            answer[2] = soFar[3]+1;
            return answer;
        }
    }

    public double[] fastestRoute(Location currLocation, ArrayList<Location> listLocationsToVisit,
                                 ArrayList<Location> listLocationsSoFar,
                                 double[] soFar,int modeOfTransportation){
        //if current route's cost has exceeded $20 break the recursion
        if(soFar[1]>20) return new double[]{999999,999,20};
        //if all locations are exhausted this is the path time and cost
        double[] bestSoFar = new double[]{99999,99999,0};

        if(listLocationsToVisit.size()==0){
            for(int i=0;i<3;i++){
                double[] finalVisitBackToMBS = toMBS(currLocation,listLocationsSoFar,soFar,i);
                if(finalVisitBackToMBS[1]>20) return new double[]{99999,99999,0};
                else{
                    if(finalVisitBackToMBS[0]<bestSoFar[0]){
                        bestSoFar=finalVisitBackToMBS;
                    }
                }
            }
            return bestSoFar;
        }

        //save an immutable version so we can have several different array lists each missing ONE element for the purpose of recursion.
        final ArrayList<Location> restorePoint = listLocationsToVisit;
        for(Location nextDestination:restorePoint){
            //we have our next Destination
            //now we get the time and cost for this rabbit hole - through 3 possible paths
            for(int i=0;i<3;i++){
                double timeSoFar;
                double costSoFar;
                if(modeOfTransportation==0){
                    //Cabbing
                    timeSoFar = soFar[0] + currLocation.travelTimesTaxi.get(nextDestination);
                    costSoFar = soFar[1] + currLocation.travelCostsTaxi.get(nextDestination);
                }
                else if(modeOfTransportation==1){
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
                double[] routeTimeAndCost = fastestRoute(nextDestination, listLocationsToVisit, listLocationsSoFar, toFeed, i);
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
                listLocationsToVisit=restorePoint;
                if(soFar[1]>20) return new double[]{999999,999,20};
            }
        }
        return bestSoFar;
    }
}