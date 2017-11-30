package com.example.tze.tourismapptwo;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    private class GetWikipediaTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... locations) {
            String location = locations[0];
            String res = "";
            try {
                String urlString = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&redirects=1&titles=" + URLEncoder.encode(location, "UTF-8");
                URL url = new URL(urlString);
                String urlResponse = getUrlResponse(url);
                JSONObject jsonObject = new JSONObject(urlResponse);
                JSONObject pages = jsonObject.getJSONObject("query").getJSONObject("pages");
                String key = pages.keys().next().toString();
                JSONObject value = pages.getJSONObject(key); // j["query"]["pages"][key]
                res = value.getString("title") + "\n" + value.getString("extract");
            }
            catch (UnsupportedEncodingException e) { Log.d(TAG, "Error when encoding location as URL: " + e.toString()); }
            catch (MalformedURLException e) { Log.d(TAG, "Error when converting to URL: " + e.toString()); }
            catch (JSONException e) {
                res = location;
                Log.d(TAG, "Error when converting " + location + " Wikipedia info to JSON: " + e.toString());
            }
            return res;
        }

        @Override
        protected void onPostExecute(String extract) {
            locationDataTextView.setText(extract);
        }

        private String getUrlResponse(URL url) {
            InputStream inputStream = null;
            HttpURLConnection conn = null;
            String content = "";
            try {
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.connect();
                Log.d(TAG, "HTTP response code: " + conn.getResponseCode());
                inputStream = conn.getInputStream();
                content = convertInputToString(inputStream);
            }
            catch (IOException e) { Log.d(TAG, "IOException when opening URL connection: " + e.toString()); }
            finally {
                conn.disconnect();
                if (inputStream != null) {
                    try { inputStream.close(); }
                    catch (IOException e) { Log.d(TAG, "IOException when closing URL inputStream: " + e.toString()); }
                }
            }
            return content;
        }

        private String convertInputToString(InputStream stream) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            String s;
            try {
                while((s = reader.readLine()) != null) {
                    builder.append(s).append('\n');
                }
            }
            catch (IOException e) { Log.d(TAG, "IOException when converting InputStream to String: " + e.toString()); }
            return builder.toString();
        }
    }
}
