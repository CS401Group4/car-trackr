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

public class MainActivity extends AppCompatActivity {
    // Firebase instance variables
    public static FirebaseAuth mFirebaseAuth;

    public static GoogleSignInClient mSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth and check if the user is signed in
        mFirebaseAuth = FirebaseAuth.getInstance();
        if (mFirebaseAuth.getCurrentUser() == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }

        Intent intent = new Intent(this, AddCarPage.class);
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

    public void launchActivity(View view) {
        Intent intent = new Intent(this, AddCarPage.class);
        startActivity(intent);
    }

    public void signOut() {
        mFirebaseAuth.signOut();
        mSignInClient.signOut();
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }
}