package com.example.tze.tourismapptwo;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

public class LocationSelectionPage extends AppCompatActivity {

    private static final String TAG = "LOC_SEL_ACTIVITY";

    public static TextView locationDataTextView;
    private RecyclerView locationRecyclerView;
    private LocationAdapter locationAdapter;
    private ArrayList<String> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selection_page);

        locationDataTextView = (TextView)findViewById(R.id.location_data_text_view);

        locations = getLocationsFromAssets();
        locationRecyclerView = (RecyclerView)findViewById(R.id.location_recycler_view);
        locationAdapter = new LocationAdapter(this, locations);
        locationRecyclerView.setAdapter(locationAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        locationRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(locationRecyclerView.getContext(), layoutManager.getOrientation());
        locationRecyclerView.addItemDecoration(dividerItemDecoration);

        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        /*
        LinearLayout layout = findViewById(R.id.location_selection_layout);
        ArrayList<String> locations = getLocationsFromAssets();
        for (String location: locations) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            checkBox.setText(location);
            checkBox.setChecked(prefs.getBoolean(location, false));
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox)v;
                    String location = checkBox.getText().toString();
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(location, checkBox.isChecked());
                    editor.commit();

                    if (checkBox.isChecked()) (new GetWikipediaTask()).execute(location);
                }
            });
            layout.addView(checkBox);
        }
        */
    }

    private ArrayList<String> getLocationsFromAssets() {
        ArrayList<String> list = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("sample_locations.txt")));
            String line;
            while((line = reader.readLine()) != null) {
                list.add(line);
            }
        }
        catch (IOException e) { Log.d(TAG, "Error retrieving locations from assets: " + e.toString()); }
        finally {
            if (reader != null) {
                try { reader.close(); }
                catch (IOException e) { Log.d(TAG, "Error closing location reader: " + e.toString()); }
            }
        }

        return list;
    }

}
