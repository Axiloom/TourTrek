package com.tourtrek.fragments;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;
import com.google.logging.type.HttpRequest;
import com.tourtrek.R;
import com.tourtrek.activities.MainActivity;
import com.tourtrek.data.Attraction;
import com.tourtrek.viewModels.AttractionViewModel;
import com.tourtrek.viewModels.TourViewModel;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static com.tourtrek.utilities.PlacesLocal.checkLocationPermission;

// separate logic into "if attractionViewModel == null" and "if tourViewModel == null"
// copy logic over for adding markers in a tour
public class MapsFragment extends Fragment {
    private static final String TAG = "MapsFragment";
    AttractionViewModel attractionViewModel;
    TourViewModel tourViewModel;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         *
         * https://stackoverflow.com/questions/29441384/fusedlocationapi-getlastlocation-always-null
         * and
         * https://www.youtube.com/watch?v=ZXXoIDj2pR0&list=PLgCYzUzKIBE-SZUrVOsbYMzH7tPigT3gi&index=6
         *
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {

            // Just a tour is selected
            if (tourViewModel.getSelectedTour() != null && attractionViewModel.getSelectedAttraction() == null){
                addTourMarkers(googleMap);
                if (tourViewModel.getSelectedTour().getAttractions().size() == 0){
                    Toast.makeText(getActivity(), "No were attractions displayed - try adding some!", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // An attraction within a tour is selected
            if (tourViewModel.getSelectedTour() != null && attractionViewModel.getSelectedAttraction() != null){
                addSingleAttractionMarker(googleMap);
                return;
            }

        } // end onMapReady
    }; // end OnMapReadyCallback

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_maps, container, false);
        attractionViewModel = new ViewModelProvider(requireActivity()).get(AttractionViewModel.class);
        tourViewModel = new ViewModelProvider(requireActivity()).get(TourViewModel.class);
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * Helper method for adding a single marker representing the location of the currently selected attraction.
     * @param googleMap
     */
    private void addSingleAttractionMarker(GoogleMap googleMap){
        // location permissions
        checkLocationPermission(getContext());

        // display the user's location, if available
        FusedLocationProviderClient locationProvider = LocationServices.getFusedLocationProviderClient(getContext());

        // set up the location request
        LocationRequest mLocationRequest = LocationRequest.create();

        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
            }
        };

        // update the user's location
        locationProvider.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper()).addOnCompleteListener(v -> {
            // get the location
            locationProvider.getLastLocation().addOnSuccessListener(location -> {
                // console message
                if (location != null){
                    Log.d(TAG, "Latitude" + location.getLatitude() + ", " + "Longitude" + location.getLongitude());

                    // add the current location to the map
                    LatLng start = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(start).title("Starting Location"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(start));

                }
                else{
                    Log.d(TAG, "YOUR CURRENT LOCATION COULD NOT BE FOUND.");
                    Toast.makeText(getActivity(), "Your current location could not be found.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // coder to go back and forth between coordinates and human readable addresses
        Geocoder coder = new Geocoder(getContext());
        try {
            // add the attraction to the map
            Log.d(TAG, attractionViewModel.getSelectedAttraction().getLocation());
            // use the coder to get a list of addresses from the current attraction's location field
            List<Address> attractionAddresses = coder.getFromLocationName(attractionViewModel.getSelectedAttraction().getLocation(),1);
            // pull out the coordinates of the location
            LatLng destination = new LatLng(attractionAddresses.get(0).getLatitude(), attractionAddresses.get(0).getLongitude());
            // add a marker for the attraction location
            googleMap.addMarker(new MarkerOptions().position(destination).title(attractionViewModel.getSelectedAttraction().getName()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(destination));

            // explanation to the user
            Toast.makeText(getActivity(), "Tap on a marker for navigation.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method for displaying every attraction in the current tour
     * @param googleMap
     */
    private void addTourMarkers(GoogleMap googleMap){
        // location permissions
        checkLocationPermission(getContext());

        // display the user's location, if available
        FusedLocationProviderClient locationProvider = LocationServices.getFusedLocationProviderClient(getContext());

        // set up the location request
        LocationRequest mLocationRequest = LocationRequest.create();

        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
            }
        };

        // update the user's location
        locationProvider.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper()).addOnCompleteListener(v -> {
            // get the location
            locationProvider.getLastLocation().addOnSuccessListener(location -> {
                // console message
                if (location != null){
                    Log.d(TAG, "Latitude" + location.getLatitude() + ", " + "Longitude" + location.getLongitude());

                    // add the current location to the map
                    LatLng start = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(start).title("Starting Location"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(start));

                }
                else{
                    Log.d(TAG, "YOUR CURRENT LOCATION COULD NOT BE FOUND.");
                    Toast.makeText(getActivity(), "Your current location could not be found.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // coder to go back and forth between coordinates and human readable addresses
        Geocoder coder = new Geocoder(getContext());
        // get the list of attraction documents and the size so that the last one can be zoomed in on
        List<DocumentReference> attractionRefs = tourViewModel.getSelectedTour().getAttractions();
        int size = attractionRefs.size();
        // add a marker for each attraction in the tour
        for (int i = 0; i < size; i++){
            DocumentReference attractionRef = attractionRefs.get(i);
            attractionRef.get().addOnCompleteListener(task -> {
                // parse the reference into an Attraction object
                Attraction attraction = task.getResult().toObject(Attraction.class);
                // log for debugging
                Log.d(TAG, attraction.getLocation());
                try {
                    // use the coder to get a list of addresses from the current attraction's location field
                    List<Address> attractionAddresses = coder.getFromLocationName(attraction.getLocation(),1);
                    // pull out the coordinates of the location
                    if (attractionAddresses.size() > 0){
                        LatLng destination = new LatLng(attractionAddresses.get(0).getLatitude(), attractionAddresses.get(0).getLongitude());
                        // add a marker for the attraction location
                        googleMap.addMarker(new MarkerOptions().position(destination).title(attraction.getName()));
                        // zoom onto the last marker
                        if (attractionRef == attractionRefs.get(size-1)){
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(destination));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        // explanation to the user
        if (size != 0){
            Toast.makeText(getActivity(), "Tap on a marker for navigation.", Toast.LENGTH_LONG).show();
        }
    }
}