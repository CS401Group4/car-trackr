package com.example.cartrackr.util;

import com.example.cartrackr.BuildConfig;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Utility class for initializing Firebase services and connecting them to the Firebase Emulator
 * Suite if necessary.
 * @since April 2021
 * @author Group 4
 */
public class FirebaseUtil {
    /** Use emulators only in debug builds **/
    private static final boolean sUseEmulators = BuildConfig.DEBUG;

    /**
     * Instance of FirebaseFirestore
     */
    private static FirebaseFirestore FIRESTORE;
    /**
     * Instance of FirebaseAuth
     */
    private static FirebaseAuth AUTH;
    /**
     * Instance of AuthUI
     */
    private static AuthUI AUTH_UI;

    /**
     * Method to get FirebaseFirestore instance
     * @return FirebaseFirestore instance
     */
    public static FirebaseFirestore getFirestore() {
        if (FIRESTORE == null) {
            FIRESTORE = FirebaseFirestore.getInstance();

            // Connect to the Cloud Firestore emulator when appropriate. The host '10.0.2.2' is a
            // special IP address to let the Android emulator connect to 'localhost'.
            if (sUseEmulators) {
                FIRESTORE.useEmulator("10.0.2.2", 8080);
            }
        }

        return FIRESTORE;
    }

    /**
     * Method to get FirebaseAuth instance
     * @return FirebaseAuth instance
     */
    public static FirebaseAuth getAuth() {
        if (AUTH == null) {
            AUTH = FirebaseAuth.getInstance();

            // Connect to the Firebase Auth emulator when appropriate. The host '10.0.2.2' is a
            // special IP address to let the Android emulator connect to 'localhost'.
            if (sUseEmulators) {
                AUTH.useEmulator("10.0.2.2", 9099);
            }
        }

        return AUTH;
    }

    /**
     * Method to get AuthUI instance
     * @return AuthUI instance
     */
    public static AuthUI getAuthUI() {
        if (AUTH_UI == null) {
            AUTH_UI = AuthUI.getInstance();

            // Connect to the Firebase Auth emulator when appropriate. The host '10.0.2.2' is a
            // special IP address to let the Android emulator connect to 'localhost'.
            if (sUseEmulators) {
                AUTH_UI.useEmulator("10.0.2.2", 9099);
            }
        }

        return AUTH_UI;
    }
}
