package com.example.cartrackr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class RegistrationPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_registration_page);
    }

    public void launchSignInActivity(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }
}