package com.tourtrek.fragments;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.widget.NestedScrollView;
import androidx.core.widget.NestedScrollView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tourtrek.R;
import com.tourtrek.activities.MainActivity;
import com.tourtrek.adapters.CurrentTourAttractionsAdapter;
import com.tourtrek.data.Attraction;
import com.tourtrek.data.Tour;
import com.tourtrek.notifications.AlarmBroadcastReceiver;
import com.tourtrek.utilities.Firestore;
import com.tourtrek.utilities.ItemClickSupport;
import com.tourtrek.utilities.PlacesLocal;
import com.tourtrek.viewModels.AttractionViewModel;
import com.tourtrek.utilities.AttractionCostSorter;
import com.tourtrek.utilities.AttractionLocationSorter;
import com.tourtrek.utilities.AttractionNameSorter;
import com.tourtrek.utilities.AttractionRatingSorter;
import com.tourtrek.utilities.Firestore;
import com.tourtrek.utilities.ItemClickSupport;
import com.tourtrek.utilities.Utilities;
import com.tourtrek.viewModels.AttractionViewModel;
import com.tourtrek.viewModels.TourViewModel;

import org.w3c.dom.Document;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import static com.tourtrek.utilities.Firestore.updateUser;
import static com.tourtrek.utilities.PlacesLocal.checkLocationPermission;
import static com.tourtrek.utilities.PlacesLocal.checkLocationPermission;
import  com.facebook.FacebookSdk;

public class TourFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "TourFragment";
    private TourViewModel tourViewModel;
    private AttractionViewModel attractionViewModel;
    private CurrentTourAttractionsAdapter attractionsAdapter;
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
    private Button twitterShareButton;
    private Button myFacebookShareButton;
    private RelativeLayout checkBoxesContainer;
    private LinearLayout buttonsContainer;
    private Button shareButton;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private ImageView coverImageView;
    private Button attractionSortButton;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private String[] items = {"Name Ascending", "Location Ascending", "Cost Ascending",
            "Rating Ascending", "Name Descending", "Location Descending",
            "Cost Descending", "Rating Descending"};
    private String result = "";
    private boolean added;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private Button navigationButton;
