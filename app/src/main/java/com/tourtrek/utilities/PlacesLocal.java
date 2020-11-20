package com.tourtrek.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.tourtrek.activities.MainActivity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PlacesLocal {

    private static final String TAG = "PlacesLocal";
    private static final String API_KEY = "AIzaSyBeaBaWXpKmDFKFzFnzjAi3mlaf89eI8XY";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static PlacesClient placesClient;

    public static void getNearbyPlaces(Context context) {

        // Initialize api
        Places.initialize(context, API_KEY);
        PlacesClient placesClient = Places.createClient(context);

        List<Place.Field> placesFields = Collections.singletonList(Place.Field.NAME);

        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placesFields);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponseTask = placesClient.findCurrentPlace(request);
            placeResponseTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FindCurrentPlaceResponse response = task.getResult();
                    for(PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {

                        Place place = placeLikelihood.getPlace();

                        System.out.println(place.getAddress());

                        Log.i(TAG, String.format("Place '%s' has likelihood: %f" ,
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));

                    }
                }
                else {
                    Exception exception = task.getException();

                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                }
            });
        }
        else {
            checkLocationPermission(context);
        }
    }

    public static void getPlaceByID(Context context, String placeId) {

        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

        // Construct a request object, passing the place ID and fields array
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            Log.i(TAG, "Place found: Name-" + place.getName() + " Address-" + place.getAddress());
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
            }
        });
    }

    public static boolean checkLocationPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(context)
                        .setTitle("Requesting Location Permission")
                        .setMessage("Requesting Location Permission Text")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                // Prompt the user one explanation has been shown
                                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            }
            else {
                // No explanation needed, we can request the permission
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
        else {
            return true;
        }
    }

    public static void initialize(Context context){
        // Initialize api
        Places.initialize(context, API_KEY);
        placesClient = Places.createClient(context);
    }
}
