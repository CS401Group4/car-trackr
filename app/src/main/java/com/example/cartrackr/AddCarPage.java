package com.example.cartrackr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cartrackr.adapter.MainAdapter;
import com.example.cartrackr.model.Vehicle;
import com.example.cartrackr.util.FirebaseUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smartcar.sdk.SmartcarAuth;
import com.smartcar.sdk.SmartcarCallback;
import com.smartcar.sdk.SmartcarResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.widget.ImageView;

/**
 * AddCarPage class that allows user to add cars from SmartCar API and display
 * them in a RecyclerView
 * @since April 2021
 * @author Group 4
 */
public class AddCarPage extends AppCompatActivity {
    /**
     * Instance of FirebaseAuth
     */
    public static FirebaseAuth mFirebaseAuth;
    /**
     * Instance of GoogleSignInClient
     */
    public static GoogleSignInClient mSignInClient;
    /**
     * Instance of FirebaseFirestore
     */
    private FirebaseFirestore mFirestore;
    /**
     * Instance of Context
     */
    private Context appContext;
    /**
     * String for Smartcar API client id
     */
    private static String CLIENT_ID;
    /**
     * String for Smartcar API redirect uri
     */
    private static String REDIRECT_URI;
    /**
     * String array of Smartcar API scopes
     */
    private static String[] SCOPE;
    /**
     * Instance of SmartcarAuth
     */
    private SmartcarAuth smartcarAuth;
    /**
     * ArrayList of vehicles
     */
    ArrayList<Vehicle> vehicles;
    /**
     * Instance of RecyclerView
     */
    RecyclerView mRecyclerView;
    /**
     * Instance of RecyclerView.LayoutManager
     */
    RecyclerView.LayoutManager mLayoutManager;
    /**
     * Instance of RecyclerView.Adapter
     */
    RecyclerView.Adapter mAdapter;
    /**
     * Instance of ImageView
     */
    private ImageView imageView;

    /**
     * Override method of onCreate
     * @param savedInstanceState Bundle type
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car_page);

        // Setup toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setIcon(R.drawable.carlogonotext);
        int color = Color.parseColor("#259504");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));

        init();
        initSmartCarObject();

        Button connectButton = findViewById(R.id.add_car_btn);

        smartcarAuth.addClickHandler(appContext, connectButton);
    }

    /**
     * Method to initialize variables
     */
    private void init() {
        // Initialize Firebase Auth and check if the user is signed in
        mFirebaseAuth = FirebaseAuth.getInstance();
        if (mFirebaseAuth.getCurrentUser() == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }

        // Initialize Firestore and the main RecyclerView
        mFirestore = FirebaseUtil.getFirestore();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(this, gso);

        vehicles = new ArrayList<>();

        // Setup recycler view
        mRecyclerView = findViewById(R.id.car_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MainAdapter(vehicles);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        // Initiate image view to display when there is no data yet
        imageView = (ImageView) findViewById(R.id.empty_car_image);

        if (vehicles.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }
    }

    /**
     * Method to initiate instance of Smartcar object
     */
    private void initSmartCarObject() {
        // Initialize smartcar object
        appContext = getApplicationContext();
        CLIENT_ID = getString(R.string.client_id);
        REDIRECT_URI = getString(R.string.smartcar_auth_scheme) + "://" + getString(R.string.smartcar_auth_host);
        SCOPE = new String[]{"required:read_vehicle_info", "required:read_location", "read_tires", "read_odometer", "read_battery"};

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

                                // Get reference to collection
                                String userId = FirebaseUtil.getAuth().getCurrentUser().getUid();

                                try {
                                    Response response = client.newCall(infoRequest).execute();
                                    String jsonBody = response.body().string();
                                    JSONObject JObject = new JSONObject(jsonBody);
                                    JSONArray getArray = JObject.getJSONArray("vehicleArray");
                                    String token = JObject.getString("refreshToken");
                                    Map<String, Object> refreshToken = new HashMap<>();
                                    refreshToken.put("refreshToken", token);
                                    mFirestore.collection("users").document(userId)
                                            .set(refreshToken)
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

                                    for (int i = 0; i < getArray.length(); i++) {
                                        JSONObject object = getArray.getJSONObject(i);
                                        JSONObject values = object.getJSONObject("fulfillmentValue");
                                        Log.i("HELLO", values.toString());

                                        String id = values.getString("id");
                                        String make = values.getString("make");
                                        String model = values.getString("model");
                                        String year = values.getString("year");
                                        Vehicle vehicle = new Vehicle(id, make, model, Integer.parseInt(year), token);

                                        Map<String, Object> vehicleMap = new HashMap<>();
                                        vehicleMap.put("make", make);
                                        vehicleMap.put("model", model);
                                        vehicleMap.put("year", year);
                                        mFirestore.collection("users").document(userId)
                                                .collection("vehicles")
                                                .document(id)
                                                .set(vehicleMap)
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
                                        vehicles.add(vehicle);
                                    }

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mRecyclerView.setVisibility(View.VISIBLE);
                                            imageView.setVisibility(View.GONE);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    });
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
    }

    /**
     * Override method needed for custom Toolbar
     * @param menu Menu type
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Override method needed for custom Toolbar
     * @param item MenuItem type
     * @return boolean
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
     * Method to run when clicking sign out
     */
    private void signOut() {
        mFirebaseAuth.signOut();
        mSignInClient.signOut();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}