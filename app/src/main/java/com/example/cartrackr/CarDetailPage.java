package com.example.cartrackr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cartrackr.model.Vehicle;
import com.example.cartrackr.util.FirebaseUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * CarDetailPage class that shows single car data
 * @since April 2021
 * @author Group 4
 */
public class CarDetailPage extends AppCompatActivity implements OnMapReadyCallback {
    /**
     * int of car's odometer reading
     */
    private int odometer = 0;
    /**
     * int of car's battery range
     */
    private int batteryRange = 0;
    /**
     * double of car's remaining battery level
     */
    private double batteryRemaining = 0;
    /**
     * double of car's latitude location
     */
    private double latitude = 0;
    /**
     * double of car's longitude location
     */
    private double longitude = 0;
    /**
     * Instance of FirebaseFirestore
     */
    private FirebaseFirestore mFirestore;
    /**
     * Instance of Vehicle class
     */
    Vehicle vehicle;
    /**
     * ProgressBar to show remaining battery
     */
    ProgressBar progressBar;
    /**
     * TextView to show remaining battery percentage
     */
    TextView batteryPercentage;
    /**
     * TextView to show range left (in miles)
     */
    TextView rangeLeft;
    /**
     * TextView to show odometer reading
     */
    TextView milesDriven;
    /**
     * Instance of LatLng showing car location
     */
    LatLng position;
    /**
     * Instance of GoogleMap
     */
    GoogleMap mMap;
    /**
     * Instance of MapView
     */
    private MapView mapView;

    /**
     * Override method of onCreate
     * @param savedInstanceState Bundle instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail_page);

        // Setup toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setIcon(R.drawable.carlogonotext);
        int color = Color.parseColor("#259504");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));

        // Initialize instance to make API calls
        final OkHttpClient client = new OkHttpClient();

        // Initialize Firestore and the main RecyclerView
        mFirestore = FirebaseUtil.getFirestore();

        // Retrieve vehicle data to display in view
        vehicle = (Vehicle) getIntent().getExtras().getSerializable("DATA");
        Log.i("CAR_DETAIL", vehicle.toString());
        getSupportActionBar().setTitle(vehicle.toString());

        // Initialize the view components
        progressBar = findViewById(R.id.progressBar);
        batteryPercentage = findViewById(R.id.battery_percent);
        rangeLeft = findViewById(R.id.range_left);
        milesDriven = findViewById(R.id.odomoter_reading);
        mapView = findViewById(R.id.mapView);

        // Need a handler to update view from new Thread
        Handler handler = new Handler();

        // Start the Google Maps render
        initGoogleMaps(savedInstanceState);

        /**
         * Initiate a separate thread to perform an API call to retrieve vehicle data
         * (e.g. Battery Level, Odometer, Location)
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Create an instance of a Request class for calling API
                Log.i("CAR_DETAIL", vehicle.getId());
                Request infoRequest = new Request.Builder()
                        .url(getString(R.string.app_server) + "/getVehicleData?vehicleId=" + vehicle.getId())
                        .build();

                try {
                    // If successful, retrieve JSON object from API request
                    // else handle error in the catch block
                    Response response = client.newCall(infoRequest).execute();
                    String jsonBody = response.body().string();
                    JSONObject JObject = new JSONObject(jsonBody);
                    Log.i("CAR_DETAIL", JObject.toString());

                    // Retrieve key property values from JSON object and assign
                    // to vehicle global data variables
                    JSONObject distance = new JSONObject(JObject.getString("distance"));
                    odometer = distance.getInt("distance");

                    JSONObject battery = new JSONObject(JObject.getString("batteryLevel"));
                    batteryRange = battery.getInt("range");
                    batteryRemaining = battery.getDouble("percentRemaining");

                    JSONObject location = new JSONObject(JObject.getString("location"));
                    latitude = location.getDouble("latitude");
                    longitude = location.getDouble("longitude");

                    saveDataToFireStore();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Update the view from this separate thread using
                // the Handler instance
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            updateCarData();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * Method to save data to FireStore
     */
    private void saveDataToFireStore() {
        Map<String, Object> vehicleData = new HashMap<>();
        vehicleData.put("odometer", odometer);
        vehicleData.put("batteryRange", batteryRange);
        vehicleData.put("batteryRemaining", batteryRemaining);
        vehicleData.put("latitude", latitude);
        vehicleData.put("longitude", longitude);

        String id = vehicle.getId();
        String userId = FirebaseUtil.getAuth().getCurrentUser().getUid();

        mFirestore.collection("users").document(userId)
                .collection("vehicles")
                .document(id)
                .update(vehicleData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DOC", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DOC", "Error writing document", e);
                    }
                });
    }

    /**
     * Method to update car's data
     * @throws IOException
     */
    private void updateCarData() throws IOException {
        batteryRemaining = Math.rint(batteryRemaining * 100);
        progressBar.setProgress((int) batteryRemaining);
        batteryPercentage.setText(String.valueOf((int) batteryRemaining) + "%");
        rangeLeft.setText(String.valueOf(batteryRange));
        milesDriven.setText(NumberFormat.getIntegerInstance().format((int) odometer));

        position = new LatLng(latitude, longitude);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
        String locationString = addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea();
        mMap.addMarker(new MarkerOptions().position(position).title(locationString));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
    }

    /**
     * Method to go to TaskPage Activity when Schedule Task button is clicked
     * @param view View instance
     */
    public void launchTaskPage(View view) {
        Intent intent = new Intent(this, TaskPage.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("DATA", vehicle);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Override method needed for custom Toolbar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Override method needed for custom Toolbar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method to run when clicking Sign Out
     */
    private void signOut() {
        AddCarPage.mFirebaseAuth.signOut();
        AddCarPage.mSignInClient.signOut();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    /**
     * Methods to display Map
     */
    private void initGoogleMaps(Bundle savedInstanceState) {
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);
    }

    /**
     * Override method needed for displaying map
     * @param map GoogleMap instance
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
    }

    /**
     * Override method needed for displaying map
     */
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    /**
     * Override method needed for displaying map
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * Override method needed for displaying map
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * Override method needed for displaying map
     */
    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    /**
     * Override method needed for displaying map
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * Override method needed for displaying map
     * @param outState Bundle instance
     * @param outPersistentState PersistableBundle instance
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * Override method needed for displaying map
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}