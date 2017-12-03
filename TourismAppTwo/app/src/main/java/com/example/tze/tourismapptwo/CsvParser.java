package com.example.tze.tourismapptwo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

public class CsvParser{
    private ArrayList<HashMap<String,Integer>> rows;
    private HashMap<String,Integer> LocationToIndex;

    // System.out.println(Arrays.asList(rows.get(LocationToIndex.get("Science Centre Singapore"))));

    /*
    Instructions for use: CsvParser's parse function takes in a string object which is the
    path of the .csv file to be analyzed. Its internal rows and LocationToIndex functions
    then get defined. You can access each location's distance (be it time or cost) to each
    other location via (rows.get(LocationToIndex.get("Science Centre Singapore"))).
     */
    public CsvParser(){
        this.rows = new ArrayList<>();
        this.LocationToIndex=new HashMap<>();
    }
    public void parse(String m){
        BufferedReader br = null;
        String line = "";
        try {
            br = new BufferedReader(new FileReader(m));
            int count=0;
            ArrayList<String> locations = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] nodes = line.split(",");
                if(count==0){
                    for(int i=0;i<nodes.length;i++){
                        if(i>0) locations.add(nodes[i]);
                    }
                }
                else{
                    HashMap<String,Integer> rowInformation = new HashMap<>();
                    for(int i=1;i<nodes.length-1;i++){
                        rowInformation.put(locations.get(i-1),Integer.valueOf(nodes[i]));
                    }
                    this.rows.add(rowInformation);
                    this.LocationToIndex.put(nodes[0],count-1);
                }
                count++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
