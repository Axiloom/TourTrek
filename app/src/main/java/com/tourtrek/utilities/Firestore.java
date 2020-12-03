package com.tourtrek.utilities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tourtrek.activities.MainActivity;

import java.util.Objects;

public class Firestore {

    private static final String TAG = "Firestore";

    public static void updateUser() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        db.collection("Users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .set(MainActivity.user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User document successfully updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating users document", e);
                    }
                });

    }

    public static void updateUser(String userUID) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(userUID)
                .set(MainActivity.user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User: " + userUID + " successfully updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating user: " + userUID, e);
                    }
                });

    }
}
