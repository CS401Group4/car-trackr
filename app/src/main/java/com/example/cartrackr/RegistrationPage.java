package com.example.cartrackr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * RegistrationPage Activity to show registration page
 * @since April 2021
 * @author Group 4
 */
public class RegistrationPage extends AppCompatActivity {
    /**
     * String for Log tag
     */
    private static final String TAG = "SignInActivity";
    /**
     * int needed for Google Sign In
     */
    private static final int RC_SIGN_IN = 9001;
    /**
     * Register button
     */
    private Button register;
    /**
     * Full name input
     */
    private EditText fullName;
    /**
     * Email input
     */
    private EditText email;
    /**
     * Email password
     */
    private EditText password;
    /**
     * Instance of FirebaseAuth
     */
    private FirebaseAuth auth;
    /**
     * Sign In button
     */
    private SignInButton signInButton;
    /**
     * GoogleSignInClient instance
     */
    private GoogleSignInClient mSignInClient;
    /**
     * FirebaseAuth instance
     */
    private FirebaseAuth mFirebaseAuth;

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

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_registration_page);

        auth = FirebaseAuth.getInstance();
        register = findViewById(R.id.register_button);
        fullName = findViewById(R.id.full_name);
        email = findViewById(R.id.email_address);
        password = findViewById(R.id.password);
        signInButton = findViewById(R.id.sign_in_button_for_reg);

        // Button click listeners
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        // Click listener for register button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = email.getText().toString();
                String textPassword = password.getText().toString();

                if (TextUtils.isEmpty(textEmail) || TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(RegistrationPage.this, "Email or Password cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(textEmail, textPassword);
                }
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(this, gso);
    }

    /**
     * Override method needed for Google Sign In
     * @param requestCode int variable
     * @param resultCode int variable
     * @param data Intent instance
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent in signIn()
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.w(TAG, account.toString());
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    /**
     * Method to run when clicking on Google Sign In
     * @param acct GoogleSignInAccount instance
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // If sign in succeeds the auth state listener will be notified and logic to
                        // handle the signed in user can be handled in the listener.
                        Log.d(TAG, "signInWithCredential:success");
                        startActivity(new Intent(RegistrationPage.this, AddCarPage.class));
                        finish();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential", e);
                        Toast.makeText(RegistrationPage.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Method to launch Sign In Activity
     */
    private void signIn() {
        Intent signInIntent = mSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Method to run to register user when register button is clicked
     * @param textEmail email value
     * @param textPassword password value
     */
    private void registerUser(String textEmail, String textPassword) {
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegistrationPage.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(RegistrationPage.this, AddCarPage.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegistrationPage.this, "Registration failed. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Method to launch Sign In Activity
     * @param view View instance
     */
    public void launchSignInActivity(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }
}