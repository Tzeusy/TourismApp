package com.example.tze.tourismapptwo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Message;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class MyPreferenceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String email = prefs.getString("email","");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        email = email.replace(".","");
        DatabaseReference myRef = database.getReference(email);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d("Please",dataSnapshot.toString());
                boolean nightmode=false;
                boolean notifications=false;
                String language="English";
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    String information = data.getValue().toString();
                    Log.d("Please",data.getValue().toString());
                    information = information.replace("{","");
                    information = information.replace("}","");
                    String[] categories = information.split(",");
                    for(String s:categories){
                        Log.d("Please",s);
                    }
                    if(categories[0].contains("true")){
                        nightmode=true;
                    }
                    if(categories[1].contains("true")){
                        notifications=true;
                    }
                    if(categories[2].contains("Malay")){
                        language="Malay";
                    }
                    else if(categories[2].contains("Chinese")){
                        language="Chinese";
                    }
                }
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("night_mode",nightmode);
                editor.putBoolean("notifications_setting",notifications);
                editor.putString("language_setting",language);
                editor.commit();

//                HashMap<String,Object> value = dataSnapshot.getValue(HashMap.class);
//                Log.d("Please","Value is:"+value.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Please","Failed to read value.");
            }
        });


//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString("night_mode",myRef.getKey());

        boolean night_mode = prefs.getBoolean("night_mode",false);
        if(night_mode){
            setTheme(R.style.PreferencesThemeDark);
        }
        else{
            setTheme(R.style.PreferencesThemeLight);
        }
    }

    static public class PrefsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Please", "Pausing Preferences");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String email = prefs.getString("email","");
        email = email.replace(".","");
        DatabaseReference myRef = database.getReference(email);
        myRef.setValue(prefs);
    }
}