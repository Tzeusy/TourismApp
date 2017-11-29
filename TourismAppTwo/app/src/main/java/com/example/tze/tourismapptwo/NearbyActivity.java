package com.example.tze.tourismapptwo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class NearbyActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    boolean setLocation = false;

    EditText searchText;
    double latitude;
    double longitude;
    double mylatitude;
    double mylongitude;

    ArrayList<CheckBox> checkBoxStatuses = new ArrayList<>();
    private int PROXIMITY_RADIUS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        getSupportActionBar().setTitle("Map Location Activity");


        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        CheckBox ShoppingBox = findViewById(R.id.ShoppingBox);
        CheckBox MedicineBox = findViewById(R.id.MedicineBox);
        CheckBox FoodBox = findViewById(R.id.FoodBox);
        CheckBox PetrolBox = findViewById(R.id.PetrolBox);
        CheckBox TransportBox = findViewById(R.id.TransportBox);
        CheckBox GroceriesBox = findViewById(R.id.GroceriesBox);
        CheckBox LandmarksBox = findViewById(R.id.LandmarksBox);
        CheckBox RecreBox = findViewById(R.id.RecreBox);

        checkBoxStatuses.add(ShoppingBox);
        checkBoxStatuses.add(MedicineBox);
        checkBoxStatuses.add(FoodBox);
        checkBoxStatuses.add(PetrolBox);
        checkBoxStatuses.add(TransportBox);
        checkBoxStatuses.add(GroceriesBox);
        checkBoxStatuses.add(LandmarksBox);
        checkBoxStatuses.add(RecreBox);

        searchText = findViewById(R.id.searchText);

        for(final CheckBox x:checkBoxStatuses){
            x.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = "";
                    if(x.toString().contains("Shopping")){
                        s="shopping_mall";
                    }
                    else if(x.toString().contains("Medicine")){
                        s="pharmacy";
                    }
                    else if(x.toString().contains("Petrol")){
                        s="gas_station";
                    }
                    else if(x.toString().contains("Food")){
                        s="restaurant";
                    }
                    else if(x.toString().contains("Transport")){
                        s="train_station";
                    }
                    else if(x.toString().contains("Groceries")){
                        s="store";
                    }
                    else if(x.toString().contains("Landmark")){
                        s="museum";
                    }
                    else if(x.toString().contains("Recre")){
                        s="movie_rental";
                    }
                    if(x.isChecked()){
                        Log.d("Please",x.getId()+" is checked");
                        String url = getUrl(mylatitude, mylongitude, s);
                        Object[] DataTransfer = new Object[2];
                        DataTransfer[0] = mGoogleMap;
                        DataTransfer[1] = url;
                        Log.d("onClick", url);
                        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                        getNearbyPlacesData.execute(DataTransfer);
                        Toast.makeText(NearbyActivity.this,s, Toast.LENGTH_LONG).show();
                    }else{
                        mGoogleMap.clear();
                        Log.d("Please",x.toString()+" is unchecked");
                    }
                }
            });
        }

    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyDxsjM1vQebvoV4EbkiA3hh-NcnQRwPmsE");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    public void onSearch(View view){
        String query = "Singapore "+ searchText.getText().toString();
        List<Address> addressList=null;
        if(!query.equals("")){
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(query,3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("Please",String.valueOf(addressList.size()));
            mGoogleMap.clear();
            for(int i=0;i<addressList.size();i++){
                Address address = addressList.get(i);
                LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
//                mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("Address "+String.valueOf(i)));
                MarkerOptions marker = new MarkerOptions();
                Log.d("Please",latLng.toString());
                marker.position(latLng);
                marker.title("Address "+String.valueOf(i));
                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mGoogleMap.addMarker(marker);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap=googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        mylatitude = location.getLatitude();
        mylongitude = location.getLongitude();

        if(!setLocation){
            //move map camera on startup for the first time
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
            setLocation=true;
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(NearbyActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
