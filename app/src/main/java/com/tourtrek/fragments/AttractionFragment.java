package com.tourtrek.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tourtrek.R;
import com.tourtrek.activities.MainActivity;
import com.tourtrek.data.Attraction;
import com.tourtrek.viewModels.AttractionViewModel;
import com.tourtrek.viewModels.TourViewModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * This fragment corresponds to the user story for creating a custom attraction.
 * It runs when a user selects the 'add attraction' option from within the fragment showing the list of attractions in a selected tour.
 * The fragment will consist of a form with text fields corresponding to Attraction variables to fill in and a button to collect
 * the contents of them and push the information to Firestore.
 */
public class AttractionFragment extends Fragment {

    private static final String TAG = "AttractionFragment";
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
        View addAttractionView = inflater.inflate(R.layout.fragment_attraction, container, false);

        // Initialize tourViewModel to get the current tour
        tourViewModel = new ViewModelProvider(getActivity()).get(TourViewModel.class);

        // Initialize attractionMarketViewModel to get the current attraction
        attractionViewModel = new ViewModelProvider(getActivity()).get(AttractionViewModel.class);

        // Initialize all fields
        nameEditText = addAttractionView.findViewById(R.id.attraction_name_et);
        locationEditText = addAttractionView.findViewById(R.id.add_attraction_location_et);
        costEditText = addAttractionView.findViewById(R.id.add_attraction_cost_et);
        startDateButton = addAttractionView.findViewById(R.id.add_attraction_start_date_btn);
        startTimeButton = addAttractionView.findViewById(R.id.add_attraction_start_time_btn);
        endDateButton = addAttractionView.findViewById(R.id.add_attraction_end_date_btn);
        endTimeButton = addAttractionView.findViewById(R.id.add_attraction_end_time_btn);
        descriptionEditText = addAttractionView.findViewById(R.id.add_attraction_description_et);
        coverImageView = addAttractionView.findViewById(R.id.add_attraction_cover_iv);
        coverTextView = addAttractionView.findViewById(R.id.attraction_cover_tv);
        updateAttractionButton = addAttractionView.findViewById(R.id.add_attraction_update_btn);
        buttonsContainer = addAttractionView.findViewById(R.id.attraction_buttons_container);

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
//            checkBoxesContainer.setVisibility(View.VISIBLE);

        if (attractionViewModel.getSelectedAttraction() == null) {

            attractionViewModel.setSelectedAttraction(new Attraction());

            // set up fields to be made visible since we are creating a new tour
            nameEditText.setEnabled(true);
            locationEditText.setEnabled(true);
            costEditText.setEnabled(true);
            startDateButton.setEnabled(true);
            startTimeButton.setEnabled(true);
            endDateButton.setClickable(true);
            endTimeButton.setVisibility(View.VISIBLE);
            coverImageView.setVisibility(View.VISIBLE);
            coverTextView.setVisibility(View.VISIBLE);
            descriptionEditText.setVisibility(View.VISIBLE);
            buttonsContainer.setVisibility(View.VISIBLE);

            updateAttractionButton.setText("Add Attraction");

            attractionViewModel.setIsNewAttraction(true);
        }
        else {

            if (MainActivity.user != null) {
                attractionIsUsers();
            }

            // Set all the fields
            nameEditText.setText(attractionViewModel.getSelectedAttraction().getName());
            locationEditText.setText(attractionViewModel.getSelectedAttraction().getLocation());
            costEditText.setText("$" + attractionViewModel.getSelectedAttraction().getCost());
            startDateButton.setText(convertDateToString(attractionViewModel.getSelectedAttraction().getStartDate().toDate()));
            startTimeButton.setText(attractionViewModel.getSelectedAttraction().getStartTime());
            endDateButton.setText(convertDateToString(attractionViewModel.getSelectedAttraction().getEndDate().toDate()));
            endTimeButton.setText(attractionViewModel.getSelectedAttraction().getEndTime());
            descriptionEditText.setText(attractionViewModel.getSelectedAttraction().getDescription());

            Glide.with(getContext())
                    .load(tourViewModel.getSelectedTour().getCoverImageURI())
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
        setupUpdateAttractionButton(addAttractionView);

        return addAttractionView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setActionBarTitle("Add Attraction");
    }

    @Override
    public void onDestroyView() {

        tourViewModel.setReturnedFromAddAttraction(false);

        super.onDestroyView();
    }


