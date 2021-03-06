package com.example.tze.tourismapptwo;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

public class CsvParser{
    private static final String TAG = "CSV_PARSER";
    private Context context;

    public CsvParser(Context context) {
        this.context = context;
    }

    public HashMap<String,HashMap<Location,Double>> parse(int resID, HashMap<String,Location> locationHashMap){
        HashMap<String,HashMap<Location,Double>> rows = new HashMap<>();

        BufferedReader br = null;
        String line = "";
        try {
            InputStream inputStream = context.getResources().openRawResource(resID);
            br = new BufferedReader(new InputStreamReader(inputStream));
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
                    HashMap<Location,Double> rowInformation = new HashMap<>();
                    for(int i=0;i<nodes.length;i++){
                        if(i==0) continue;
                        rowInformation.put(locationHashMap.get(locations.get(i-1)),Double.valueOf(nodes[i]));
                    }
                    rows.put(nodes[0], rowInformation);
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
        return rows;
    }
}
