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
    private TextView itineraryTextView;

    private HashMap<String,Location> entertainmentLocations;
    private HashMap<String,Location> foodLocations;
    private HashMap<String,Location> museumLocations;
    private HashMap<String,Location> outdoorLocations;
    private HashMap<String,Location> placeOfWorshipLocations;

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
        itineraryTextView = (TextView)findViewById(R.id.itinerary_text_view);

        updateSelectedLocations();
        parseAllCsv();
        (new GetWeatherTask()).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSelectedLocations();
    }

    private void updateSelectedLocations() {
        String genre = locationGenreSpinner.getSelectedItem().toString();
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

    private void parseAllCsv() {
        Context context = getBaseContext();
        CsvParser parser = new CsvParser(context);
        // distance.csv == Location.travelCosts, time.csv == Location.travelTimes
        // entertainment
        HashMap<String,Location> entertainmentLocations = new HashMap<>();
        for (String loc: DataParser.getLocationsFromAssets(getBaseContext(), Location.Genre.ENTERTAINMENT)) entertainmentLocations.put(loc, new Location(loc));

        HashMap<String,HashMap<Location,Double>> entertainmentPublicTCost = parser.parse(R.raw.entertainment_bus_cost, entertainmentLocations);
        HashMap<String,HashMap<Location,Double>> entertainmentPublicTTime = parser.parse(R.raw.entertainment_bus_time, entertainmentLocations);
        HashMap<String,HashMap<Location,Double>> entertainmentTaxiCost = parser.parse(R.raw.entertainment_taxi_cost, entertainmentLocations);
        HashMap<String,HashMap<Location,Double>> entertainmentTaxiTime = parser.parse(R.raw.entertainment_taxi_time, entertainmentLocations);
        HashMap<String,HashMap<Location,Double>> entertainmentWalkingTime = parser.parse(R.raw.entertainment_walk_time, entertainmentLocations);
        for (HashMap.Entry<String,Location> entry: entertainmentLocations.entrySet()) {
            String locationName = entry.getKey();
            Location location = entry.getValue();
            location.travelTimesPublicT = entertainmentPublicTTime.get(locationName);
            location.travelCostsPublicT = entertainmentPublicTCost.get(locationName);
            location.travelTimesTaxi = entertainmentTaxiTime.get(locationName);
            location.travelCostsTaxi = entertainmentTaxiCost.get(locationName);
            location.travelTimesWalking = entertainmentWalkingTime.get(locationName);
        }
        this.entertainmentLocations = entertainmentLocations;
        // food
        HashMap<String,Location> foodLocations = new HashMap<>();
        for (String loc: DataParser.getLocationsFromAssets(getBaseContext(), Location.Genre.FOOD)) foodLocations.put(loc, new Location(loc));

        HashMap<String,HashMap<Location,Double>> foodPublicTCost = parser.parse(R.raw.food_bus_cost, foodLocations);
        HashMap<String,HashMap<Location,Double>> foodPublicTTime = parser.parse(R.raw.food_bus_time, foodLocations);
        HashMap<String,HashMap<Location,Double>> foodTaxiCost = parser.parse(R.raw.food_taxi_cost, foodLocations);
        HashMap<String,HashMap<Location,Double>> foodTaxiTime = parser.parse(R.raw.food_taxi_time, foodLocations);
        HashMap<String,HashMap<Location,Double>> foodWalkingTime = parser.parse(R.raw.food_walk_time, foodLocations);
        for (HashMap.Entry<String,Location> entry: foodLocations.entrySet()) {
            String locationName = entry.getKey();
            Location location = entry.getValue();
            location.travelTimesPublicT = foodPublicTTime.get(locationName);
            location.travelCostsPublicT = foodPublicTCost.get(locationName);
            location.travelTimesTaxi = foodTaxiTime.get(locationName);
            location.travelCostsTaxi = foodTaxiCost.get(locationName);
            location.travelTimesWalking = foodWalkingTime.get(locationName);
        }
        this.foodLocations = foodLocations;
        // museum
        HashMap<String,Location> museumLocations = new HashMap<>();
        for (String loc: DataParser.getLocationsFromAssets(getBaseContext(), Location.Genre.MUSEUM)) museumLocations.put(loc, new Location(loc));

        HashMap<String,HashMap<Location,Double>> museumPublicTCost = parser.parse(R.raw.museum_bus_cost, museumLocations);
        HashMap<String,HashMap<Location,Double>> museumPublicTTime = parser.parse(R.raw.museum_bus_time, museumLocations);
        HashMap<String,HashMap<Location,Double>> museumTaxiCost = parser.parse(R.raw.museum_taxi_cost, museumLocations);
        HashMap<String,HashMap<Location,Double>> museumTaxiTime = parser.parse(R.raw.museum_taxi_time, museumLocations);
        HashMap<String,HashMap<Location,Double>> museumWalkingTime = parser.parse(R.raw.museum_walk_time, museumLocations);
        for (HashMap.Entry<String,Location> entry: museumLocations.entrySet()) {
            String locationName = entry.getKey();
            Location location = entry.getValue();
            location.travelTimesPublicT = museumPublicTTime.get(locationName);
            location.travelCostsPublicT = museumPublicTCost.get(locationName);
            location.travelTimesTaxi = museumTaxiTime.get(locationName);
            location.travelCostsTaxi = museumTaxiCost.get(locationName);
            location.travelTimesWalking = museumWalkingTime.get(locationName);
        }
        this.museumLocations = museumLocations;
        // outdoor
        HashMap<String,Location> outdoorLocations = new HashMap<>();
        for (String loc: DataParser.getLocationsFromAssets(getBaseContext(), Location.Genre.OUTDOOR)) outdoorLocations.put(loc, new Location(loc));

        HashMap<String,HashMap<Location,Double>> outdoorPublicTCost = parser.parse(R.raw.outdoor_bus_cost, outdoorLocations);
        HashMap<String,HashMap<Location,Double>> outdoorPublicTTime = parser.parse(R.raw.outdoor_bus_time, outdoorLocations);
        HashMap<String,HashMap<Location,Double>> outdoorTaxiCost = parser.parse(R.raw.outdoor_taxi_cost, outdoorLocations);
        HashMap<String,HashMap<Location,Double>> outdoorTaxiTime = parser.parse(R.raw.outdoor_taxi_time, outdoorLocations);
        HashMap<String,HashMap<Location,Double>> outdoorWalkingTime = parser.parse(R.raw.outdoor_walk_time, outdoorLocations);
        for (HashMap.Entry<String,Location> entry: outdoorLocations.entrySet()) {
            String locationName = entry.getKey();
            Location location = entry.getValue();
            location.travelTimesPublicT = outdoorPublicTTime.get(locationName);
            location.travelCostsPublicT = outdoorPublicTCost.get(locationName);
            location.travelTimesTaxi = outdoorTaxiTime.get(locationName);
            location.travelCostsTaxi = outdoorTaxiCost.get(locationName);
            location.travelTimesWalking = outdoorWalkingTime.get(locationName);
        }
        this.outdoorLocations = outdoorLocations;
        // place of worship
        HashMap<String,Location> placeOfWorshipLocations = new HashMap<>();
        for (String loc: DataParser.getLocationsFromAssets(getBaseContext(), Location.Genre.PLACEOFWORSHIP)) placeOfWorshipLocations.put(loc, new Location(loc));

        HashMap<String,HashMap<Location,Double>> placeOfWorshipPublicTCost = parser.parse(R.raw.placeofworship_bus_cost, placeOfWorshipLocations);
        HashMap<String,HashMap<Location,Double>> placeOfWorshipPublicTTime = parser.parse(R.raw.placeofworship_bus_time, placeOfWorshipLocations);
        HashMap<String,HashMap<Location,Double>> placeOfWorshipTaxiCost = parser.parse(R.raw.placeofworship_taxi_cost, placeOfWorshipLocations);
        HashMap<String,HashMap<Location,Double>> placeOfWorshipTaxiTime = parser.parse(R.raw.placeofworship_taxi_time, placeOfWorshipLocations);
        HashMap<String,HashMap<Location,Double>> placeOfWorshipWalkingTime = parser.parse(R.raw.placeofworship_walk_time, placeOfWorshipLocations);
        for (HashMap.Entry<String,Location> entry: placeOfWorshipLocations.entrySet()) {
            String locationName = entry.getKey();
            Location location = entry.getValue();
            location.travelTimesPublicT = placeOfWorshipPublicTTime.get(locationName);
            location.travelCostsPublicT = placeOfWorshipPublicTCost.get(locationName);
            location.travelTimesTaxi = placeOfWorshipTaxiTime.get(locationName);
            location.travelCostsTaxi = placeOfWorshipTaxiCost.get(locationName);
            location.travelTimesWalking = placeOfWorshipWalkingTime.get(locationName);
        }
        this.placeOfWorshipLocations = placeOfWorshipLocations;
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

    public void getItineraryButtonListener(View v) {
        String selectedGenre = locationGenreSpinner.getSelectedItem().toString();
        HashMap<String,Location> locationHashMap = null;
        if (selectedGenre.equals(Location.Genre.ENTERTAINMENT)) locationHashMap = entertainmentLocations;
        else if (selectedGenre.equals(Location.Genre.FOOD)) locationHashMap = foodLocations;
        else if (selectedGenre.equals(Location.Genre.MUSEUM)) locationHashMap = museumLocations;
        else if (selectedGenre.equals(Location.Genre.OUTDOOR)) locationHashMap = outdoorLocations;
        else if (selectedGenre.equals(Location.Genre.PLACEOFWORSHIP)) locationHashMap = placeOfWorshipLocations;

        SharedPreferences prefs = getBaseContext().getSharedPreferences(selectedGenre, Context.MODE_PRIVATE);
        ArrayList<Location> locationsToVisit = new ArrayList<>();
        // for the selected genre
        for (String locationName: DataParser.getLocationsFromAssets(getBaseContext(), selectedGenre)) {
            // check all locations in the genre, if selected, add to pool
            if (prefs.getBoolean(locationName, false)) locationsToVisit.add(locationHashMap.get(locationName));
        }
        if (locationsToVisit.size() < 3) itineraryTextView.setText("At least 3 locations are required");
        else {
            Location startingLocation = locationHashMap.get("Marina Bay Sands");
            double budget = Double.valueOf(PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("budget_settings", "20.0"));
            ArrayList<LocationEdge> smartRoute = SmartSolver.solve(startingLocation, startingLocation, locationsToVisit, budget);
            SmartSolver.printRoute(smartRoute);
            itineraryTextView.setText(SmartSolver.routeToString(smartRoute));
        }
    }
}
