package com.example.cartrackr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CarDetailPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail_page);
    }

    public void launchTaskPage(View view) {
        Intent intent = new Intent(this, TaskPage.class);
        startActivity(intent);
    }
}