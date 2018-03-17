package com.mediaoasis.trvany.services;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mediaoasis.trvany.activities.user.MainActivity;


/**
 * Created by Nasr on 3/27/2017.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Refreshed token: ", refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.


        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(final String refreshedToken) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        if (firebaseUser != null)
            if (MainActivity.CurrentUser != null) {
                DatabaseReference databaseReference = firebaseDatabase.getReference().getRoot().child("User")
                        .child(firebaseUser.getUid()).child("token");
                databaseReference.setValue(refreshedToken);
            } else {
                DatabaseReference databaseReference = firebaseDatabase.getReference().getRoot().child("Brokers")
                        .child(firebaseUser.getUid()).child("token");
                databaseReference.setValue(refreshedToken);
            }

    }
}