//    private NestedScrollView scrollView;
    // To keep track of whether we are in an async call
    private boolean loading;
    private ImageButton rate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        //setup for facebook share
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        // To check that the tour has not been added
        added = false;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((MainActivity)requireActivity()).disableTabs();
        loading = true;

        // Grab a reference to the current view
        View tourView = inflater.inflate(R.layout.fragment_tour, container, false);

        // Initialize attractionViewModel to set the attraction chosen from the recycler
        attractionViewModel = new ViewModelProvider(requireActivity()).get(AttractionViewModel.class);

        // Initialize tourViewModel to get the current tour
        tourViewModel = new ViewModelProvider(requireActivity()).get(TourViewModel.class);

        // Initialize attractionSortButton
        //review button
        rate = tourView.findViewById(R.id.tour_review_btn);

        if (tourViewModel.isNewTour()) {
            rate.setVisibility(View.GONE);
        }
        rate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                if (!tourViewModel.getSelectedTour().getReviews().equals(null)) {
                    if (!tourViewModel.getSelectedTour().getReviews().contains(mAuth.getCurrentUser().getUid())) {
                        showReviewDialog();
                    } else {
                        Toast.makeText(getContext(), "You cannot rate a tour more than once", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


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
        navigationButton = tourView.findViewById(R.id.tour_navigation_btn);
        deleteTourButton = tourView.findViewById(R.id.tour_delete_btn);
        tourImportButton = tourView.findViewById(R.id.tour_import_btn);
        coverImageView = tourView.findViewById(R.id.tour_cover_iv);
        coverTextView = tourView.findViewById(R.id.tour_cover_tv);
        checkBoxesContainer = tourView.findViewById(R.id.tour_checkboxes_container);
        publicCheckBox =  tourView.findViewById(R.id.tour_public_cb);
        notificationsCheckBox = tourView.findViewById(R.id.tour_notifications_cb);
        buttonsContainer = tourView.findViewById(R.id.tour_buttons_container);
        myFacebookShareButton = tourView.findViewById(R.id.tour_my_fb_share_btn);

        ShareButton shareButton = (ShareButton)tourView.findViewById(R.id.tour_fb_share_btn);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://github.com/lovinganivia/TourTrek/tree/Feature-Share-tour"))
                    .build();
            shareButton.setShareContent(linkContent);
        }

        myFacebookShareButton.setOnClickListener(view -> {
            shareButton.performClick();
        });

        twitterShareButton =tourView.findViewById(R.id.tour_tw_share_btn);
        twitterShareButton.setOnClickListener(view -> {
            shareOnTwitter();
        });



        // No navigation with a brand new tour
        if (tourViewModel.isNewTour()){
            navigationButton.setVisibility(View.GONE);
        }


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
            costEditText.setText(String.format("$%.2f", tourViewModel.getSelectedTour().getCost()));
            startDateButton.setText(tourViewModel.getSelectedTour().retrieveStartDateAsString());
            endDateButton.setText(tourViewModel.getSelectedTour().retrieveEndDateAsString());
            notificationsCheckBox.setChecked(tourViewModel.getSelectedTour().getNotifications());
            publicCheckBox.setChecked(tourViewModel.getSelectedTour().isPubliclyAvailable());
        }

        // Check to see if this tour belongs to the user
        if (MainActivity.user != null) {
            tourIsUsers();
        }
        else{ // no user is logged in, so disable importing
            tourImportButton.setVisibility(View.GONE);
        }

        LinearLayout loadingContainer = tourView.findViewById(R.id.tour_cover_loading_container);
        loadingContainer.setVisibility(View.VISIBLE);
        ((MainActivity)requireActivity()).disableTabs();
        loading = true;

        Glide.with(getContext())
                .load(tourViewModel.getSelectedTour().getCoverImageURI())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        LinearLayout loadingContainer = tourView.findViewById(R.id.tour_cover_loading_container);
                        loadingContainer.setVisibility(View.INVISIBLE);

                        if (getActivity() != null && isAdded()) {
                            ((MainActivity) requireActivity()).enableTabs();
                            loading = false;
                        }

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        LinearLayout loadingContainer = tourView.findViewById(R.id.tour_cover_loading_container);
                        loadingContainer.setVisibility(View.INVISIBLE);
                        if (getActivity() != null && isAdded()) {
                            ((MainActivity) requireActivity()).enableTabs();
                            loading = false;
                        }
                        return false;
                    }
                })
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
            showDatePickerDialog(startDateButton, getContext(), "start");
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

        endDateButton.setOnClickListener(view -> {
            showDatePickerDialog(endDateButton, getContext(), "end");
        });

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

        setupDeleteTourButton(tourView);
        setupImportTourButton(tourView);

        ((MainActivity)requireActivity()).enableTabs();
        loading = false;

        setupNavigationButton(tourView);

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

        if (!tourViewModel.returnedFromAddAttraction() && !tourViewModel.returnedFromNavigation()) {
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

        if (tourViewModel.getSelectedTour() != null) {
            if (tourViewModel.getSelectedTour().getStartDate() != null)
                startDateButton.setText(tourViewModel.getSelectedTour().retrieveStartDateAsString());
            if (tourViewModel.getSelectedTour().getEndDate() != null)
                endDateButton.setText(tourViewModel.getSelectedTour().retrieveEndDateAsString());
        }

        if (tourViewModel.isNewTour() || tourViewModel.getSelectedTour() == null)
            ((MainActivity) requireActivity()).setActionBarTitle("New Tour");
        else
            ((MainActivity) requireActivity()).setActionBarTitle(tourViewModel.getSelectedTour().getName());

    }

    /**
     * Retrieve all attractions belonging to this user
     */
    private void fetchAttractionsAsync() {

        // Get instance of firestore
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Setup collection reference
        CollectionReference attractionsCollection = db.collection("Attractions");

        // Grab each attraction for the selected tour
        if (!tourViewModel.getSelectedTour().getAttractions().isEmpty()) {

            // Clear the data set if one exists
            if (attractionsAdapter != null)
                attractionsAdapter.clear();

            for (DocumentReference documentReference : tourViewModel.getSelectedTour().getAttractions()) {

                attractionsCollection.document(documentReference.getId()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                attractionsAdapter.addNewData(documentSnapshot.toObject(Attraction.class));
                                attractionsAdapter.copyAttractions(attractionsAdapter.getDataSet());
                                swipeRefreshLayout.setRefreshing(false);

                            }
                        });

            }
        }
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

        callbackManager.onActivityResult(requestCode, resultCode, imageReturnedIntent);
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
     *
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

            ((MainActivity)requireActivity()).disableTabs();

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

                                                ((MainActivity)requireActivity()).enableTabs();
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

            ((MainActivity)requireActivity()).disableTabs();
            loading = true;

            db.collection("Tours").document(tourViewModel.getSelectedTour().getTourUID())
                    .set(tourViewModel.getSelectedTour())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Tour written to firestore");

                        // Update the user in the firestore
                        Firestore.updateUser();

                        if (tourViewModel.getSelectedTour().getNotifications())
                            scheduleAlarms();

                        // Remove any pre-existing alarms for this tour
                        if (!tourViewModel.getSelectedTour().getNotifications())
                            removeAlarms();

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

                        ((MainActivity)requireActivity()).enableTabs();
                        loading = false;
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document");

                            ((MainActivity)requireActivity()).enableTabs();
                            loading = false;
                        }
                    });
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


                searchAttractions(attractionsAdapter, query);
                Activity currentActivity = requireActivity();
                Utilities.hideKeyboard(currentActivity);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchAttractions(attractionsAdapter, newText);

                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void searchAttractions(CurrentTourAttractionsAdapter adapter, String newText){
        ArrayList<Attraction> data = new ArrayList<>(adapter.getDataSetCopy());

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

            case "Rating Ascending":
                Collections.sort(temp, new AttractionRatingSorter());
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

            case "Rating Descending":
                Collections.sort(temp, new AttractionRatingSorter());
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
    private void scheduleAlarms() {

        // Set an alarm for each attraction within the tour
        for (Attraction attraction : attractionsAdapter.getDataSet()) {

            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(attraction.getStartDate());
                String startTime = attraction.getStartTime();
                SimpleDateFormat df = new SimpleDateFormat("hh:mm aa");
                Date date = df.parse(startTime);
                calendar.set(Calendar.HOUR_OF_DAY, date.getHours());
                calendar.set(Calendar.MINUTE, date.getMinutes());

                Timestamp attractionStartDate = new Timestamp(calendar.getTime());
                Timestamp now = Timestamp.now();

                // Only enable an alarm for the attraction if the attraction hasn't started yet
                if (attractionStartDate.compareTo(now) > 0)
                    setAlarmForAttraction(attraction);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Timestamp tourStartDate = new Timestamp(tourViewModel.getSelectedTour().getStartDate());
        Timestamp now = Timestamp.now();

        // Only enable an alarm for the tour if the tour hasn't started yet
        if (tourStartDate.compareTo(now) > 0) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("alarms", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

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

            // Add reference to new alarm so we can cancel later;
            Set<String> attractionAlarms = sharedPreferences.getStringSet(tourViewModel.getSelectedTour().getTourUID(), new HashSet<>());
            attractionAlarms.add(notification_id);
            editor.putStringSet(tourViewModel.getSelectedTour().getTourUID(), attractionAlarms).apply();
        }
    }

    void shareOnTwitter()
    {
        PackageManager pm=getContext().getPackageManager();
        try {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = "Welcome to TourTrek";

            @SuppressWarnings("unused")
            PackageInfo info=pm.getPackageInfo("com.twitter.android", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.twitter.android");

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(getContext(), "Twitter not Installed", Toast.LENGTH_SHORT).show();
            return ;
        }
        return ;
    }

    private void setAlarmForAttraction(Attraction attraction) {

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("alarms", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Create view button
        Intent viewIntent = new Intent(getContext(), MainActivity.class);
        viewIntent.putExtra("viewId", 1);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(getContext(), 0, viewIntent, 0);

        // Build the notification to display
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "2");
        builder.setContentTitle("Attraction Started");
        builder.setContentText(attraction.getName() + " has started");
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setChannelId("2");
        builder.setContentIntent(viewPendingIntent);
        builder.setAutoCancel(true);
        builder.addAction(R.drawable.ic_profile, "View", viewPendingIntent);
        Notification notification = builder.build();

        // Get Tour Start Date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(attraction.getStartDate());

        try {
            String startTime = attraction.getStartTime();
            SimpleDateFormat df = new SimpleDateFormat("hh:mm aa");
            Date date = df.parse(startTime);
            calendar.set(Calendar.HOUR, date.getHours());
            calendar.set(Calendar.MINUTE, date.getMinutes());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Initialize the alarm manager
        AlarmManager alarmMgr = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmBroadcastReceiver.class);
        String notification_id = String.valueOf(System.currentTimeMillis() % 10000);
        intent.putExtra(AlarmBroadcastReceiver.NOTIFICATION_ID, notification_id);
        intent.putExtra(AlarmBroadcastReceiver.NOTIFICATION, notification);
        intent.putExtra("NOTIFICATION_CHANNEL_ID", "2");
        intent.putExtra("NOTIFICATION_CHANNEL_NAME", "Attraction Start");
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), Integer.parseInt(notification_id), intent, PendingIntent.FLAG_ONE_SHOT);
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

        // Add reference to new alarm so we can cancel later;
        Set<String> attractionAlarms = sharedPreferences.getStringSet(tourViewModel.getSelectedTour().getTourUID(), new HashSet<>());
        attractionAlarms.add(notification_id);
        editor.putStringSet(tourViewModel.getSelectedTour().getTourUID(), attractionAlarms).apply();
    }

    private void removeAlarms() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("alarms", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> alarms = sharedPreferences.getStringSet(tourViewModel.getSelectedTour().getTourUID(), new HashSet<>());

        // Get the alarm manager
        AlarmManager alarmMgr = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);

        for (String notification_id : alarms) {
            Intent intent = new Intent(getContext(), AlarmBroadcastReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), Integer.parseInt(notification_id), intent, PendingIntent.FLAG_ONE_SHOT);
            alarmMgr.cancel(alarmIntent);
        }

        // Remove all items from shared preferences
        editor.clear().apply();
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {

            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(requireActivity(), new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    public void showDatePickerDialog(Button button, Context context, String type) {

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                String date = (month + 1) + "/" + day + "/" + year;
                button.setText(date);
                button.setBackgroundColor(Color.parseColor("#10000000"));

                // Differentiate between startDate and endDate
                TourViewModel tourViewModel = new ViewModelProvider((MainActivity)context).get(TourViewModel.class);
                try {
                    if (type.equals("start"))
                        tourViewModel.getSelectedTour().setStartDateFromString(button.getText().toString());
                    else
                        tourViewModel.getSelectedTour().setEndDateFromString(button.getText().toString());
                } catch (ParseException e) {
                    Log.e(TAG, "Error converting startDate to a firebase Timestamp");
                }
            }
        };

        TourViewModel tourViewModel = new ViewModelProvider((MainActivity)context).get(TourViewModel.class);

        final Calendar calendar = Calendar.getInstance();;

        if (tourViewModel.getSelectedTour().getStartDate() != null)
            calendar.setTime(tourViewModel.getSelectedTour().getStartDate());

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), dateSetListener, year, month, day);

        datePickerDialog.show();
    }

    /**
     * Upon clicking the "Import Tour" button, a copy of the current tour should be added to the user's
     * account.
     * Precondition: The button should only be clicked on a tour in the marketplace. Such a tour already has a UID.
     * @param tourView
     */
    private void setupImportTourButton(View tourView) {
        tourImportButton.setOnClickListener(u -> {
            // create a new Firestore document
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference newTourDoc = db.collection("Tours").document();

            // part of updating the user's list of tours
            MainActivity.user.getTours().add(newTourDoc);

            // get a new tour object with an empty list of attractions and the new tour UID
            Tour tour = copyTour(tourViewModel.getSelectedTour(), newTourDoc.getId(), db);

            // oldAttraction = attractions in the tour to be imported
            List<DocumentReference> oldAttractions = tourViewModel.getSelectedTour().getAttractions();

            // iterate through the list of existing attractions and create new ones
            for (DocumentReference attractionRef : oldAttractions){
                // Add the new attraction document reference to the list of attractions in the tour
                // Doing it here is important because of lambda expression limitations
                DocumentReference newAttractionDoc = db.collection("Attractions").document();
                tour.getAttractions().add(newAttractionDoc);
                // query the old attraction reference
                attractionRef.get().addOnSuccessListener(result -> {
                    // get the old attraction as an object to pull out its fields
                    Attraction oldAttraction = result.toObject(Attraction.class);
                    // create the new attraction
                    Attraction newAttraction = new Attraction(oldAttraction.getReviews(), oldAttraction.getLocation(), oldAttraction.getLat(), oldAttraction.getLon(), oldAttraction.getCost(),
                            oldAttraction.getName(), oldAttraction.getDescription(), newAttractionDoc.getId(), oldAttraction.getStartDate(), oldAttraction.getStartTime(),
                            oldAttraction.getEndDate(), oldAttraction.getEndTime(), oldAttraction.getAddress(), oldAttraction.getCoverImageURI(), oldAttraction.getWeather());
                    // set the new attraction data in Firestore
                    newAttractionDoc.set(newAttraction).addOnSuccessListener(result2 -> {
                        Log.d("TourFragment", "Attraction set w/ tour importing");
                    });
                });
            }

            // set the content of the new Firestore Tour document
            newTourDoc.set(tour).addOnCompleteListener(v -> {
                Log.d(TAG, "The tour was imported.");

            })
                .addOnFailureListener(v1 -> {
                    Log.d(TAG, "Tour importation failed.");
                });

            // update the user's list of tours
            updateUser();

            // go back
            getParentFragmentManager().popBackStack();
        });
    }

    /**
     * Create a new tour which is no different than the one provided except for the UID and having no attraction references.
     * @param oldTour
     * @param newUID
     * @param db
     * @return
     */
    private Tour copyTour(Tour oldTour, String newUID, FirebaseFirestore db){

        // create a new list of attractions
        List<DocumentReference> newAttractions = new ArrayList<DocumentReference>();

        // make a new tour with the same contents as the old one except it is not publicly available and it has a new document ID
        Tour newTour = new Tour(oldTour.getName(), oldTour.getStartDate(), oldTour.getEndDate(), oldTour.getLocation(),
                                oldTour.getCost(), oldTour.getNotifications(), oldTour.getReviews(), oldTour.getDescription(),
                                false, newAttractions, oldTour.getCoverImageURI(), newUID);

        return newTour;
    }

    private void setupNavigationButton(View tourView){
        navigationButton.setOnClickListener(v -> {

            // check that location services are enabled and give a prompt to enable them if needed
//            Boolean permissionIsGranted = PlacesLocal.checkLocationPermission(getContext());
//            if (permissionIsGranted){
//                Log.d(TAG, "Location enabled");

                tourViewModel.setReturnedFromNavigation(true);

                // switch to the map fragment
                final FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment, new MapsFragment(), "MapsFragment");
                ft.addToBackStack("MapsFragment").commit();
//            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showReviewDialog() {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_tour_review, null);
        //Get elements
        RatingBar ratingBar = view.findViewById(R.id.tour_review_rb);
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

    private void updateTourInFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Tours").document(tourViewModel.getSelectedTour().getTourUID())
                .set(tourViewModel.getSelectedTour())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Tour written to firestore");

                    // Update the user in the firestore
                    Firestore.updateUser();

                    Toast.makeText(getContext(), "You successfully rated the tour", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document"));
    }

    private double computeRating(double totalRating) {

        return (totalRating) / tourViewModel.getSelectedTour().getReviews().size();
    }

    private void addNewRating(double newRating) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            tourViewModel.getSelectedTour().addUser(mAuth.getCurrentUser().getUid());

            if (tourViewModel.getSelectedTour().getReviews().equals(null)) {
                tourViewModel.getSelectedTour().setReviews(new ArrayList<>());
                tourViewModel.getSelectedTour().setRating(0);
                tourViewModel.getSelectedTour().setTotalRating(0);
            }

            //Add the new rating to totalRating
            tourViewModel.getSelectedTour().setTotalRating(
                    tourViewModel.getSelectedTour().getTotalRating() + newRating);

            //compute rating
            tourViewModel.getSelectedTour().setRating(computeRating(
                    tourViewModel.getSelectedTour().getTotalRating()));

            //update tour
            updateTourInFirebase();
        }

}

