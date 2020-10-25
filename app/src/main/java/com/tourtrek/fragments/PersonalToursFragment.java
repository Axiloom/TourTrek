package com.tourtrek.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.tourtrek.utilities.ItemClickSupport;
import com.tourtrek.viewModels.TourViewModel;

import java.util.ArrayList;
import java.util.List;

public class PersonalToursFragment extends Fragment {

    private static final String TAG = "ToursFragment";
    private RecyclerView currentRecyclerView;
    private RecyclerView futureRecyclerView;
    private RecyclerView pastRecyclerView;
    private RecyclerView.Adapter currentTourAdapter;
    private RecyclerView.Adapter futureTourAdapter;
    private RecyclerView.Adapter pastTourAdapter;
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        // Get the current nav backstack
        NavController navController = NavHostFragment.findNavController(this);

        // Display login screen if no user was previous logged in
        if (mAuth.getCurrentUser() == null || MainActivity.user == null) {
            navController.navigate(R.id.navigation_login);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View personalToursView = inflater.inflate(R.layout.fragment_personal_tours, container, false);

        // Initialize view model
        tourViewModel = new ViewModelProvider(this.getActivity()).get(TourViewModel.class);

        Button personalFutureToursTitleButton = personalToursView.findViewById(R.id.personal_future_tours_title_btn);

        // TODO: Replace this listener when implementing AddTourFragment
        personalFutureToursTitleButton.setOnClickListener(
                view -> Toast.makeText(getContext(), "Show add tour fragment here", Toast.LENGTH_SHORT).show());

        if (MainActivity.user != null) {

            // Configure recycler views
            configureRecyclerViews(personalToursView);
            configureSwipeRefreshLayouts(personalToursView);
            configureOnClickRecyclerView();

        }

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

            // Get all current tours
            fetchToursAsync("current");

            // Specify an adapter
            currentTourAdapter = new CurrentPersonalToursAdapter(getContext());
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

            // Get all current tours
            fetchToursAsync("future");

            // Specify an adapter
            futureTourAdapter = new FuturePersonalToursAdapter(getContext());
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

            fetchToursAsync("past");

            // Specify an adapter
            pastTourAdapter = new PastPersonalToursAdapter(getContext());
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
                    Tour tour = ((CurrentPersonalToursAdapter) currentTourAdapter).getTour(position);

                    // Add the selected tour to the view model so we can access the tour inside the fragment
                    tourViewModel.setSelectedTour(tour);
                    MainActivity.user.setCurrentTourObj(tour);

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
                    MainActivity.user.setCurrentTourObj(tour);
                    // Display the tour selected
                    final FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.replace(R.id.nav_host_fragment, new TourFragment(), "TourFragment");
                    ft.addToBackStack("TourFragment").commit();
                });

        // Past
        ItemClickSupport.addTo(pastRecyclerView, R.layout.item_personal_tour)
                .setOnItemClickListener((recyclerView, position, v) -> {

                    // Reference to the current tour selected
                    Tour tour = ((PastPersonalToursAdapter) pastTourAdapter).getTour(position);

                    // Add the selected tour to the view model so we can access the tour inside the fragment
                    tourViewModel.setSelectedTour(tour);
                    MainActivity.user.setCurrentTourObj(tour);
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
        List<String> usersToursUIDs = new ArrayList<>();
        if (!MainActivity.user.getTours().isEmpty()) {
            for (DocumentReference documentReference : MainActivity.user.getTours()) {
                usersToursUIDs.add(documentReference.getId());
            }
        }

        // Query database
        toursCollection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.w(TAG, "No documents found in the Tours collection");
                    }
                    else {

                        // Final list of tours for this category
                        List<Tour> usersTours = new ArrayList<>();

                        // Go through each document and compare the dates
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {

                            // First check that the document belongs to the user
                            if (usersToursUIDs.contains(document.getId())) {

                                Timestamp tourStartDate = (Timestamp) document.get("startDate");
                                Timestamp now = Timestamp.now();
                                Long length = (Long)document.get("length");

                                // Next query based on timestamp
                                if (type.equals("current")) { // TODO: filter based on tour length and timestamp
                                }
                                else if (type.equals("future") && tourStartDate.compareTo(now) > 0) {
                                    usersTours.add(document.toObject(Tour.class));
                                }
                                else if (type.equals("past") && tourStartDate.compareTo(now) < 0) {
                                    usersTours.add(document.toObject(Tour.class));
                                }
                            }
                        }

                        switch (type) {

                            case "current":
                                ((CurrentPersonalToursAdapter) currentTourAdapter).clear();
                                ((CurrentPersonalToursAdapter) currentTourAdapter).addAll(usersTours);
                                currentSwipeRefreshLayout.setRefreshing(false);
                                break;
                            case "future":
                                ((FuturePersonalToursAdapter) futureTourAdapter).clear();
                                ((FuturePersonalToursAdapter) futureTourAdapter).addAll(usersTours);
                                futureSwipeRefreshLayout.setRefreshing(false);
                                break;
                            case "past":
                                ((PastPersonalToursAdapter) pastTourAdapter).clear();
                                ((PastPersonalToursAdapter) pastTourAdapter).addAll(usersTours);
                                pastSwipeRefreshLayout.setRefreshing(false);
                                break;
                        }
                    }
                });
    }

}