package com.example.tze.tourismapptwo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        Button nearby = findViewById(R.id.Nearby);
        Button itinerary = findViewById(R.id.Itinerary);
        Button about = findViewById(R.id.About);
        about.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(HomePage.this).create();
                alertDialog.setTitle("About");
                alertDialog.setMessage("Welcome to our app! Click on Nearby to see what is near you, " +
                        "or click on Itinerary to choose the places you would like to visit, and we " +
                        "will build your itinerary for you! Have fun and enjoy your stay in Singapore!");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );alertDialog.show();
            }
        });
    }

    public void itineraryButtonListener(View v) {
        Context context = v.getContext();
        Intent intent = new Intent(context, ItineraryPage.class);
        startActivity(intent);
    }

    public void nearbyButtonListener(View v) {
        Context context = v.getContext();
        Intent intent = new Intent(context, NearbyActivity.class);
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "You have selected Settings", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClassName(this, "com.example.tze.tourismapptwo.MyPreferenceActivity");
                startActivity(intent);
                return true;
            case R.id.preference_settings:
//                Toast.makeText(this, "You have selected Preferences", Toast.LENGTH_SHORT).show();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                Toast.makeText(this,prefs.getAll().toString(),Toast.LENGTH_LONG).show();
                boolean night_mode = prefs.getBoolean("night_mode",false);
                if(night_mode){

                }
                return true;
        }
        return false;
    }
}
