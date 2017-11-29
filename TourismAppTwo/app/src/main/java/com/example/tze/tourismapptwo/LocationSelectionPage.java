package com.example.tze.tourismapptwo;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LocationSelectionPage extends AppCompatActivity {

    private static final String TAG = "LOC_SEL_ACTIVITY";

    TextView locationDataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selection_page);

        locationDataTextView = (TextView)findViewById(R.id.location_data_text_view);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
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
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(checkBox.getText().toString(), checkBox.isChecked());
                    editor.commit();

                    locationDataTextView.setText(checkBox.getText());
                }
            });
            layout.addView(checkBox);
        }
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