    /**
     * Convert a given date into the readable string representation
     *
     * @param date date to convert
     *
     * @return string representation of the date
     */
    public String convertDateToString(Date date) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        final String newDate = formatter.format(date);

        return newDate;
    }

    /**
     * Check if the attraction belongs to the current user and make fields visible if so
     */
    public void attractionIsUsers() {

        // Check to see if this is an abandoned new attraction
        if (tourViewModel.isNewTour()) {
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
//            checkBoxesContainer.setVisibility(View.VISIBLE);
            updateAttractionButton.setText("Add Attraction");
            return;
        }


        // Get instance of firestore
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Pull out the UID's of each attraction that belongs to the currently selected tour
        List<String> tourAttractionsUIDs = new ArrayList<>();
        if (!tourViewModel.getSelectedTour().getAttractions().isEmpty()) {
            for (DocumentReference documentReference : tourViewModel.getSelectedTour().getAttractions()) {
                tourAttractionsUIDs.add(documentReference.getId());
            }
        }

        if (tourAttractionsUIDs.contains(attractionViewModel.getSelectedAttraction().getAttractionUID())) {

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
//            checkBoxesContainer.setVisibility(View.VISIBLE);

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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if(resultCode == Activity.RESULT_OK) {
            assert imageReturnedIntent != null;

            Glide.with(this)
                    .load(imageReturnedIntent.getData())
                    .placeholder(R.drawable.default_image)
                    .into(coverImageView);
            uploadImageToDatabase(imageReturnedIntent);
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

    private void setupUpdateAttractionButton(View view){

        updateAttractionButton.setOnClickListener(v -> {

            // first get the information from each EditText
            String name = nameEditText.getText().toString();
            String location = locationEditText.getText().toString();
            String cost = costEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String startDate = startDateButton.getText().toString();
            String startTime = startTimeButton.getText().toString();
            String endDate = endDateButton.getText().toString();
            String endTime = endTimeButton.getText().toString();

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

            // parse dates to firebase format
            try {
                attractionViewModel.getSelectedAttraction().setEndDateFromString(endDate);
            } catch (ParseException e) {
                Log.e(TAG, "Error converting startDate to a firebase Timestamp");
                Toast.makeText(getContext(), "Start Date Formatted Incorrectly", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                attractionViewModel.getSelectedAttraction().setStartDateFromString(startDate);
            } catch (ParseException e) {
                Log.e(TAG, "Error converting endDate to a firebase Timestamp");
                Toast.makeText(getContext(), "End Date Formatted Incorrectly", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            attractionViewModel.getSelectedAttraction().setName(name);
            attractionViewModel.getSelectedAttraction().setLocation(location);
            attractionViewModel.getSelectedAttraction().setDescription(description);

            // Remove $ from cost
            if (cost.startsWith("$"))
                attractionViewModel.getSelectedAttraction().setCost(Float.parseFloat(cost.substring(1)));
            else
                attractionViewModel.getSelectedAttraction().setCost(Float.parseFloat(cost));

            // Get Firestore instance
            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            final DocumentReference attractionDocumentReference = db.collection("Attractions").document();

            attractionViewModel.getSelectedAttraction().setAttractionUID(attractionDocumentReference.getId());

            attractionDocumentReference.set(attractionViewModel.getSelectedAttraction())
                    .addOnCompleteListener(task -> {
                        Log.d(TAG, "Attraction written to firestore");


                        if (attractionViewModel.isNewAttraction()) {
                            Toast.makeText(getContext(), "Successfully Added Attraction", Toast.LENGTH_SHORT).show();

                            attractionViewModel.setIsNewAttraction(null);
                            getParentFragmentManager().popBackStack();
                        }
                        else
                            Toast.makeText(getContext(), "Successfully Updated Attraction", Toast.LENGTH_SHORT).show();

                        // Only update the tour with new attraction if it has been created in the firestore
                        if (tourViewModel.getSelectedTour().getTourUID() != null)
                            syncTour();

                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document"));
    });
    }

    /**
     * Update the selected tour
     *
     * This method assumes a tour is already created and has a properly filled UID field
     * https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
     */
    private void syncTour() {
        // Get instance of firestore
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Setup collection reference
        CollectionReference toursCollection = db.collection("Tours");

        toursCollection.document(tourViewModel.getSelectedTour().getTourUID())
                .set(tourViewModel.getSelectedTour())
                .addOnCompleteListener(v -> {
                    Log.d(TAG, "Tour successfully written to firestore");
                });
    }

}