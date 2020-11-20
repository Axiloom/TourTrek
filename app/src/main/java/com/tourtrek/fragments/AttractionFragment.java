package com.tourtrek.fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tourtrek.R;
import com.tourtrek.activities.MainActivity;
import com.tourtrek.adapters.CurrentPersonalToursAdapter;
import com.tourtrek.data.Attraction;
import com.tourtrek.notifications.AlarmBroadcastReceiver;
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
import java.util.UUID;

/**
 * This fragment corresponds to the user story for creating a custom attraction.
 * It runs when a user selects the 'add attraction' option from within the fragment showing the list of attractions in a selected tour.
 * The fragment will consist of a form with text fields corresponding to Attraction variables to fill in and a button to collect
 * the contents of them and push the information to Firestore.
 */
public class AttractionFragment extends Fragment {

    private static final String TAG = "AttractionFragment";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 4588;
    private EditText locationEditText;
    private EditText costEditText;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private TextView coverTextView;
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

    /**
     * Default for proper back button usage
     */
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Grab a reference to the current view
        View attractionView = inflater.inflate(R.layout.fragment_attraction, container, false);

        // Initialize tourViewModel to get the current tour
        tourViewModel = new ViewModelProvider(requireActivity()).get(TourViewModel.class);

        // Initialize attractionMarketViewModel to get the current attraction
        attractionViewModel = new ViewModelProvider(requireActivity()).get(AttractionViewModel.class);

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
        buttonsContainer = attractionView.findViewById(R.id.attraction_buttons_container);
        searchAttractionButton = attractionView.findViewById(R.id.attraction_search_ib);

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
        buttonsContainer.setVisibility(View.GONE);

