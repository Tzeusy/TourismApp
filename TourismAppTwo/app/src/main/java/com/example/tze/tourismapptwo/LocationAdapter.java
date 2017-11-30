package com.example.tze.tourismapptwo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private static final String TAG = "LOCATION_ADAPTER";
    private ArrayList<String> locations;
    Context parentContext;

    public LocationAdapter(Context context, ArrayList<String> locations) {
        parentContext = context;
        this.locations = locations;
    }

    @Override
    public LocationAdapter.LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIDForListItem = R.layout.location_recycler_item;
        LayoutInflater inflater = LayoutInflater.from(parentContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIDForListItem, parent, shouldAttachToParentImmediately);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LocationAdapter.LocationViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "LOCATION_VIEW_HOLDER";
        private SharedPreferences prefs;
        private TextView locationTextView;

        LocationViewHolder(View v) {
            super(v);

            prefs = PreferenceManager.getDefaultSharedPreferences(v.getContext());

            locationTextView = (TextView)v.findViewById(R.id.location_recycler_text_view);
            locationTextView.setOnClickListener(this);
        }

        public void bind(int position) {
            String locationName = locations.get(position);
            Boolean isSelected = prefs.getBoolean(locationName, false);
            updateLocationTextView(isSelected);
            locationTextView.setText(locationName);
        }

        @Override
        public void onClick(View v) {
            String locationName = locationTextView.getText().toString(); // get location name
            Boolean isSelected = !prefs.getBoolean(locationName, false); // get new supposed selection
            // commit to preferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(locationName, isSelected);
            editor.commit();
            // update info entry
            if (isSelected) (new GetWikipediaTask()).execute(locationName);
            // update gui
            updateLocationTextView(isSelected);
        }

        private void updateLocationTextView(Boolean isSelected) {
            if (isSelected) {
                locationTextView.setBackgroundColor(Color.GRAY);
            }
            else {
                locationTextView.setBackgroundColor(Color.LTGRAY);
            }
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
                LocationSelectionPage.locationDataTextView.setText(extract);
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
}
