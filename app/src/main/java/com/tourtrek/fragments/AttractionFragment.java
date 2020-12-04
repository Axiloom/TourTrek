package com.tourtrek.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tourtrek.R;
import com.tourtrek.activities.MainActivity;
import com.tourtrek.adapters.CurrentPersonalToursAdapter;
import com.tourtrek.data.Attraction;
import com.tourtrek.notifications.AlarmBroadcastReceiver;
import com.tourtrek.utilities.PlacesLocal;
import com.tourtrek.utilities.Weather;
import com.tourtrek.viewModels.AttractionViewModel;
import com.tourtrek.viewModels.TourViewModel;
import com.tourtrek.data.Tour;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import org.w3c.dom.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * This fragment corresponds to the user story for creating a custom attraction.
 * It runs when a user selects the 'add attraction' option from within the fragment showing the list of attractions in a selected tour.
 * The fragment will consist of a form with text fields corresponding to Attraction variables to fill in and a button to collect
 * the contents of them and push the information to Firestore.
 *
 * TODO fix tapping the back button when in a Google Map leading to the add attraction screen
 * TODO make sure that this location is accessible via the view model
 */
public class AttractionFragment extends Fragment {

    private static final String TAG = "AttractionFragment";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 4588;
    private EditText locationEditText;
    private EditText costEditText;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private TextView coverTextView;
    private TextView weatherTextView;
    private Button startDateButton;
    private Button startTimeButton;
    private Button endDateButton;
    private Button endTimeButton;
    private Button updateAttractionButton;
    private Button deleteAttractionButton;
    private ImageButton searchAttractionButton;
    private LinearLayout buttonsContainer;
    private TourViewModel tourViewModel;
    private AttractionViewModel attractionViewModel;
    private ImageView coverImageView;
    private Button navigationAttractionButton;
    private FusedLocationProviderClient locationClient;
    // To keep track of whether we are in an async call
    private boolean loading;
    // To keep track of whether tour ticket dialog is showing
    private boolean dialogIsShowing;
    private ImageButton rate;
    private Button addTicketButton;
    ImageView ticketImageView;
    Button backButton;
    Button confirmButton;
    Dialog dialog;

