package com.example.tze.tourismapptwo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Button nearby = findViewById(R.id.Nearby);
        Button itinerary = findViewById(R.id.Itinerary);
        Button settings = findViewById(R.id.About);
//        settings.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                startActivity(new Intent(HomePage.this,SettingsActivity.class));
//            }
//        });
    }

    public void itineraryButtonListener(View v) {
        Context context = v.getContext();
        Intent intent = new Intent(context, ItineraryPage.class);
        startActivity(intent);
    }
}
