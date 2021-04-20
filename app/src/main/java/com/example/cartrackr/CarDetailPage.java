package com.example.cartrackr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CarDetailPage extends AppCompatActivity implements OnMapReadyCallback {
    private int odometer = 0;
    private int batteryRange = 0;
    private double batteryRemaining = 0;
    private double latitude = 0;
    private double longitude = 0;

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

        final OkHttpClient client = new OkHttpClient();

        Vehicle vehicle = (Vehicle) getIntent().getExtras().getSerializable("DATA");
        Log.i("CAR_DETAIL", vehicle.toString());
        getSupportActionBar().setTitle(vehicle.toString());

        progressBar = findViewById(R.id.progressBar);
        batteryPercentage = findViewById(R.id.battery_percent);
        rangeLeft = findViewById(R.id.range_left);
        milesDriven = findViewById(R.id.odomoter_reading);
        mapView = findViewById(R.id.mapView);
//        updateProgressBar();
        Handler handler = new Handler();

        initGoogleMaps(savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("CAR_DETAIL", vehicle.getId());
                Request infoRequest = new Request.Builder()
                        .url(getString(R.string.app_server) + "/getVehicleData?vehicleId=" + vehicle.getId())
                        .build();

                try {
                    Response response = client.newCall(infoRequest).execute();
                    String jsonBody = response.body().string();
                    JSONObject JObject = new JSONObject(jsonBody);
                    Log.i("CAR_DETAIL", JObject.toString());

                    JSONObject distance = new JSONObject(JObject.getString("distance"));
                    odometer = distance.getInt("distance");

                    JSONObject battery = new JSONObject(JObject.getString("batteryLevel"));
                    batteryRange = battery.getInt("range");
                    batteryRemaining = battery.getDouble("percentRemaining");

                    JSONObject location = new JSONObject(JObject.getString("location"));
                    latitude = location.getDouble("latitude");
                    longitude = location.getDouble("longitude");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateProgressBar();
                    }
                });
            }
        }).start();
    }

    private void updateProgressBar() {
        batteryRemaining = Math.rint(batteryRemaining * 100);
        progressBar.setProgress((int) batteryRemaining);
        batteryPercentage.setText(String.valueOf((int) batteryRemaining) + "%");
        rangeLeft.setText(String.valueOf(batteryRange));
        milesDriven.setText(NumberFormat.getIntegerInstance().format((int) odometer));

        position = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(position).title("Current location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
    }

    public void launchTaskPage(View view) {
        Intent intent = new Intent(this, TaskPage.class);
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