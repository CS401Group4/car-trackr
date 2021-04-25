package com.example.cartrackr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Main Activity class to show Sign In and Register button
 * @since April 2021
 * @author Group 4
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Override method for onCreate
     * @param savedInstanceState Bundle instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_main);
    }

    /**
     * Method to launch Register activity
     * @param view View instance
     */
    public void launchRegisterActivity(View view) {
        Intent intent = new Intent(this, RegistrationPage.class);
        startActivity(intent);
    }

    /**
     * Method to launch Sign In activity
     * @param view View instance
     */
    public void launchSignInActivity(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }
}