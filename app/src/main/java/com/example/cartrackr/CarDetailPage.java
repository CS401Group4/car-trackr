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

public class CarDetailPage extends AppCompatActivity implements OnMapReadyCallback {
    private int odometer = 0;
    private int batteryRange = 0;
    private double batteryRemaining = 0;
    private double latitude = 0;
    private double longitude = 0;

    private FirebaseFirestore mFirestore;

    Vehicle vehicle;

    ProgressBar progressBar;
    TextView batteryPercentage;
    TextView rangeLeft;
    TextView milesDriven;
    LatLng position;
    GoogleMap mMap;

    private MapView mapView;

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
                            updateProgressBar();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

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

    private void updateProgressBar() throws IOException {
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

    public void launchTaskPage(View view) {
        Intent intent = new Intent(this, TaskPage.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("DATA", vehicle);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

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

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}