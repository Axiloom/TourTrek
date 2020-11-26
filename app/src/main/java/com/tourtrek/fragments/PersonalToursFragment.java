package com.tourtrek.fragments;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tourtrek.R;
import com.tourtrek.activities.MainActivity;
import com.tourtrek.adapters.CurrentPersonalToursAdapter;
import com.tourtrek.adapters.FuturePersonalToursAdapter;
import com.tourtrek.adapters.PastPersonalToursAdapter;
import com.tourtrek.data.Tour;
import com.tourtrek.notifications.AlarmBroadcastReceiver;
import com.tourtrek.utilities.ItemClickSupport;
import com.tourtrek.viewModels.TourViewModel;

import java.util.ArrayList;
import java.util.List;

public class PersonalToursFragment extends Fragment {

    private static final String TAG = "ToursFragment";
    private RecyclerView currentRecyclerView;
    private RecyclerView futureRecyclerView;
    private RecyclerView pastRecyclerView;
    private CurrentPersonalToursAdapter currentTourAdapter;
    private FuturePersonalToursAdapter futureTourAdapter;
    private PastPersonalToursAdapter pastTourAdapter;
    private SwipeRefreshLayout currentSwipeRefreshLayout;
    private SwipeRefreshLayout futureSwipeRefreshLayout;
    private SwipeRefreshLayout pastSwipeRefreshLayout;
    private TourViewModel tourViewModel;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Leave empty since we don't want to user to go back to another screen
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View personalToursView = inflater.inflate(R.layout.fragment_personal_tours, container, false);

        mAuth = FirebaseAuth.getInstance();

        // Display login screen if no user was previous logged in
        if (mAuth.getCurrentUser() == null || MainActivity.user == null) {
            NavHostFragment.findNavController(this).navigate(R.id.navigation_login);
            return personalToursView;
        }

        // Initialize view model
        tourViewModel = new ViewModelProvider(requireActivity()).get(TourViewModel.class);

        Button personalFutureToursTitleButton = personalToursView.findViewById(R.id.personal_future_tours_title_btn);

        personalFutureToursTitleButton.setOnClickListener(
                view -> {

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    // Create a new tour in firebase
                    tourViewModel.setSelectedTour(new Tour());
                    final DocumentReference tourDocumentReference = db.collection("Tours").document();
                    tourViewModel.getSelectedTour().setTourUID(tourDocumentReference.getId());
                    db.collection("Tours").document(tourDocumentReference.getId()).set(tourViewModel.getSelectedTour());
                    tourViewModel.setIsNewTour(true);
                    MainActivity.user.addTourToTours(tourDocumentReference);

                    final FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.replace(R.id.nav_host_fragment, new TourFragment(), "TourFragment");
                    ft.addToBackStack("TourFragment").commit();
                });

        // Configure recycler views
        configureRecyclerViews(personalToursView);
        configureSwipeRefreshLayouts(personalToursView);
        configureOnClickRecyclerView();

