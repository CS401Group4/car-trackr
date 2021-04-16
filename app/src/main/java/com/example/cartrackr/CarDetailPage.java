package com.example.cartrackr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class CarDetailPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail_page);

        Vehicle vehicle = (Vehicle) getIntent().getExtras().getSerializable("DATA");
        Log.i("CAR", vehicle.toString());
    }

    public void launchTaskPage(View view) {
        Intent intent = new Intent(this, TaskPage.class);
        startActivity(intent);
    }
}