        // no attraction selected -> new one
        if (attractionViewModel.getSelectedAttraction() == null) {

            attractionViewModel.setSelectedAttraction(new Attraction());

            // set up fields to be made visible since we are creating a new tour
            nameEditText.setEnabled(true);
            locationEditText.setEnabled(true);
            costEditText.setEnabled(true);
            startDateButton.setEnabled(true);
            startTimeButton.setEnabled(true);
            endDateButton.setEnabled(true);
            endTimeButton.setEnabled(true);
            endTimeButton.setVisibility(View.VISIBLE);
            coverImageView.setVisibility(View.VISIBLE);
            coverTextView.setVisibility(View.VISIBLE);
            descriptionEditText.setVisibility(View.VISIBLE);
            buttonsContainer.setVisibility(View.VISIBLE);

            updateAttractionButton.setText("Add Attraction");

            attractionViewModel.setIsNewAttraction(true);

            attractionIsUsers();
        }
        else { // attraction selected -> existing one

            attractionIsUsers();

            // Set all the fields
            nameEditText.setText(attractionViewModel.getSelectedAttraction().getName());
            locationEditText.setText(attractionViewModel.getSelectedAttraction().getLocation());
            costEditText.setText("$" + attractionViewModel.getSelectedAttraction().getCost());
            startDateButton.setText(attractionViewModel.getSelectedAttraction().retrieveStartDateAsString());
            startTimeButton.setText(attractionViewModel.getSelectedAttraction().getStartTime());
            endDateButton.setText(attractionViewModel.getSelectedAttraction().retrieveEndDateAsString());
            endTimeButton.setText(attractionViewModel.getSelectedAttraction().getEndTime());
            descriptionEditText.setText(attractionViewModel.getSelectedAttraction().getDescription());
            updateAttractionButton.setText("Update Attraction");

            Glide.with(getContext())
                    .load(attractionViewModel.getSelectedAttraction().getCoverImageURI())
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
                    nameEditText.setBackgroundColor(Color.parseColor("#E4A561"));
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
                    locationEditText.setBackgroundColor(Color.parseColor("#E4A561"));
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
                    costEditText.setBackgroundColor(Color.parseColor("#E4A561"));
                }
            }
        });

        startDateButton.setOnClickListener(view -> ((MainActivity) requireActivity()).showDatePickerDialog(startDateButton));

        startDateButton.setOnFocusChangeListener((view, hasFocus) -> {

            if (startDateButton.getHint().equals("Pick Date")) {
                startDateButton.setHint("");
            }

            startDateButton.setBackgroundColor(Color.parseColor("#10000000"));

            if (!hasFocus && startDateButton.getHint().equals("")) {
                if (startDateButton.getText().toString().equals("")) {
                    startDateButton.setHint("Pick Date");
                    startDateButton.setBackgroundColor(Color.parseColor("#E4A561"));
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
                    startTimeButton.setBackgroundColor(Color.parseColor("#E4A561"));
                }
            }
        });

        endDateButton.setOnClickListener(view -> ((MainActivity) requireActivity()).showDatePickerDialog(endDateButton));

        endDateButton.setOnFocusChangeListener((view, hasFocus) -> {

            if (endDateButton.getHint().equals("Pick Date")) {
                endDateButton.setHint("");
            }

            endDateButton.setBackgroundColor(Color.parseColor("#10000000"));

            if (!hasFocus && endDateButton.getHint().equals("")) {
                if (endDateButton.getText().toString().equals("")) {
                    endDateButton.setHint("Pick Date");
                    endDateButton.setBackgroundColor(Color.parseColor("#E4A561"));
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
                    endTimeButton.setBackgroundColor(Color.parseColor("#E4A561"));
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
                    descriptionEditText.setBackgroundColor(Color.parseColor("#E4A561"));
                }
            }
        });

        // set up the action to carry out via the update button
        setupUpdateAttractionButton(attractionView);

        // set up the action to carry out via the delete button
        setupDeleteAttractionButton(attractionView);

        return attractionView;
    }

    @Override
    public void onResume() {
        super.onResume();

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
        attractionViewModel.setIsNewAttraction(null);
        attractionViewModel.setSelectedAttraction(null);

        super.onDestroyView();
    }

    /**
     * Check if the attraction belongs to the current user and make fields visible if so
     */
    public void attractionIsUsers() {

        // enables updating an attraction when it is part of a tour owned by the user and when it is a new attraction
        if (tourViewModel.isUserOwned() || attractionViewModel.isNewAttraction()){
            nameEditText.setEnabled(true);
            locationEditText.setEnabled(true);
            costEditText.setEnabled(true);
            startDateButton.setEnabled(true);
            startTimeButton.setEnabled(true);
            endDateButton.setEnabled(true);
            endTimeButton.setEnabled(true);
            coverImageView.setClickable(true);
            coverTextView.setVisibility(View.VISIBLE);
            buttonsContainer.setVisibility(View.VISIBLE);

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
                Place place = Autocomplete.getPlaceFromIntent(data);

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
                                                System.out.println("FAILING");
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("FAILING");
                            }
                        });

                attractionViewModel.setReturnedFromSearch(true);

                attractionViewModel.getSelectedAttraction().setName(place.getName());
                attractionViewModel.getSelectedAttraction().setLocation(place.getAddress());

            }
            else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                // Do Nothing because the user is exiting intent
            }
        }
        else {
            if(resultCode == Activity.RESULT_OK) {
                assert data != null;

                Glide.with(this)
                        .load(data.getData())
                        .placeholder(R.drawable.default_image)
                        .into(coverImageView);
                uploadImageToDatabase(data);
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

            db.collection("Attractions")
                    .document(attractionViewModel.getSelectedAttraction().getAttractionUID())
                    .set(attractionViewModel.getSelectedAttraction())
                    .addOnCompleteListener(task -> {
                        Log.d(TAG, "Attraction written to firestore");

                        // update the attraction to the tour object in the firestore
                        db.collection("Tours").document(tourViewModel.getSelectedTour().getTourUID()).update("attractions", tourViewModel.getSelectedTour().getAttractions());

                        // TODO: Setup alarm for start time
                        if (tourViewModel.getSelectedTour().getNotifications())
                            scheduleNotification();

                        if (attractionViewModel.isNewAttraction()) {
                            Toast.makeText(getContext(), "Successfully Added Attraction", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().popBackStack();
                        }
                        else {
                            Toast.makeText(getContext(), "Successfully Updated Attraction", Toast.LENGTH_SHORT).show();
                        }

                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document"));
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
    private void scheduleNotification() {

        // Create view button
        Intent viewIntent = new Intent(getContext(), MainActivity.class);
        viewIntent.putExtra("viewId", 1);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(getContext(), 0, viewIntent, 0);

        // Build the notification to display
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "2");
        builder.setContentTitle("Attraction Started");
        builder.setContentText(attractionViewModel.getSelectedAttraction().getName() + " has started");
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setChannelId("2");
        builder.setContentIntent(viewPendingIntent);
        builder.setAutoCancel(true);
        builder.addAction(R.drawable.ic_profile, "View", viewPendingIntent);
        Notification notification = builder.build();

        // Get Tour Start Date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(attractionViewModel.getSelectedAttraction().getStartDate());

        try {
            String startTime = attractionViewModel.getSelectedAttraction().getStartTime();
            SimpleDateFormat df = new SimpleDateFormat("hh:mm aa");
            Date date = df.parse(startTime);
            calendar.set(Calendar.HOUR, date.getHours());
            calendar.set(Calendar.MINUTE, date.getMinutes());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Initialize the alarm manager
        AlarmManager alarmMgr = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmBroadcastReceiver.class);
        String notification_id = String.valueOf(System.currentTimeMillis() % 10000);
        intent.putExtra(AlarmBroadcastReceiver.NOTIFICATION_ID, notification_id);
        intent.putExtra(AlarmBroadcastReceiver.NOTIFICATION, notification);
        intent.putExtra("NOTIFICATION_CHANNEL_ID", "2");
        intent.putExtra("NOTIFICATION_CHANNEL_NAME", "Attraction Start");
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), Integer.parseInt(notification_id), intent, PendingIntent.FLAG_ONE_SHOT);
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

    }

    public void startAutoCompleteActivity(View view) {
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS,
                        Place.Field.PHOTO_METADATAS))
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

}