        return personalToursView;
    }

    /**
     * Configure the recycler view
     *
     * @param view current view
     */
    public void configureRecyclerViews(View view) {

        // Current

        // Get our recycler view from the layout
        currentRecyclerView = view.findViewById(R.id.personal_current_tours_rv);

        // Improves performance because content does not change size
        currentRecyclerView.setHasFixedSize(true);

        // Only load 10 tours before loading more
        currentRecyclerView.setItemViewCacheSize(10);

        // Enable drawing cache
        currentRecyclerView.setDrawingCacheEnabled(true);
        currentRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // User linear layout manager
        RecyclerView.LayoutManager currentToursLayoutManager = new LinearLayoutManager(getContext());
        currentRecyclerView.setLayoutManager(currentToursLayoutManager);

        // Specify an adapter
        currentTourAdapter = new CurrentPersonalToursAdapter(getContext());

        // Get all current tours
        fetchToursAsync("current");

        currentRecyclerView.setAdapter(currentTourAdapter);

        // Stop showing progressBar when items are loaded
        currentRecyclerView
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        () -> ((CurrentPersonalToursAdapter)currentTourAdapter).stopLoading());

        // Future

        // Get our recycler view from the layout
        futureRecyclerView = view.findViewById(R.id.personal_future_tours_rv);

        // Improves performance because content does not change size
        futureRecyclerView.setHasFixedSize(true);

        // Only load 10 tours before loading more
        futureRecyclerView.setItemViewCacheSize(10);

        // Enable drawing cache
        futureRecyclerView.setDrawingCacheEnabled(true);
        futureRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // User linear layout manager
        RecyclerView.LayoutManager futureToursLayoutManager = new LinearLayoutManager(getContext());
        futureRecyclerView.setLayoutManager(futureToursLayoutManager);

        // Specify an adapter
        futureTourAdapter = new FuturePersonalToursAdapter(getContext());

        // Get all current tours
        fetchToursAsync("future");

        futureRecyclerView.setAdapter(futureTourAdapter);

        // Stop showing progressBar when items are loaded
        futureRecyclerView
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(() -> ((FuturePersonalToursAdapter)futureTourAdapter).stopLoading());

        // Past

        // Get our recycler view from the layout
        pastRecyclerView = view.findViewById(R.id.personal_past_tours_rv);

        // Improves performance because content does not change size
        pastRecyclerView.setHasFixedSize(true);

        // Only load 10 tours before loading more
        pastRecyclerView.setItemViewCacheSize(10);

        // Enable drawing cache
        pastRecyclerView.setDrawingCacheEnabled(true);
        pastRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // User linear layout manager
        RecyclerView.LayoutManager pastToursLayoutManager = new LinearLayoutManager(getContext());
        pastRecyclerView.setLayoutManager(pastToursLayoutManager);

        // Specify an adapter
        pastTourAdapter = new PastPersonalToursAdapter(getContext());

        fetchToursAsync("past");

        pastRecyclerView.setAdapter(pastTourAdapter);

        // Stop showing progressBar when items are loaded
        pastRecyclerView
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(() -> ((PastPersonalToursAdapter)pastTourAdapter).stopLoading());

    }

    /**
     * Configure the swipe down to refresh function of our recycler view
     *
     * @param view current view
     */
    public void configureSwipeRefreshLayouts(View view) {

        // Current
        currentSwipeRefreshLayout = view.findViewById(R.id.personal_current_tours_srl);
        currentSwipeRefreshLayout.setOnRefreshListener(() -> fetchToursAsync("current"));

        // Future
        futureSwipeRefreshLayout = view.findViewById(R.id.personal_future_tours_srl);
        futureSwipeRefreshLayout.setOnRefreshListener(() -> fetchToursAsync("future"));

        // Past
        pastSwipeRefreshLayout = view.findViewById(R.id.personal_past_tours_srl);
        pastSwipeRefreshLayout.setOnRefreshListener(() -> fetchToursAsync("past"));

    }

    /**
     * Enables the click listener for each item in our recycler view
     */
    public void configureOnClickRecyclerView() {

        // Current
        ItemClickSupport.addTo(currentRecyclerView, R.layout.item_personal_tour)
                .setOnItemClickListener((recyclerView, position, v) -> {

                    // Reference to the current tour selected
                    Tour tour = ((CurrentPersonalToursAdapter) currentTourAdapter).getData(position);

                    // Add the selected tour to the view model so we can access the tour inside the fragment
                    tourViewModel.setSelectedTour(tour);

                    // Display the tour selected
                    final FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.replace(R.id.nav_host_fragment, new TourFragment(), "TourFragment");
                    ft.addToBackStack("TourFragment").commit();
                });

        // Future
        ItemClickSupport.addTo(futureRecyclerView, R.layout.item_personal_tour)
                .setOnItemClickListener((recyclerView, position, v) -> {

                    // Reference to the current tour selected
                    Tour tour = ((FuturePersonalToursAdapter) futureTourAdapter).getTour(position);

                    // Add the selected tour to the view model so we can access the tour inside the fragment
                    tourViewModel.setSelectedTour(tour);

                    // Display the tour selected
                    final FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.replace(R.id.nav_host_fragment, new TourFragment(), "TourFragment");
                    ft.addToBackStack("TourFragment").commit();
                });

        // Past
        ItemClickSupport.addTo(pastRecyclerView, R.layout.item_personal_tour)
                .setOnItemClickListener((recyclerView, position, v) -> {

                    // Reference to the current tour selected
                    Tour tour = ((PastPersonalToursAdapter) pastTourAdapter).getData(position);

                    // Add the selected tour to the view model so we can access the tour inside the fragment
                    tourViewModel.setSelectedTour(tour);

                    // Display the tour selected
                    final FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.replace(R.id.nav_host_fragment, new TourFragment(), "TourFragment");
                    ft.addToBackStack("TourFragment").commit();
                });
    }

    /**
     * Retrieve all tours belonging to this user
     *
     * @param type type of tours to fetch
     */
    private void fetchToursAsync(String type) {

        // Get instance of firestore
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Setup collection reference
        CollectionReference toursCollection = db.collection("Tours");

        // Pull out the UID's of each tour that belongs to this user
        if (!MainActivity.user.getTours().isEmpty()) {

            currentTourAdapter.clear();

            if (MainActivity.user.getTours().isEmpty())
                ((MainActivity)requireActivity()).enableTabs();

            for (DocumentReference documentReference : MainActivity.user.getTours()) {

                ((MainActivity)requireActivity()).disableTabs();

                toursCollection.document(documentReference.getId()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                Timestamp tourStartDate = (Timestamp) documentSnapshot.get("startDate");
                                Timestamp tourEndDate = (Timestamp) documentSnapshot.get("endDate");
                                Timestamp now = Timestamp.now();

                                // the start date is before now and the end date is after now
                                if (type.equals("current") && tourStartDate != null && tourStartDate.compareTo(now) < 0 && tourEndDate != null && tourEndDate.compareTo(now) > 0) {
                                    currentTourAdapter.addNewData(documentSnapshot.toObject(Tour.class));
                                    currentSwipeRefreshLayout.setRefreshing(false);
                                }
                                // the start date is after now and the end date is after now
                                else if (type.equals("future") && tourStartDate != null && tourStartDate.compareTo(now) > 0 && tourEndDate != null && tourEndDate.compareTo(now) > 0) {
                                    ((FuturePersonalToursAdapter) futureTourAdapter).addNewData(documentSnapshot.toObject(Tour.class));
                                    futureSwipeRefreshLayout.setRefreshing(false);
                                }
                                // the start date and end dates are before now
                                else if (type.equals("past") && tourStartDate != null && tourStartDate.compareTo(now) < 0 && tourEndDate != null && tourEndDate.compareTo(now) < 0) {
                                    ((PastPersonalToursAdapter) pastTourAdapter).addNewData(documentSnapshot.toObject(Tour.class));
                                    pastSwipeRefreshLayout.setRefreshing(false);
                                }

                                ((MainActivity)requireActivity()).enableTabs();
                            }
                        });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setActionBarTitle("Personal Tours");
    }

}