package com.tourtrek.utilities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.firestore.GeoPoint;
import com.tourtrek.activities.MainActivity;

import androidx.core.app.ActivityCompat;

import static androidx.test.InstrumentationRegistry.getContext;
import static com.tourtrek.utilities.Firestore.updateUser;

public class MapsUtilities {
    private static String TAG = "MapsUtilities";

    /**
     * Use this method to update the current user's location
     * @param locationClient
     * @return GeoPoint with coordinates for use by Maps
     */
    public static void getUserLastLocation(FusedLocationProviderClient locationClient) {

        Log.d(TAG, "Updating user location.");

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Location location = task.getResult();
                MainActivity.user.setGeoLocation(new GeoPoint(location.getLatitude(), location.getLongitude()));
                updateUser();
            }
        });
    }

}
