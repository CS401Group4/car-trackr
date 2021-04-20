package com.example.cartrackr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CarDetailPage extends AppCompatActivity {
    private int odometer;
    private int batteryRange;
    private double batteryRemaining = 0;
    private long latitude;
    private long longitude;

    ProgressBar progressBar;
    TextView batteryPercentage;

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
        Log.i("CAR", vehicle.getId());

        progressBar = findViewById(R.id.progressBar);
        batteryPercentage = findViewById(R.id.battery_percent);
        updateProgressBar();
        Handler handler = new Handler();

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
                    latitude = location.getLong("latitude");
                    longitude = location.getLong("longitude");
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
        batteryRemaining = batteryRemaining * 100;
        Log.i("CAR_DETAIL", String.valueOf(batteryRemaining));
        progressBar.setProgress((int) batteryRemaining);
        batteryPercentage.setText(String.valueOf(batteryRemaining) + "%");
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
}