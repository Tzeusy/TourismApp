package com.example.tze.tourismapptwo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItineraryPage extends AppCompatActivity {

    private static final String TAG = "ITINERARY_ACTIVITY";

    private ArrayList<String> selectedLocations;
    private TextView weatherTemperatureTextView;
    private ImageView weatherIconImageView;
    private Spinner locationGenreSpinner;
    private TextView locationCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_page);

        locationGenreSpinner = (Spinner)findViewById(R.id.location_genre_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.locations_genre_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationGenreSpinner.setAdapter(spinnerAdapter);
        locationGenreSpinner.setOnItemSelectedListener(new LocationGenreSpinnerActivity());

        weatherTemperatureTextView = (TextView)findViewById(R.id.weather_temperature_text_view);
        weatherIconImageView = (ImageView)findViewById(R.id.weather_icon_image_view);
        locationCountTextView = (TextView)findViewById(R.id.location_count_text_view);

        updateSelectedLocations();
        (new GetWeatherTask()).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSelectedLocations();
    }

    private void updateSelectedLocations() {
        String genre = locationGenreSpinner.getSelectedItem().toString();
        Log.d(TAG, "Current spinner item: " + genre);
        // use genre instead of selected_locations
        SharedPreferences prefs = getBaseContext().getSharedPreferences(genre, Context.MODE_PRIVATE);
        Map<String,Boolean> map = (Map<String,Boolean>)prefs.getAll();

        ArrayList<String> list = new ArrayList<>();
        for (Map.Entry<String,Boolean> entry: map.entrySet()) {
            if (entry.getValue()) list.add(entry.getKey());
        }
        selectedLocations = list;
        locationCountTextView.setText("Locations selected " + selectedLocations.size());
    }

    private class GetWeatherTask extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... args) {
            URL url = null;
            try { url = new URL("https://api.data.gov.sg/v1/environment/24-hour-weather-forecast"); }
            catch (MalformedURLException e) { Log.d(TAG, "Unable to create URL: " + e.toString()); }

            JSONObject jsonObject = null;
            try {
                String urlResponse = getUrlResponse(url);
                jsonObject = new JSONObject(urlResponse);
            }
            catch (JSONException e) { Log.d(TAG, "Error when creating JSON Object: " + e.toString()); }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                // jsonObject.items[0].general
                JSONObject general = jsonObject.getJSONArray("items").getJSONObject(0).getJSONObject("general");
                String forecast = general.getString("forecast");
                JSONObject temperature = general.getJSONObject("temperature");
                Integer high = Integer.parseInt(temperature.getString("high"));
                Integer low = Integer.parseInt(temperature.getString("low"));

                Log.d(TAG, "Forecast: " + forecast + "\nHigh: " + high + "\tLow: " + low);

                Integer averageTemperature = (high + low)/2;
                weatherTemperatureTextView.setText(averageTemperature.toString() + "°C");

                // Thunder, Showers/Rain, Wind, Cloud, else
                forecast = forecast.toLowerCase();
                String filename;
                if (forecast.contains("thunder")) filename = "weather_stormy";
                else if (forecast.contains("showers") || forecast.contains("rain")) filename = "weather_rainy";
                else if (forecast.contains("wind")) filename = "weather_windy";
                else if (forecast.contains("cloud")) filename = "weather_cloudy";
                else filename = "weather_sunny";
                String packageName = getBaseContext().getPackageName();
                int resID = getBaseContext().getResources().getIdentifier(filename, "drawable", packageName);
                weatherIconImageView.setImageResource(resID);
            }
            catch (JSONException e) { Log.d(TAG, "Error when extracting JSON info: " + e.toString()); }
        }

        private String getUrlResponse(URL url) {
            InputStream inputStream = null;
            HttpURLConnection conn = null;
            String content = "";
            try {
                conn = (HttpURLConnection)url.openConnection();
                conn.setRequestProperty("api-key", "uGfJQLpNru1asu6tRzilgCY8otSu5Mk6");
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.connect();
                Log.d(TAG, "Weather API HTTP response code: " + conn.getResponseCode());
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

    private class LocationGenreSpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView parent, View view, int pos, long id) {
            //String genre = (String)parent.getItemAtPosition(pos);
            updateSelectedLocations();
        }

        public void onNothingSelected(AdapterView parent) {
        }
    }

    public void selectLocationButtonListener(View v) {
        Context context = v.getContext();
        Intent intent = new Intent(context, LocationSelectionPage.class);
        intent.putExtra("genre", locationGenreSpinner.getSelectedItem().toString());
        startActivity(intent);
    }
}
