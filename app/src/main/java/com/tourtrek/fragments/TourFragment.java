package com.tourtrek.fragments;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.MainThread;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tourtrek.R;
import com.tourtrek.activities.MainActivity;
import com.tourtrek.adapters.CurrentPersonalToursAdapter;
import com.tourtrek.adapters.CurrentTourAttractionsAdapter;
import com.tourtrek.adapters.TourMarketAdapter;
import com.tourtrek.data.Attraction;
import com.tourtrek.data.Tour;
import com.tourtrek.notifications.AlarmBroadcastReceiver;
import com.tourtrek.utilities.Firestore;
import com.tourtrek.utilities.ItemClickSupport;
import com.tourtrek.viewModels.AttractionViewModel;
import com.tourtrek.utilities.AttractionCostSorter;
import com.tourtrek.utilities.AttractionLocationSorter;
import com.tourtrek.utilities.AttractionNameSorter;
import com.tourtrek.viewModels.TourViewModel;

import java.text.ParseException;
import com.tourtrek.utilities.ItemClickSupport;
import com.tourtrek.viewModels.AttractionViewModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import static com.tourtrek.utilities.Firestore.updateUser;
// TODO - when a user imports a tour, they should get a copy of it as their own tour to manipulate

public class TourFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "TourFragment";
    private TourViewModel tourViewModel;
    private AttractionViewModel attractionViewModel;
    private RecyclerView.Adapter attractionsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button addAttractionButton;
    private EditText locationEditText;
    private EditText costEditText;
    private Button startDateButton;
    private Button endDateButton;
    private Button tourImportButton;
    private EditText nameEditText;
    private Button updateTourButton;
    private Button deleteTourButton;
    private TextView coverTextView;
    private CheckBox notificationsCheckBox;
    private CheckBox publicCheckBox;
    private RelativeLayout checkBoxesContainer;
    private LinearLayout buttonsContainer;
    Button shareButton;
    private ImageView coverImageView;
    private Button attractionSortButton;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private String[] items = {"Name Ascending", "Location Ascending", "Cost Ascending",
            "Name Descending", "Location Descending", "Cost Descending"};
    private String result = "";
    private boolean added;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        // To check that the tour has not been added
        added = false;

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().popBackStack();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Grab a reference to the current view
        View tourView = inflater.inflate(R.layout.fragment_tour, container, false);

        // Initialize attractionViewModel to set the attraction chosen from the recycler
        attractionViewModel = new ViewModelProvider(requireActivity()).get(AttractionViewModel.class);

        // Initialize tourViewModel to get the current tour
        tourViewModel = new ViewModelProvider(requireActivity()).get(TourViewModel.class);

        //initialize attractionSortButton
        attractionSortButton = tourView.findViewById(R.id.tour_attraction_sort_btn);

        //Setup dialog;
        builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Select Sorting option");

        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result = items[which];
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sortAttractions((CurrentTourAttractionsAdapter) attractionsAdapter, result);
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog = builder.create();
        attractionSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        // Initialize all fields
        nameEditText = tourView.findViewById(R.id.tour_name_et);
        addAttractionButton = tourView.findViewById(R.id.tour_add_attraction_btn);
        locationEditText = tourView.findViewById(R.id.tour_location_et);
        costEditText = tourView.findViewById(R.id.tour_cost_et);
        startDateButton = tourView.findViewById(R.id.tour_start_date_btn);
        endDateButton = tourView.findViewById(R.id.tour_end_date_btn);
        updateTourButton = tourView.findViewById(R.id.tour_update_btn);
        deleteTourButton = tourView.findViewById(R.id.tour_delete_btn);
        shareButton = tourView.findViewById(R.id.tour_share_btn);
        tourImportButton = tourView.findViewById(R.id.tour_import_btn);
        coverImageView = tourView.findViewById(R.id.tour_cover_iv);
        coverTextView = tourView.findViewById(R.id.tour_cover_tv);
        checkBoxesContainer = tourView.findViewById(R.id.tour_checkboxes_container);
        publicCheckBox =  tourView.findViewById(R.id.tour_public_cb);
        notificationsCheckBox = tourView.findViewById(R.id.tour_notifications_cb);
        buttonsContainer = tourView.findViewById(R.id.tour_buttons_container);

        // When the button is clicked, switch to the AddAttractionFragment
        addAttractionButton.setOnClickListener(v -> {

            // Set to true, so we don't reset our view model
            tourViewModel.setReturnedFromAddAttraction(true);

            final FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, new AttractionFragment(), "AttractionFragment");
            ft.addToBackStack("AttractionFragment").commit();
        });

        // set up fields to be made invisible at first
        nameEditText.setEnabled(false);
        locationEditText.setEnabled(false);
        costEditText.setEnabled(false);
        startDateButton.setEnabled(false);
        endDateButton.setEnabled(false);
        coverImageView.setClickable(false);
        addAttractionButton.setVisibility(View.GONE);
        buttonsContainer.setVisibility(View.GONE);
        coverTextView.setVisibility(View.GONE);
        checkBoxesContainer.setVisibility(View.GONE);

        // tour flagged as not belonging to the user by default
        tourViewModel.setIsUserOwned(false);
        //configure Image View onClick event
        coverImageView.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            int PICK_IMAGE = 1;
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        });

        // set up the recycler view of attractions
        configureRecyclerView(tourView);
        configureSwipeRefreshLayouts(tourView);
        setupUpdateTourButton(tourView);

        // This means we are creating a new tour
        if (tourViewModel.isNewTour()) {
            updateTourButton.setText("Add Tour");
            tourViewModel.setIsUserOwned(true);
        }
        else {
            nameEditText.setText(tourViewModel.getSelectedTour().getName());
            locationEditText.setText(tourViewModel.getSelectedTour().getLocation());
            costEditText.setText("$" + tourViewModel.getSelectedTour().getCost());
            startDateButton.setText(tourViewModel.getSelectedTour().retrieveStartDateAsString());
            endDateButton.setText(tourViewModel.getSelectedTour().retrieveEndDateAsString());
            notificationsCheckBox.setChecked(tourViewModel.getSelectedTour().getNotifications());
            publicCheckBox.setChecked(tourViewModel.getSelectedTour().isPubliclyAvailable());
        }

        // Check to see if this tour belongs to the user
        if (MainActivity.user != null)
            tourIsUsers();

        Glide.with(getContext())
                .load(tourViewModel.getSelectedTour().getCoverImageURI())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.default_image)
                .into(coverImageView);

        nameEditText.setOnFocusChangeListener((view, hasFocus) -> {

            if (nameEditText.getHint().equals("Tour Name")) {
                nameEditText.setHint("");
            }

            nameEditText.setBackgroundColor(Color.parseColor("#10000000"));

            if (!hasFocus && nameEditText.getHint().equals("")) {
                if (nameEditText.getText().toString().equals("")) {
                    nameEditText.setHint("Tour Name");
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

        startDateButton.setOnClickListener(view -> {
            ((MainActivity) requireActivity()).showDatePickerDialog(startDateButton);
        });

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

        endDateButton.setOnClickListener(view -> {
            ((MainActivity) requireActivity()).showDatePickerDialog(endDateButton);
        });

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

        setupDeleteTourButton(tourView);

        return tourView;
    }

    @Override
    public void onDestroyView() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (tourViewModel.isNewTour() && !added && !tourViewModel.returnedFromAddAttraction()) {
            // Go through each attraction in the tour and delete them from the firestore

            // Pull out the UID's of each attraction that belongs to this tour
            List<String> tourAttractionUIDs = new ArrayList<>();
            if (!tourViewModel.getSelectedTour().getAttractions().isEmpty()) {
                for (DocumentReference documentReference : tourViewModel.getSelectedTour().getAttractions()) {
                    tourAttractionUIDs.add(documentReference.getId());
                }
            }

            for (String attractionUID : tourAttractionUIDs) {
                db.collection("Attractions").document(attractionUID).delete();
            }

            // Delete the tour from the firestore since the user has not
            db.collection("Tours").document(tourViewModel.getSelectedTour().getTourUID()).delete();

            // Remove the tour from the users tour list
            for (DocumentReference tourDocumentReference : MainActivity.user.getTours()) {
                if (tourDocumentReference.getId().equals(tourViewModel.getSelectedTour().getTourUID()))
                    MainActivity.user.getTours().remove(tourDocumentReference);
            }
        }

        if (!tourViewModel.returnedFromAddAttraction()) {
            tourViewModel.setSelectedTour(null);
            tourViewModel.setIsNewTour(null);
        }

        super.onDestroyView();
    }

    /**
     * Configure the recycler view
     *
     * @param view current view
     */
    public void configureRecyclerView(View view) {

        // Get our recycler view from the layout
        RecyclerView attractionsRecyclerView = view.findViewById(R.id.tour_attractions_rv);

        // Improves performance because content does not change size
        attractionsRecyclerView.setHasFixedSize(true);

        // Only load 10 tours before loading more
        attractionsRecyclerView.setItemViewCacheSize(10);

        // Enable drawing cache
        attractionsRecyclerView.setDrawingCacheEnabled(true);
        attractionsRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // User linear layout manager
        RecyclerView.LayoutManager attractionsLayoutManager = new LinearLayoutManager(getContext());
        attractionsRecyclerView.setLayoutManager(attractionsLayoutManager);

        // Specify an adapter
        attractionsAdapter = new CurrentTourAttractionsAdapter(getContext());

        // Pull the tours attractions if it already exists in firebase
        if (tourViewModel.getSelectedTour() != null) {
            fetchAttractionsAsync();
        }

        // set the adapter
        attractionsRecyclerView.setAdapter(attractionsAdapter);

        // Stop showing progressBar when items are loaded
        attractionsRecyclerView
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        () -> ((CurrentTourAttractionsAdapter)attractionsAdapter).stopLoading());

        // enable clicking a recycler view item to update an attraction
        ItemClickSupport.addTo(attractionsRecyclerView, R.layout.item_attraction)
                .setOnItemClickListener((recyclerView, position, v) -> {

                    tourViewModel.setReturnedFromAddAttraction(true);

                    // Reference to the current tour selected
                    Attraction attraction = ((CurrentTourAttractionsAdapter) attractionsAdapter).getData(position);

                    // Add the selected tour to the view model so we can access the tour inside the fragment
                    attractionViewModel.setSelectedAttraction(attraction);

                    // Display the attraction selected
                    final FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.replace(R.id.nav_host_fragment, new AttractionFragment(), "AttractionFragment");
                    ft.addToBackStack("AttractionFragment").commit();
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (tourViewModel.isNewTour() || tourViewModel.getSelectedTour() == null)
            ((MainActivity) requireActivity()).setActionBarTitle("New Tour");
        else
            ((MainActivity) requireActivity()).setActionBarTitle(tourViewModel.getSelectedTour().getName());

    }

    /**
     * Retrieve all attractions belonging to this user
     *
     */
    private void fetchAttractionsAsync() {

        // Get instance of firestore
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Setup collection reference
        CollectionReference attractionsCollection = db.collection("Attractions");

        // Pull out the UID's of each tour that belongs to this user
        List<String> usersAttractionUIDs = new ArrayList<>();
        if (!tourViewModel.getSelectedTour().getAttractions().isEmpty()) {
            for (DocumentReference documentReference : tourViewModel.getSelectedTour().getAttractions()) {
                usersAttractionUIDs.add(documentReference.getId());
            }
        }

        // Query database
        attractionsCollection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.w(TAG, "No documents found in the Attractions collection for this user");
                    }
                    else {

                        // Final list of tours for this category
                        List<Attraction> usersAttractions = new ArrayList<>();

                        // Go through each document and compare the dates
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {

                            // First check that the document belongs to the user
                            if (usersAttractionUIDs.contains(document.getId())) {
                                usersAttractions.add(document.toObject(Attraction.class));
                            }
                        }

                        ((CurrentTourAttractionsAdapter) attractionsAdapter).clear();
                        ((CurrentTourAttractionsAdapter) attractionsAdapter).addAll(usersAttractions);
                        ((CurrentTourAttractionsAdapter) attractionsAdapter).copyAttractions(usersAttractions);
                        swipeRefreshLayout.setRefreshing(false);

                    }
                });
    }

    /**
     * Configure the swipe down to refresh function of our recycler view
     *
     * @param view current view
     */
    public void configureSwipeRefreshLayouts(View view) {

        swipeRefreshLayout = view.findViewById(R.id.tour_attractions_srl);
        swipeRefreshLayout.setOnRefreshListener(() -> fetchAttractionsAsync());

    }

    /**
     * Check if the tour belongs to the current user and make fields visible if so
     */
    public void tourIsUsers() {

        // Check to see if this is an abandoned new tour
        if (tourViewModel.isNewTour()) {
            nameEditText.setEnabled(true);
            locationEditText.setEnabled(true);
            costEditText.setEnabled(true);
            startDateButton.setEnabled(true);
            endDateButton.setEnabled(true);
            coverImageView.setClickable(true);
            updateTourButton.setVisibility(View.VISIBLE);
            tourImportButton.setVisibility(View.GONE);
            addAttractionButton.setVisibility(View.VISIBLE);
            coverTextView.setVisibility(View.VISIBLE);
            buttonsContainer.setVisibility(View.VISIBLE);
            checkBoxesContainer.setVisibility(View.VISIBLE);
            updateTourButton.setText("Add Tour");

            tourViewModel.setIsUserOwned(true);

            return;
        }

        // Get instance of firestore
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Pull out the UID's of each tour that belongs to this user
        List<String> usersToursUIDs = new ArrayList<>();
        if (!MainActivity.user.getTours().isEmpty()) {
            for (DocumentReference documentReference : MainActivity.user.getTours()) {
                usersToursUIDs.add(documentReference.getId());
            }
        }

        if (usersToursUIDs.contains(tourViewModel.getSelectedTour().getTourUID())) {

            nameEditText.setEnabled(true);
            locationEditText.setEnabled(true);
            costEditText.setEnabled(true);
            startDateButton.setEnabled(true);
            endDateButton.setEnabled(true);
            coverImageView.setClickable(true);
            updateTourButton.setVisibility(View.VISIBLE);
            tourImportButton.setVisibility(View.GONE);
            addAttractionButton.setVisibility(View.VISIBLE);
            coverTextView.setVisibility(View.VISIBLE);
            buttonsContainer.setVisibility(View.VISIBLE);
            checkBoxesContainer.setVisibility(View.VISIBLE);

            tourViewModel.setIsUserOwned(true);

            coverImageView.setOnClickListener(view -> {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                int PICK_IMAGE = 1;
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
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

        final StorageReference storageReference = storage.getReference().child("TourCoverPictures/" + imageUUID);

        final UploadTask uploadTask = storageReference.putFile(selectedImage);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(exception -> Log.e(TAG, "Error adding image: " + imageUUID + " to cloud storage"))
                .addOnSuccessListener(taskSnapshot -> {
                    Log.i(TAG, "Successfully added image: " + imageUUID + " to cloud storage");

                    storage.getReference().child("TourCoverPictures/" + imageUUID).getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                tourViewModel.getSelectedTour().setCoverImageURI(uri.toString());
                            })
                            .addOnFailureListener(exception -> {
                                Log.e(TAG, "Error retrieving uri for image: " + imageUUID + " in cloud storage, " + exception.getMessage());
                            });
                });
    }

    /**
     * Remove the tour from the user's list of tours in the database and return to the prior screen
     * // TODO deal with the problem of deleting a tour which is referenced by another user
     *
     * @param view
     */
    public void setupDeleteTourButton(View view){
        // only visible to a user with the tour in their list of tours
        if (tourViewModel.getSelectedTour().getTourUID() != null && tourViewModel.isUserOwned()){
            deleteTourButton.setVisibility(View.VISIBLE);
        }

        // delete listener
        deleteTourButton.setOnClickListener(v -> {

            String currentTourUID = tourViewModel.getSelectedTour().getTourUID();
            List<DocumentReference> tourRefs = MainActivity.user.getTours();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // only remove a tour which is private
            if (!tourViewModel.getSelectedTour().isPubliclyAvailable()){
                for (int i = 0; i < tourRefs.size(); i++){

                    if (tourRefs.get(i).getId().equals(currentTourUID)){
                        // remove from the user
                        MainActivity.user.getTours().remove(i);

                        // remove attractions in the tour in the DB
                        db.collection("Tours").document(currentTourUID)
                                .get()
                                .addOnCompleteListener(task -> {
                                    // get Tour object
                                    Tour currentTour = task.getResult().toObject(Tour.class);
                                    // iterate through each attraction document and delete it
                                    for (int j = 0; j < currentTour.getAttractions().size(); j++){
                                        db.collection("Attractions").document(
                                                currentTour.getAttractions()
                                                        .get(j)
                                                        .getId())
                                                .delete()
                                                .addOnSuccessListener(v1 -> Log.d(TAG, "Attraction deleted"))
                                                .addOnFailureListener(v2 -> Log.d(TAG, "Attraction could not be deleted"));
                                    }

                                    // remove the tour from the DB
                                    task.getResult().getReference()
                                            .delete()
                                            .addOnCompleteListener(w -> {

                                                // remove the tour from the user's DB entry
                                                updateUser();

                                                // toast message
                                                Toast.makeText(getContext(), "Tour removed", Toast.LENGTH_SHORT).show();

                                                // go back
                                                getParentFragmentManager().popBackStack();
                                            });
                                });
                        break;
                    }
                }
            }
            // the tour is not private - error
            else{
                Toast.makeText(getContext(), "You cannot delete a public tour!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setupUpdateTourButton(View view) {

        Button editTourUpdateButton = view.findViewById(R.id.tour_update_btn);

        editTourUpdateButton.setOnClickListener(view1 -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

            added = true;

            String name = nameEditText.getText().toString();
            String location = locationEditText.getText().toString();
            String cost = costEditText.getText().toString();
            String startDate = startDateButton.getText().toString();
            String endDate = endDateButton.getText().toString();

            // error-handling of dates so as to not break the tour classification by date
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
                    endDate.equals("")) {
                Toast.makeText(getContext(), "Not all fields entered", Toast.LENGTH_SHORT).show();
                return;
            }

            // parse date to firebase format
            Date date;
            try {
                tourViewModel.getSelectedTour().setStartDateFromString(startDateButton.getText().toString());
            } catch (ParseException e) {
                Log.e(TAG, "Error converting startDate to a firebase Timestamp");
                return;
            }

            try {
                tourViewModel.getSelectedTour().setEndDateFromString(endDateButton.getText().toString());
            } catch (ParseException e) {
                Log.e(TAG, "Error converting startDate to a firebase Timestamp");
                return;
            }

            tourViewModel.getSelectedTour().setName(nameEditText.getText().toString());
            tourViewModel.getSelectedTour().setLocation(locationEditText.getText().toString());
            tourViewModel.getSelectedTour().setNotifications(notificationsCheckBox.isChecked());
            tourViewModel.getSelectedTour().setPubliclyAvailable(publicCheckBox.isChecked());

            // Remove $ from cost
            if (costEditText.getText().toString().startsWith("$"))
                tourViewModel.getSelectedTour().setCost(Float.parseFloat(costEditText.getText().toString().substring(1)));
            else
                tourViewModel.getSelectedTour().setCost(Float.parseFloat(costEditText.getText().toString()));

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Tours").document(tourViewModel.getSelectedTour().getTourUID())
                    .set(tourViewModel.getSelectedTour())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Tour written to firestore");

                        // Update the user in the firestore
                        Firestore.updateUser();

                        // TODO: only schedule the notification if it hasn't started yet
                        if (tourViewModel.getSelectedTour().getNotifications())
                            scheduleNotification();

                        tourViewModel.setSelectedTour(null);
                        tourViewModel.setIsNewTour(null);
                        getParentFragmentManager().popBackStack();

                        if (tourViewModel.isNewTour()) {
                            Toast.makeText(getContext(), "Successfully Added Tour", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(), "Successfully Updated Tour", Toast.LENGTH_SHORT).show();

                            tourViewModel.setIsNewTour(false);
                        }

                    })
            .addOnFailureListener(e -> Log.w(TAG, "Error writing document"));
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Show the top app bar with the search icon
        inflater.inflate(R.menu.tour_search_menu, menu);

        // Get the menu item
        MenuItem item = menu.findItem(R.id.tour_search_itm);

        SearchView searchView = (SearchView) item.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchAttractions((CurrentTourAttractionsAdapter) attractionsAdapter, query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchAttractions((CurrentTourAttractionsAdapter) attractionsAdapter, newText);

                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }


    public void searchAttractions(CurrentTourAttractionsAdapter adapter, String newText){
        ArrayList<Attraction> data = new ArrayList<>(adapter.getDataSet());

        List<Attraction> filteredTourList = findAttractions(data, newText);

        adapter.clear();
        adapter.setDataSetFiltered(filteredTourList);
        adapter.addAll(filteredTourList);
    }

    public List<Attraction> findAttractions(List<Attraction> data, String newText) {

        ArrayList<Attraction> originalList = new ArrayList<>(data);
        List<Attraction> filteredTourList = new ArrayList<>();

        if (newText == null || newText.length() == 0) {

            filteredTourList.addAll(originalList);

        } else {

            String key = newText.toLowerCase();

            for(Attraction attraction: originalList){
                if(attraction.getName().toLowerCase().contains(key)){
                    filteredTourList.add(attraction);
                }
            }
        }

        return filteredTourList;
    }

    /**
     * Update the selected tour
     *
     * This method assumes a tour is already created and has a properly filled UID field
     * https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
     */
    private void syncTour() {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String key = (String) parent.getItemAtPosition(position);
        sortAttractions((CurrentTourAttractionsAdapter) attractionsAdapter, key);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void sortAttractions(CurrentTourAttractionsAdapter adapter, String key){

        ArrayList<Attraction> data = new ArrayList<>(adapter.getDataSetFiltered());

        List<Attraction> temp = sortedAttractions(data, key);

        adapter.clear();
        adapter.addAll(temp);
    }

    public List<Attraction> sortedAttractions(List<Attraction> data, String key) {
        List<Attraction> temp = new ArrayList<>(data);

        switch (key){

            case "Name Ascending":
                Collections.sort(temp, new AttractionNameSorter());
                break;

            case "Location Ascending":
                Collections.sort(temp, new AttractionLocationSorter());
                break;

            case "Cost Ascending":
                Collections.sort(temp, new AttractionCostSorter());
                break;

            case "Name Descending":
                Collections.sort(temp, new AttractionNameSorter());
                Collections.reverse(temp);
                break;

            case "Location Descending":
                Collections.sort(temp, new AttractionLocationSorter());
                Collections.reverse(temp);
                break;

            case "Cost Descending":
                Collections.sort(temp, new AttractionCostSorter());
                Collections.reverse(temp);
                break;

            default:
                return temp;
        }
        return temp;
    }


    /**
     * Create a notification channel and add an alarm to be triggered by a broadcast receiver
     */
    private void scheduleNotification() {

        // Create view button
        Intent viewIntent = new Intent(getContext(), MainActivity.class);
        viewIntent.putExtra("viewId", 1);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(getContext(), 0, viewIntent, 0);

        // Build the notification to display
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "1");
        builder.setContentTitle("Tour Started");
        builder.setContentText(tourViewModel.getSelectedTour().getName() + " has started");
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setChannelId("1");
        builder.setContentIntent(viewPendingIntent);
        builder.setAutoCancel(true);
        builder.addAction(R.drawable.ic_profile, "View", viewPendingIntent);
        Notification notification = builder.build();

        // Get Tour Start Date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tourViewModel.getSelectedTour().getStartDate());

        // Initialize the alarm manager
        AlarmManager alarmMgr = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmBroadcastReceiver.class);
        String notification_id = String.valueOf(System.currentTimeMillis() % 10000);
        intent.putExtra(AlarmBroadcastReceiver.NOTIFICATION_ID, notification_id);
        intent.putExtra(AlarmBroadcastReceiver.NOTIFICATION, notification);
        intent.putExtra("NOTIFICATION_CHANNEL_ID", "1");
        intent.putExtra("NOTIFICATION_CHANNEL_NAME", "Tour Start");
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), Integer.parseInt(notification_id), intent, PendingIntent.FLAG_ONE_SHOT);
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

    }
}

