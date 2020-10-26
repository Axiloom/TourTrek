package com.tourtrek.fragments;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.ActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tourtrek.R;
import com.tourtrek.activities.MainActivity;
import com.tourtrek.adapters.TourMarketAdapter;
import com.tourtrek.data.Tour;
import com.tourtrek.utilities.ItemClickSupport;
import com.tourtrek.viewModels.TourViewModel;

import java.util.ArrayList;

import static androidx.core.content.ContextCompat.getSystemService;

public class TourMarketFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    private static final String TAG = "TourMarketFragment";
    private RecyclerView recyclerView;
    private TourMarketAdapter tourMarketAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TourViewModel tourViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View tourMarketView = inflater.inflate(R.layout.fragment_tour_market, container, false);

        // Initialize view model
        tourViewModel = new ViewModelProvider(this.getActivity()).get(TourViewModel.class);

        configureRecyclerView(tourMarketView);
        configureSwipeRefreshLayout(tourMarketView);
        configureOnClickRecyclerView();

        return tourMarketView;
    }

    /**
     * Enables the click listener for each item in our recycler view
     */
    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.item_tour)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        // Reference to the current tour selected
                        Tour tour = ((TourMarketAdapter)tourMarketAdapter).getTour(position);

                        // Add the selected tour to the view model so we can access the tour inside the fragment
                        tourViewModel.setSelectedTour(tour);

                        // Display the tour selected
                        final FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                        ft.replace(R.id.nav_host_fragment, new TourFragment(), "TourFragment");
                        ft.addToBackStack("TourFragment").commit();
                    }
                });
    }

    /**
     * Configure the recycler view
     *
     * @param view current view
     */
    private void configureRecyclerView(View view) {

        // Get our recycler view from the layout
        recyclerView = view.findViewById(R.id.tour_market_rv);

        // Improves performance because content does not change size
        recyclerView.setHasFixedSize(true);

        // Only load 10 tours before loading more
        recyclerView.setItemViewCacheSize(10);

        // Enable drawing cache
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // User linear layout manager
        RecyclerView.LayoutManager tourMarketLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(tourMarketLayoutManager);

        // Fetch all the public tours
        fetchToursAsync();

        // Specify an adapter
        tourMarketAdapter = new TourMarketAdapter(getContext());
        recyclerView.setAdapter(tourMarketAdapter);

    }

    /**
     * Configure the swipe down to refresh function of our recycler view
     *
     * @param view current view
     */
    private void configureSwipeRefreshLayout(View view) {

        swipeRefreshLayout = view.findViewById(R.id.tour_market_srl);
        swipeRefreshLayout.setOnRefreshListener(this::fetchToursAsync);
    }

    /**
     * Retrieve all publicly facing tours from firestore
     */
    private void fetchToursAsync() {

        // Get instance of firestore
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Setup collection reference
        CollectionReference toursCollection = db.collection("Tours");

        // Setup basic query information
        Query query = toursCollection.whereEqualTo("publiclyAvailable", true);

        // Query database
        query
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            // For holding all our tours
                            ArrayList<Tour> tours = new ArrayList<>();

                            // Convert each document into its object
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                tours.add(documentSnapshot.toObject(Tour.class));
                            }

                            Log.i(TAG, "Successfully retrieved " + tours.size() + " tours from the firestore");

                            // Clear and add tours
                            ((TourMarketAdapter)tourMarketAdapter).clear();
                            ((TourMarketAdapter)tourMarketAdapter).addAll(tours);

                            // Stop showing refresh decorator
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        else {
                            Log.w(TAG, "Error retrieving tours from firestore: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tourMarketAdapter.getFilter().filter(newText);
                return true;
            }
        });

       
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        tourMarketAdapter.getFilter().filter(newText);
        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }
    @Override
    public void onResume() {

        super.onResume();
        ((MainActivity) getActivity()).setActionBarTitle("Tour Market");
    }





}

