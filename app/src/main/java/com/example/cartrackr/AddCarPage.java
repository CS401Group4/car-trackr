package com.example.cartrackr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.smartcar.sdk.SmartcarAuth;
import com.smartcar.sdk.SmartcarCallback;
import com.smartcar.sdk.SmartcarResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.os.Bundle;

public class AddCarPage extends AppCompatActivity {
    private Context appContext;
    private static String CLIENT_ID;
    private static String REDIRECT_URI;
    private static String[] SCOPE;
    private SmartcarAuth smartcarAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car_page);

        // Initialize smartcar object
        appContext = getApplicationContext();
        CLIENT_ID = getString(R.string.client_id);
        REDIRECT_URI = getString(R.string.smartcar_auth_scheme) + "://" + getString(R.string.smartcar_auth_host);
        SCOPE = new String[]{"required:read_vehicle_info"};

        smartcarAuth = new SmartcarAuth(
                CLIENT_ID,
                REDIRECT_URI,
                SCOPE,
                true,
                new SmartcarCallback() {
                    // Authorization Step 3b: Receive an authorization code
                    @Override
                    public void handleResponse(final SmartcarResponse smartcarResponse) {
                        Log.i("AddCarPage", smartcarResponse.getCode());

                        final OkHttpClient client = new OkHttpClient();

                        // Request can not run on the Main Thread
                        // Main Thread is used for UI and therefore can not be blocked
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                // send request to exchange the auth code for the access token
                                Request exchangeRequest = new Request.Builder()
                                        // Android emulator runs in a VM, therefore localhost will be the
                                        // emulator's own loopback address
                                        .url(getString(R.string.app_server) + "/exchange?code=" + smartcarResponse.getCode())
                                        .build();

                                try {
                                    client.newCall(exchangeRequest).execute();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                // send request to retrieve the vehicle info
                                Request infoRequest = new Request.Builder()
                                        .url(getString(R.string.app_server) + "/vehicle")
                                        .build();

                                try {
                                    Response response = client.newCall(infoRequest).execute();

                                    String jsonBody = response.body().string();
                                    JSONObject JObject = new JSONObject(jsonBody);

                                    String make = JObject.getString("make");
                                    String model = JObject.getString("model");
                                    String year = JObject.getString("year");

                                    Intent intent = new Intent(appContext, AddCarPage.class);
                                    intent.putExtra("INFO", make + " " + model + " " + year);
                                    startActivity(intent);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
        );

        Button connectButton = findViewById(R.id.add_car_btn);

        smartcarAuth.addClickHandler(appContext, connectButton);
    }
}