package com.example.cartrackr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CarDetailPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail_page);

        final OkHttpClient client = new OkHttpClient();

        Vehicle vehicle = (Vehicle) getIntent().getExtras().getSerializable("DATA");
        Log.i("CAR", vehicle.getId());

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("CAR_DETAIL", vehicle.getId());
                Request infoRequest = new Request.Builder()
                        .url(getString(R.string.app_server) + "/odometer?vehicleId=" + vehicle.getId())
                        .build();

                try {
                    Response response = client.newCall(infoRequest).execute();
                    String jsonBody = response.body().string();
                    JSONObject JObject = new JSONObject(jsonBody);
                    Log.i("CAR_DETAIL", JObject.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void launchTaskPage(View view) {
        Intent intent = new Intent(this, TaskPage.class);
        startActivity(intent);
    }
}