    /**
     * Default for proper back button usage
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!loading)
                    getParentFragmentManager().popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Grab a reference to the current view
        View attractionView = inflater.inflate(R.layout.fragment_attraction, container, false);

        // Initialize tourViewModel to get the current tour
        tourViewModel = new ViewModelProvider(requireActivity()).get(TourViewModel.class);

        // Initialize attractionMarketViewModel to get the current attraction
        attractionViewModel = new ViewModelProvider(requireActivity()).get(AttractionViewModel.class);

        //add ticket button
        addTicketButton = attractionView.findViewById(R.id.attraction_add_ticket_btn);

        if (attractionViewModel.isNewAttraction() || attractionViewModel.getSelectedAttraction() == null) {
            addTicketButton.setVisibility(View.GONE);
        }

        addTicketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTicketDialog();
            }
        });

        //review button
        rate = attractionView.findViewById(R.id.attraction_review_btn);

        if (attractionViewModel.isNewAttraction() || attractionViewModel.getSelectedAttraction() == null) {
            rate.setVisibility(View.GONE);
        }
        rate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                if (!attractionViewModel.getSelectedAttraction().getReviews()
                        .contains(mAuth.getCurrentUser().getUid())) { showReviewDialog();
                } else {
                    Toast.makeText(getContext(), "You cannot rate an attraction more than once", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Initialize all fields
        nameEditText = attractionView.findViewById(R.id.attraction_name_et);
        locationEditText = attractionView.findViewById(R.id.attraction_location_et);
        costEditText = attractionView.findViewById(R.id.attraction_cost_et);
        startDateButton = attractionView.findViewById(R.id.attraction_start_date_btn);
        startTimeButton = attractionView.findViewById(R.id.attraction_start_time_btn);
        endDateButton = attractionView.findViewById(R.id.attraction_end_date_btn);
        endTimeButton = attractionView.findViewById(R.id.attraction_end_time_btn);
        descriptionEditText = attractionView.findViewById(R.id.attraction_description_et);
        coverImageView = attractionView.findViewById(R.id.attraction_cover_iv);
        coverTextView = attractionView.findViewById(R.id.attraction_cover_tv);
        updateAttractionButton = attractionView.findViewById(R.id.attraction_update_btn);
        deleteAttractionButton = attractionView.findViewById(R.id.attraction_delete_btn);
        navigationAttractionButton = attractionView.findViewById(R.id.attraction_navigation_btn);
//        buttonsContainer = attractionView.findViewById(R.id.attraction_buttons_container);
        searchAttractionButton = attractionView.findViewById(R.id.attraction_search_ib);

        weatherTextView = attractionView.findViewById(R.id.attraction_weather_tv);

        searchAttractionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAutoCompleteActivity(attractionView);
            }
        });

        nameEditText.setEnabled(false);
        locationEditText.setEnabled(false);
        costEditText.setEnabled(false);
        startDateButton.setEnabled(false);
        startTimeButton.setEnabled(false);
        endDateButton.setEnabled(false);
        endTimeButton.setEnabled(false);
        coverImageView.setClickable(false);
        coverTextView.setVisibility(View.GONE);
//        buttonsContainer.setVisibility(View.GONE);

        // no attraction selected -> new one
        if (attractionViewModel.getSelectedAttraction() == null) {

            attractionViewModel.setSelectedAttraction(new Attraction());

            // set up fields to be made visible since we are creating a new tour
            nameEditText.setEnabled(true);
//            locationEditText.setEnabled(true);
            costEditText.setEnabled(true);
            startDateButton.setEnabled(true);
            startTimeButton.setEnabled(true);
            endDateButton.setEnabled(true);
            endTimeButton.setEnabled(true);
            endTimeButton.setVisibility(View.VISIBLE);
            coverImageView.setVisibility(View.VISIBLE);
            coverTextView.setVisibility(View.VISIBLE);
            descriptionEditText.setVisibility(View.VISIBLE);
//            buttonsContainer.setVisibility(View.VISIBLE);

            updateAttractionButton.setText("Add Attraction");

            attractionViewModel.setIsNewAttraction(true);

            attractionIsUsers();
        }
        else { // attraction selected -> existing one

            attractionIsUsers();

            // Set all the fields
            if (attractionViewModel.getSelectedAttraction().getName() != null)
                nameEditText.setText(attractionViewModel.getSelectedAttraction().getName());
            if (attractionViewModel.getSelectedAttraction().getLocation() != null)
                locationEditText.setText(attractionViewModel.getSelectedAttraction().getLocation());
            costEditText.setText(String.format("$%.2f", attractionViewModel.getSelectedAttraction().getCost()));
            if (attractionViewModel.getSelectedAttraction().getStartDate() != null)
                startDateButton.setText(attractionViewModel.getSelectedAttraction().retrieveStartDateAsString());
            if (attractionViewModel.getSelectedAttraction().getStartTime() != null)
                startTimeButton.setText(attractionViewModel.getSelectedAttraction().getStartTime());
            if (attractionViewModel.getSelectedAttraction().getEndDate() != null)
                endDateButton.setText(attractionViewModel.getSelectedAttraction().retrieveEndDateAsString());
            if (attractionViewModel.getSelectedAttraction().getEndTime() != null)
                endTimeButton.setText(attractionViewModel.getSelectedAttraction().getEndTime());
            if (attractionViewModel.getSelectedAttraction().getDescription() != null)
                descriptionEditText.setText(attractionViewModel.getSelectedAttraction().getDescription());
            updateAttractionButton.setText("Update Attraction");

            if (attractionViewModel.getSelectedAttraction().getLon() != 0 && attractionViewModel.getSelectedAttraction().getLat() != 0) {

                // Get updated weather
                Weather.getWeather(attractionViewModel.getSelectedAttraction().getLat(), attractionViewModel.getSelectedAttraction().getLon(), getContext());

                // get temperature
                // Wait for the weather api to receive the data
                if (attractionViewModel.getSelectedAttraction().getWeather() != null && attractionViewModel.getSelectedAttraction().getStartDate() != null) {

                    for (Map.Entry<String, String> entry : attractionViewModel.getSelectedAttraction().getWeather().entrySet()) {
                        String aDateString = entry.getKey();

                        java.text.DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");

                        Calendar calendar = Calendar.getInstance();

                        try {
                            Date aDate = formatter.parse(aDateString);
                            calendar.setTime(aDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error converting string date");
                        }

                        String temperature = entry.getValue();

                        int aMonth = calendar.get(Calendar.MONTH);
                        int aDay = calendar.get(Calendar.DAY_OF_MONTH);
                        int aYear = calendar.get(Calendar.YEAR);

                        Calendar newCalendar = Calendar.getInstance();

                        newCalendar.setTime(attractionViewModel.getSelectedAttraction().getStartDate());

                        int startMonth = newCalendar.get(Calendar.MONTH);
                        int startDay = newCalendar.get(Calendar.DAY_OF_MONTH);
                        int startYear = newCalendar.get(Calendar.YEAR);

                        if (aMonth == startMonth && aDay == startDay && aYear == startYear) {
                            weatherTextView.setText(String.format("%s ℉", temperature));
                            break;
                        }
                        else
                            weatherTextView.setText("N/A");

                    }
                }

            }

            LinearLayout loadingContainer = attractionView.findViewById(R.id.attraction_cover_loading_container);
            loadingContainer.setVisibility(View.VISIBLE);
            ((MainActivity)requireActivity()).disableTabs();
            loading = true;

            Glide.with(getContext())
                    .load(attractionViewModel.getSelectedAttraction().getCoverImageURI())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            LinearLayout loadingContainer = attractionView.findViewById(R.id.attraction_cover_loading_container);
                            loadingContainer.setVisibility(View.INVISIBLE);
                            ((MainActivity)requireActivity()).enableTabs();
                            loading = false;
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            LinearLayout loadingContainer = attractionView.findViewById(R.id.attraction_cover_loading_container);
                            loadingContainer.setVisibility(View.INVISIBLE);
                            try {
                                ((MainActivity)requireActivity()).enableTabs();
                            }
                            catch (java.lang.IllegalStateException e){
                                Log.d("AttractionFragment", "Not associated with an activity.");
                            }
                            loading = false;
                            return false;
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.default_image)
                    .into(coverImageView);

        }

        nameEditText.setOnFocusChangeListener((view, hasFocus) -> {

            if (nameEditText.getHint().equals("Attraction Name")) {
                nameEditText.setHint("");
            }

            nameEditText.setBackgroundColor(Color.parseColor("#10000000"));

            if (!hasFocus && nameEditText.getHint().equals("")) {
                if (nameEditText.getText().toString().equals("")) {
                    nameEditText.setHint("Attraction Name");
                    nameEditText.setBackgroundColor(Color.parseColor("#FF4859"));
                }
            }
        });

        locationEditText.setOnFocusChangeListener((view, hasFocus) -> {

            if (locationEditText.getHint().equals("City, State")) {
                locationEditText.setHint("");
            }

            locationEditText.setBackgroundColor(Color.parseColor("#10000000"));

            if (!hasFocus && locationEditText.getHint().equals("")) {
                if (locationEditText.getText().toString().equals("")) {
                    locationEditText.setHint("City, State");
                    locationEditText.setBackgroundColor(Color.parseColor("#FF4859"));
                }
            }
        });

        costEditText.setOnFocusChangeListener((view, hasFocus) -> {
            if (costEditText.getHint().equals("$0.00")) {
                costEditText.setHint("");
            }

            costEditText.setBackgroundColor(Color.parseColor("#10000000"));

            if (!hasFocus && costEditText.getHint().equals("")) {
                if (costEditText.getText().toString().equals("")) {
                    costEditText.setHint("$0.00");
                    costEditText.setBackgroundColor(Color.parseColor("#FF4859"));
                }
            }
        });

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get the most recent weather data
                if (attractionViewModel.getSelectedAttraction().getLat() != 0 && attractionViewModel.getSelectedAttraction().getLon() != 0) {
                    Weather.getWeather(attractionViewModel.getSelectedAttraction().getLat(), attractionViewModel.getSelectedAttraction().getLon(), getContext());
                }

                showDatePickerDialog(startDateButton, weatherTextView, getContext(), "start");
            }
        });

        startDateButton.setOnFocusChangeListener((view, hasFocus) -> {

            if (startDateButton.getHint().equals("Pick Date")) {
                startDateButton.setHint("");
            }

            startDateButton.setBackgroundColor(Color.parseColor("#10000000"));

            if (!hasFocus && startDateButton.getHint().equals("")) {
                if (startDateButton.getText().toString().equals("")) {
                    startDateButton.setHint("Pick Date");
                    startDateButton.setBackgroundColor(Color.parseColor("#FF4859"));
                }
            }
        });

        startTimeButton.setOnClickListener(view -> ((MainActivity) requireActivity()).showTimePickerDialog(startTimeButton));

        startTimeButton.setOnFocusChangeListener((view, hasFocus) -> {

            if (startTimeButton.getHint().equals("Pick Time")) {
                startTimeButton.setHint("");
            }

            startTimeButton.setBackgroundColor(Color.parseColor("#10000000"));

            if (!hasFocus && startTimeButton.getHint().equals("")) {
                if (startTimeButton.getText().toString().equals("")) {
                    startTimeButton.setHint("Pick Time");
                    startTimeButton.setBackgroundColor(Color.parseColor("#FF4859"));
                }
            }
        });

        endDateButton.setOnClickListener(view -> showDatePickerDialog(endDateButton, null, getContext(), "end"));

        endDateButton.setOnFocusChangeListener((view, hasFocus) -> {

            if (endDateButton.getHint().equals("Pick Date")) {
                endDateButton.setHint("");
            }

            endDateButton.setBackgroundColor(Color.parseColor("#10000000"));

            if (!hasFocus && endDateButton.getHint().equals("")) {
                if (endDateButton.getText().toString().equals("")) {
                    endDateButton.setHint("Pick Date");
                    endDateButton.setBackgroundColor(Color.parseColor("#FF4859"));
                }
            }
        });

        endTimeButton.setOnClickListener(view -> ((MainActivity) requireActivity()).showTimePickerDialog(endTimeButton));

        endTimeButton.setOnFocusChangeListener((view, hasFocus) -> {

            if (endTimeButton.getHint().equals("Pick Time")) {
                endTimeButton.setHint("");
            }

            endTimeButton.setBackgroundColor(Color.parseColor("#10000000"));

            if (!hasFocus && endTimeButton.getHint().equals("")) {
                if (endTimeButton.getText().toString().equals("")) {
                    endTimeButton.setHint("Pick Time");
                    endTimeButton.setBackgroundColor(Color.parseColor("#FF4859"));
                }
            }
        });

        descriptionEditText.setOnFocusChangeListener((view, hasFocus) -> {

            if (descriptionEditText.getHint().equals("Details")) {
                descriptionEditText.setHint("");
            }

            descriptionEditText.setBackgroundColor(Color.parseColor("#10000000"));

            if (!hasFocus && descriptionEditText.getHint().equals("")) {
                if (descriptionEditText.getText().toString().equals("")) {
                    descriptionEditText.setHint("Details");
                    descriptionEditText.setBackgroundColor(Color.parseColor("#FF4859"));
                }
            }
        });

        // set up the action to carry out via the update button
        setupUpdateAttractionButton(attractionView);

        // set up the action to carry out via the delete button
        setupDeleteAttractionButton(attractionView);

        setupNavigationButton();

        return attractionView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (attractionViewModel.getSelectedAttraction() != null) {
            if (attractionViewModel.getSelectedAttraction().getStartDate() != null)
                startDateButton.setText(attractionViewModel.getSelectedAttraction().retrieveStartDateAsString());
            if (attractionViewModel.getSelectedAttraction().getEndDate() != null)
                endDateButton.setText(attractionViewModel.getSelectedAttraction().retrieveEndDateAsString());
        }

        // Add info from searching Google Places API
        if (attractionViewModel.returnedFromSearch()) {

            Glide.with(getContext())
                    .load(attractionViewModel.getSelectedAttraction().getCoverImageURI())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.default_image)
                    .into(coverImageView);

            nameEditText.setText(attractionViewModel.getSelectedAttraction().getName());
            nameEditText.setBackgroundColor(Color.parseColor("#10000000"));
            locationEditText.setText(attractionViewModel.getSelectedAttraction().getLocation());
            locationEditText.setBackgroundColor(Color.parseColor("#10000000"));
            attractionViewModel.setReturnedFromSearch(false);
        }

        if (attractionViewModel.isNewAttraction()){
            ((MainActivity) requireActivity()).setActionBarTitle("Add Attraction");
        }
        else{
            ((MainActivity) requireActivity()).setActionBarTitle(attractionViewModel.getSelectedAttraction().getName());
        }
    }

    @Override
    public void onDestroyView() {

        tourViewModel.setReturnedFromAddAttraction(false);

        if (!attractionViewModel.returnedFromNavigation()) {
            attractionViewModel.setIsNewAttraction(null);
            attractionViewModel.setSelectedAttraction(null);
        }

        attractionViewModel.setReturnedFromNavigation(false);

        super.onDestroyView();
    }

    /**
     * Setup of the onClickListener of the "Navigation" button
     */
    private void setupNavigationButton(){

        navigationAttractionButton.setOnClickListener(v -> {

            // check that location services are enabled and give a prompt to enable them if needed
//            Boolean permissionIsGranted = PlacesLocal.checkLocationPermission(getContext());
//            if (permissionIsGranted){
//                Log.d(TAG, "Location enabled");

                attractionViewModel.setReturnedFromNavigation(true);

                // switch to the map fragment
                final FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment, new MapsFragment(), "MapsFragment");
                ft.addToBackStack("MapsFragment").commit();
//            }
        });
    }

    /**
     * Check if the attraction belongs to the current user and make fields visible if so
     */
    public void attractionIsUsers() {
//        Log.d(TAG, "Checking attraction status..." + "UID " + attractionViewModel.getSelectedAttraction().getAttractionUID() + "user " + MainActivity.user.getUsername());
        // navigation should be available for every attraction in the database
        if (attractionViewModel.getSelectedAttraction().getAttractionUID() != null){
            navigationAttractionButton.setVisibility(View.VISIBLE);
            if (tourViewModel.isUserOwned()){
                deleteAttractionButton.setVisibility(View.VISIBLE);
            }
        }

        // enables updating an attraction when it is part of a tour owned by the user and when it is a new attraction
        if (tourViewModel.isUserOwned() || attractionViewModel.isNewAttraction()){
            nameEditText.setEnabled(true);
//            locationEditText.setEnabled(true);
            costEditText.setEnabled(true);
            startDateButton.setEnabled(true);
            startTimeButton.setEnabled(true);
            endDateButton.setEnabled(true);
            endTimeButton.setEnabled(true);
            coverImageView.setClickable(true);
            coverTextView.setVisibility(View.VISIBLE);
//            buttonsContainer.setVisibility(View.VISIBLE);
            updateAttractionButton.setVisibility(View.VISIBLE);

            // to enable deletion of attractions selected from the tour's recycler view
            if (attractionViewModel.getSelectedAttraction().getAttractionUID() != null){
                deleteAttractionButton.setVisibility((View.VISIBLE));
            }

            coverImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    int PICK_IMAGE = 1;
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {
              
                ((MainActivity)requireActivity()).disableTabs();
                loading = true;

                Place place = Autocomplete.getPlaceFromIntent(data);

                attractionViewModel.getSelectedAttraction().setCoverImageURI("");
                weatherTextView.setText("N/A");

                if (place.getPhotoMetadatas() != null) {
                    PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
                    String attributes = photoMetadata.getAttributions();

                    FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata).build();

                    PlacesClient placesClient = Places.createClient(requireContext());

                    LinearLayout loadingContainer = getActivity().findViewById(R.id.attraction_cover_loading_container);
                    loadingContainer.setVisibility(View.VISIBLE);

                    placesClient.fetchPhoto(photoRequest)
                            .addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                                @Override
                                public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {

                                    Bitmap bitmap = fetchPhotoResponse.getBitmap();

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] data = baos.toByteArray();

                                    // Load image into view
                                    Glide.with(requireContext())
                                            .load(data)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .placeholder(R.drawable.default_image)
                                            .into(coverImageView);

                                    loadingContainer.setVisibility(View.GONE);

                                    ((MainActivity)requireActivity()).enableTabs();
                                    loading = false;

                                    // Upload Image to firestore storage
                                    final FirebaseStorage storage = FirebaseStorage.getInstance();

                                    final UUID imageUUID = UUID.randomUUID();

                                    final StorageReference storageReference = storage.getReference().child("AttractionCoverPictures/" + imageUUID);

                                    final UploadTask uploadTask = storageReference.putBytes(data);

                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            storage.getReference().child("AttractionCoverPictures/" + imageUUID).getDownloadUrl()
                                                    .addOnSuccessListener(uri -> {
                                                        attractionViewModel.getSelectedAttraction().setCoverImageURI(uri.toString());

                                                        Log.i(TAG, "Successfully loaded cover image");

                                                    });
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Failed to upload attraction cover image from Places API");
                                                    ((MainActivity)requireActivity()).enableTabs();
                                                    loading = false;
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Failed to fetch attraction photo from Places API");
                                    ((MainActivity)requireActivity()).enableTabs();
                                    loading = false;
                                }
                            });
                }
                else {

                    ((MainActivity)requireActivity()).enableTabs();
                    loading = false;

                    // Load image into view
                    Glide.with(requireContext())
                            .load(R.drawable.default_image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(coverImageView);
                }

                attractionViewModel.setReturnedFromSearch(true);

                attractionViewModel.getSelectedAttraction().setName(place.getName());
                attractionViewModel.getSelectedAttraction().setLocation(place.getAddress());
                attractionViewModel.getSelectedAttraction().setLat(Objects.requireNonNull(place.getLatLng()).latitude);
                attractionViewModel.getSelectedAttraction().setLon(Objects.requireNonNull(place.getLatLng()).longitude);
                attractionViewModel.getSelectedAttraction().setRating(place.getRating());

                // Get updated weather
                Weather.getWeather(attractionViewModel.getSelectedAttraction().getLat(), attractionViewModel.getSelectedAttraction().getLon(), getContext());

                // get temperature
                // Wait for the weather api to receive the data
                if (attractionViewModel.getSelectedAttraction().getWeather() != null && attractionViewModel.getSelectedAttraction().getStartDate() != null) {

                    for (Map.Entry<String, String> entry : attractionViewModel.getSelectedAttraction().getWeather().entrySet()) {
                        String aDateString = entry.getKey();

                        java.text.DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");

                        Calendar calendar = Calendar.getInstance();

                        try {
                            Date aDate = formatter.parse(aDateString);
                            calendar.setTime(aDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error converting string date");
                        }

                        String temperature = entry.getValue();

                        int aMonth = calendar.get(Calendar.MONTH);
                        int aDay = calendar.get(Calendar.DAY_OF_MONTH);
                        int aYear = calendar.get(Calendar.YEAR);

                        Calendar newCalendar = Calendar.getInstance();

                        newCalendar.setTime(attractionViewModel.getSelectedAttraction().getStartDate());

                        int startMonth = newCalendar.get(Calendar.MONTH);
                        int startDay = newCalendar.get(Calendar.DAY_OF_MONTH);
                        int startYear = newCalendar.get(Calendar.YEAR);

                        if (aMonth == startMonth && aDay == startDay && aYear == startYear) {
                            weatherTextView.setText(String.format("%s ℉", temperature));
                            break;
                        }
                        else
                            weatherTextView.setText("N/A");
                    }
                }
            }
            else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                ((MainActivity)requireActivity()).enableTabs();
                loading = false;
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                // Do Nothing because the user is exiting intent
            }
        }
        else {
            if(resultCode == Activity.RESULT_OK && !dialogIsShowing) {
                assert data != null;

                Glide.with(this)
                        .load(data.getData())
                        .placeholder(R.drawable.default_image)
                        .into(coverImageView);
                uploadImageToDatabase(data);
            }
        }

        if(resultCode == Activity.RESULT_OK) {
            assert data != null;
            if (dialogIsShowing) {
                Glide.with(this)
                        .load(data.getData())
                        .placeholder(R.drawable.default_image)
                        .into(ticketImageView);

                //Write to Firebase only when the user confirm
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uploadTicketToDatabase(data);
                        dialog.dismiss();
                    }
                });
            }

        }

    }

    /**
     * Uploads an image to the Profile Images cloud storage.
     *
     * @param imageReturnedIntent intent of the image being saved
     */
    public void uploadImageToDatabase(Intent imageReturnedIntent) {

        final FirebaseStorage storage = FirebaseStorage.getInstance();

        // Uri to the image
        Uri selectedImage = imageReturnedIntent.getData();

        final UUID imageUUID = UUID.randomUUID();

        final StorageReference storageReference = storage.getReference().child("AttractionCoverPictures/" + imageUUID);

        final UploadTask uploadTask = storageReference.putFile(selectedImage);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(exception -> Log.e(TAG, "Error adding image: " + imageUUID + " to cloud storage"))
                .addOnSuccessListener(taskSnapshot -> {
                    Log.i(TAG, "Successfully added image: " + imageUUID + " to cloud storage");

                    storage.getReference().child("AttractionCoverPictures/" + imageUUID).getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                attractionViewModel.getSelectedAttraction().setCoverImageURI(uri.toString());
                            })
                            .addOnFailureListener(exception -> {
                                Log.e(TAG, "Error retrieving uri for image: " + imageUUID + " in cloud storage, " + exception.getMessage());
                            });
                });
    }

    /**
     * This methods is usable for both adding a new attraction and updating an existing attraction
     * @param view
     */
    private void setupUpdateAttractionButton(View view){

        updateAttractionButton.setOnClickListener(v -> {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            // first get the information from each EditText
            String name = nameEditText.getText().toString();
            String location = locationEditText.getText().toString();
            String cost = costEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String startDate = startDateButton.getText().toString();
            String startTime = startTimeButton.getText().toString();
            String endDate = endDateButton.getText().toString();
            String endTime = endTimeButton.getText().toString();

            // error-handling of dates
            try {
                Date start = simpleDateFormat.parse(startDate);
                Date end = simpleDateFormat.parse(endDate);
                if (end.compareTo(start) < 0){
                    Toast.makeText(getContext(), "Start dates must be before end dates!", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (name.equals("") ||
                    location.equals("") ||
                    cost.equals("") ||
                    startDate.equals("") ||
                    startTime.equals("") ||
                    endDate.equals("") ||
                    endTime.equals("") ||
                    description.equals("")) {
                Toast.makeText(getContext(), "Not all fields entered", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                attractionViewModel.getSelectedAttraction().setStartDateFromString(startDate);
            } catch (ParseException e) {
                Log.e(TAG, "Error converting endDate to a firebase Timestamp");
                e.printStackTrace();
            }

            // parse dates to firebase format
            try {
                attractionViewModel.getSelectedAttraction().setEndDateFromString(endDate);
            } catch (ParseException e) {
                Log.e(TAG, "Error converting startDate to a firebase Timestamp");
                return;
            }

            attractionViewModel.getSelectedAttraction().setName(name);
            attractionViewModel.getSelectedAttraction().setLocation(location);
            attractionViewModel.getSelectedAttraction().setDescription(description);
            attractionViewModel.getSelectedAttraction().setStartTime(startTime);
            attractionViewModel.getSelectedAttraction().setEndTime(endTime);

            if (tourViewModel.getSelectedTour().getStartDate() != null && tourViewModel.getSelectedTour().getEndDate() != null){
                // Check that the attraction lies within the tour dates
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(attractionViewModel.getSelectedAttraction().getStartDate());
                Timestamp attractionStartDate = new Timestamp(calendar.getTime());
                calendar.setTime(attractionViewModel.getSelectedAttraction().getEndDate());
                Timestamp attractionEndDate = new Timestamp(calendar.getTime());
                calendar.setTime(tourViewModel.getSelectedTour().getStartDate());
                Timestamp tourStartDate = new Timestamp(calendar.getTime());
                calendar.setTime(tourViewModel.getSelectedTour().getEndDate());
                Timestamp tourEndDate = new Timestamp(calendar.getTime());

                if (attractionStartDate.compareTo(tourStartDate) < 0 || attractionEndDate.compareTo(tourEndDate) > 0) {
                    Toast.makeText(getContext(), "Attraction must fall within tour dates", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Remove $ from cost
            if (cost.startsWith("$"))
                attractionViewModel.getSelectedAttraction().setCost(Float.parseFloat(cost.substring(1)));
            else
                attractionViewModel.getSelectedAttraction().setCost(Float.parseFloat(cost));

            // Get Firestore instance
            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Create a new attraction in the firestore if it doesn't exist
            if (attractionViewModel.isNewAttraction()) {
                final DocumentReference attractionDocumentReference = db.collection("Attractions").document();
                attractionViewModel.getSelectedAttraction().setAttractionUID(attractionDocumentReference.getId());
                tourViewModel.getSelectedTour().addAttraction(attractionDocumentReference);
            }

            ((MainActivity)requireActivity()).disableTabs();
            loading = true;

            db.collection("Attractions")
                    .document(attractionViewModel.getSelectedAttraction().getAttractionUID())
                    .set(attractionViewModel.getSelectedAttraction())
                    .addOnCompleteListener(task -> {
                        Log.d(TAG, "Attraction written to firestore with UID: " + attractionViewModel.getSelectedAttraction().getAttractionUID());

                        // Add/Update attraction to the selected tour
                        db.collection("Tours").document(tourViewModel.getSelectedTour().getTourUID()).update("attractions", tourViewModel.getSelectedTour().getAttractions());

                        if (attractionViewModel.isNewAttraction()) {
                            Toast.makeText(getContext(), "Successfully Added Attraction", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().popBackStack();
                        }
                        else {
                            Toast.makeText(getContext(), "Successfully Updated Attraction", Toast.LENGTH_SHORT).show();
                        }

                        ((MainActivity)requireActivity()).enableTabs();
                        loading = false;
                    })
                    .addOnFailureListener(e -> {
                        ((MainActivity)requireActivity()).enableTabs();
                        loading = false;
                        Log.w(TAG, "Error writing document");
                    });
        });
    }

    /**
     * Upon clicking the delete button, the current attraction is removed from the current tour view model
     * and the user is returned to the current tour fragment.
     * A toast message is shown marking successful deletion.
     *
     * Precondition: the attraction has been formally added and has a UID
     * @param view
     */
    private void setupDeleteAttractionButton(View view) {

        deleteAttractionButton.setOnClickListener(v -> {

            String currentAttractionUID = attractionViewModel.getSelectedAttraction().getAttractionUID();
            List<DocumentReference> attractionRefs = tourViewModel.getSelectedTour().getAttractions();
            int originalSize = attractionRefs.size();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // search through the tour view model's list of attractions and delete the one corresponding to the current attraction based on UID
            for (int i = 0; i < originalSize; i++){
                if (attractionRefs.get(i).getId().equals(currentAttractionUID)){
                    tourViewModel.getSelectedTour().getAttractions().remove(i);
                    break;
                }
            }

            // remove the attraction from the database
            db.collection("Attractions").document(currentAttractionUID).delete()
                    .addOnCompleteListener(task -> {

                        Toast.makeText(getContext(), "Attraction Deleted", Toast.LENGTH_SHORT).show();

                        attractionViewModel.setSelectedAttraction(null);
                        attractionViewModel.setIsNewAttraction(null);

                        // update the tour
                        updateTourWithDeletion(db);

                        // go back
                        getParentFragmentManager().popBackStack();
                    })
                    .addOnFailureListener(task2 -> {
                        Toast.makeText(getContext(), "Error Deleting Attraction", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    /**
     * Helper method for updating the current tour in the DB when the user deletes an attraction to leave no dangling references
     * Updating the current tour in the DB to eliminate the deleted attraction's reference immediately is necessary
     * attraction addition and updating immediately write to the DB without tapping the update tour button.
     * Precondition: not a new tour
     */
    private void updateTourWithDeletion(FirebaseFirestore db){
                db.collection("Tours").document(tourViewModel.getSelectedTour().getTourUID())
                        .set(tourViewModel.getSelectedTour())
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Tour written to Firestore");
                        })
                        .addOnFailureListener(e -> Log.w(TAG, "Error writing tour document"));
    }

    public void startAutoCompleteActivity(View view) {
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS,
                        Place.Field.PHOTO_METADATAS, Place.Field.LAT_LNG, Place.Field.RATING))
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .build(requireContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    public void updateCoverImage() {
        Glide.with(getContext())
                .load(attractionViewModel.getSelectedAttraction().getCoverImageURI())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.default_image)
                .into(coverImageView);
    }

    public void showDatePickerDialog(Button button, TextView weather, Context context, String type) {

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                String date = (month + 1) + "/" + day + "/" + year;
                button.setText(date);
                button.setBackgroundColor(Color.parseColor("#10000000"));

                try {
                    if (type.equals("start"))
                        attractionViewModel.getSelectedAttraction().setStartDateFromString(button.getText().toString());
                    else
                        attractionViewModel.getSelectedAttraction().setEndDateFromString(button.getText().toString());
                } catch (ParseException e) {
                    Log.e(TAG, "Error converting startDate to a firebase Timestamp");
                }

                if (weather != null) {
                    AttractionViewModel attractionViewModel = new ViewModelProvider((MainActivity)context).get(AttractionViewModel.class);

                    // Wait for the weather api to receive the data
                    if (attractionViewModel.getSelectedAttraction().getWeather() != null) {

                        for (Map.Entry<String, String> entry : attractionViewModel.getSelectedAttraction().getWeather().entrySet()) {
                            String aDateString = entry.getKey();

                            java.text.DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");

                            Calendar calendar = Calendar.getInstance();

                            try {
                                Date aDate = formatter.parse(aDateString);
                                calendar.setTime(aDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                Log.e(TAG, "Error converting string date");
                            }

                            String temperature = entry.getValue();

                            int aMonth = calendar.get(Calendar.MONTH);
                            int aDay = calendar.get(Calendar.DAY_OF_MONTH);
                            int aYear = calendar.get(Calendar.YEAR);

                            if (aMonth == month && aDay == day && aYear == year) {
                                weather.setText(String.format("%s℉", temperature));
                                break;
                            }
                            else
                                weather.setText("N/A");

                        }
                    }
                }
            }
        };

        TourViewModel tourViewModel = new ViewModelProvider((MainActivity)context).get(TourViewModel.class);

        final Calendar calendar = Calendar.getInstance();;

        if (tourViewModel.getSelectedTour().getStartDate() != null) {
            if (type.equals("end") && attractionViewModel.getSelectedAttraction().getStartDate() != null)
                calendar.setTime(attractionViewModel.getSelectedAttraction().getStartDate());
            else
                calendar.setTime(tourViewModel.getSelectedTour().getStartDate());
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), dateSetListener, year, month, day);

        datePickerDialog.show();
    }

    private void showReviewDialog(){

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_attraction_review, null);
        //Get elements
        RatingBar ratingBar = view.findViewById(R.id.attraction_review_rb);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });

        builder.setPositiveButton("SUBMIT", (dialogInterface, i) -> {

            addNewRating(ratingBar.getRating());

        });
        final AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void updateAttractionInFirebase(){
        // Get Firestore instance
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Attractions")
                .document(attractionViewModel.getSelectedAttraction().getAttractionUID())
                .set(attractionViewModel.getSelectedAttraction())
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "Attraction written to firestore with UID: " + attractionViewModel.getSelectedAttraction().getAttractionUID());

                    // Add/Update attraction to the selected tour
                    db.collection("Tours").document(tourViewModel.getSelectedTour().getTourUID()).update("attractions", tourViewModel.getSelectedTour().getAttractions());

                })
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document"));
    }

    private double computeRating(double totalRating) {

        return totalRating / attractionViewModel.getSelectedAttraction().getReviews().size();
    }

    private void addNewRating(double newRating) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        attractionViewModel.getSelectedAttraction().addUser(mAuth.getCurrentUser().getUid());

        if (attractionViewModel.getSelectedAttraction().getReviews().equals(null)) {
            attractionViewModel.getSelectedAttraction().setReviews(new ArrayList<>());
            attractionViewModel.getSelectedAttraction().setRating(0);
            attractionViewModel.getSelectedAttraction().setTotalRating(0);
        }

        //add new rating to totalRating
        attractionViewModel.getSelectedAttraction().setTotalRating(
                attractionViewModel.getSelectedAttraction().getTotalRating() + newRating);

        //compute rating
        attractionViewModel.getSelectedAttraction().setRating(computeRating(
                attractionViewModel.getSelectedAttraction().getTotalRating()));

        Toast.makeText(getContext(), "You successfully rated the attraction", Toast.LENGTH_SHORT).show();
        updateAttractionInFirebase();
    }

    private void showTicketDialog() {


        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.item_attraction_ticket);

        dialog.show();
        dialogIsShowing = true;

        backButton = dialog.findViewById(R.id.item_attraction_ticket_back_btn);
        confirmButton = dialog.findViewById(R.id.item_attraction_okay_btn);
        ticketImageView = dialog.findViewById(R.id.item_attraction_ticket_iv);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogIsShowing = false;
                dialog.dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogIsShowing = false;
                dialog.dismiss();
            }
        });

        ticketImageView.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            int PICK_IMAGE = 1;
            startActivityForResult(Intent.createChooser(intent, "Select Ticket"), PICK_IMAGE);
        });

        Glide.with(getActivity())
                .load(attractionViewModel.getSelectedAttraction().getTicketURI())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_tourist)
                .into(ticketImageView);

        updateAttractionInFirebase();

    }

    public void uploadTicketToDatabase(Intent imageReturnedIntent) {

        final FirebaseStorage storage = FirebaseStorage.getInstance();

        // Uri to the image
        Uri selectedImage = imageReturnedIntent.getData();

        final UUID imageUUID = UUID.randomUUID();

        final StorageReference storageReference = storage.getReference().child("AttractionTickets/" + imageUUID);

        final UploadTask uploadTask = storageReference.putFile(selectedImage);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(exception -> Log.e(TAG, "Error adding image: " + imageUUID + " to cloud storage"))
                .addOnSuccessListener(taskSnapshot -> {
                    Log.i(TAG, "Successfully added image: " + imageUUID + " to cloud storage");

                    storage.getReference().child("AttractionTickets/" + imageUUID).getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                attractionViewModel.getSelectedAttraction().setTicketURI(uri.toString());
                            })
                            .addOnFailureListener(exception -> {
                                Log.e(TAG, "Error retrieving uri for image: " + imageUUID + " in cloud storage, " + exception.getMessage());
                            });
                });
    }
}
