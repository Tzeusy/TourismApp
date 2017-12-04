package com.example.tze.tourismapptwo;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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

    private ImageView locationImageView;
    private TextView locationHeaderTextView;
    private TextView locationDataTextView;
    private RecyclerView locationRecyclerView;
    private LocationAdapter locationAdapter;
    private ArrayList<String> locations;
    private String genre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selection_page);

        locationImageView = (ImageView)findViewById(R.id.location_image_view);
        locationHeaderTextView = (TextView)findViewById(R.id.location_header_text_view);
        locationDataTextView = (TextView)findViewById(R.id.location_data_text_view);

        genre = getIntent().getStringExtra("genre");
        // doing RecyclerView things
        locations = DataParser.getLocationsFromAssets(getBaseContext(), genre);
        locations.remove("Marina Bay Sands");
        locationRecyclerView = (RecyclerView)findViewById(R.id.location_recycler_view);
        locationAdapter = new LocationAdapter(this, locations, genre, locationHeaderTextView, locationDataTextView, locationImageView);
        locationRecyclerView.setAdapter(locationAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        locationRecyclerView.setLayoutManager(layoutManager);
        // this part adds a lined divider between each item
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(locationRecyclerView.getContext(), layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getApplicationContext().getResources().getDrawable(R.drawable.location_recycler_view_divider));
        locationRecyclerView.addItemDecoration(dividerItemDecoration);
    }
}
