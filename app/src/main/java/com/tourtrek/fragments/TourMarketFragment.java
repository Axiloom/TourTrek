package com.tourtrek.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.Spinner;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;

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
import com.tourtrek.adapters.CurrentTourAttractionsAdapter;
import com.tourtrek.adapters.TourMarketAdapter;
import com.tourtrek.data.Tour;
import com.tourtrek.utilities.TourLengthSorter;
import com.tourtrek.utilities.TourLocationSorter;
import com.tourtrek.utilities.TourNameSorter;
import com.tourtrek.utilities.ItemClickSupport;
import com.tourtrek.utilities.TourRatingSorter;
import com.tourtrek.utilities.Utilities;
import com.tourtrek.viewModels.TourViewModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TourMarketFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "TourMarketFragment";
    private RecyclerView recyclerView;
    private TourMarketAdapter tourMarketAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TourViewModel tourViewModel;
    private String[] items = {"Name Ascending", "Location Ascending", "Duration Ascending",
            "Rating Ascending", "Name Descending", "Location Descending", "Duration Descending",
            "Rating Descending"};
    private String result = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Leave empty since we don't want to user to go back to another screen
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View tourMarketView = inflater.inflate(R.layout.fragment_tour_market, container, false);

        // Initialize view model
        tourViewModel = new ViewModelProvider(requireActivity()).get(TourViewModel.class);

       showSortingDialog(tourMarketView);



        configureRecyclerView(tourMarketView);
        configureSwipeRefreshLayout(tourMarketView);
        configureOnClickRecyclerView();

        return tourMarketView;
    }

    public void showSortingDialog(View view) {
        Button sortButton = view.findViewById(R.id.tour_market_btn);
        AlertDialog dialog;
        AlertDialog.Builder builder;

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
                sortTours(tourMarketAdapter, result);
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog = builder.create();
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
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
                        Tour tour = tourMarketAdapter.getData(position);

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

        ((MainActivity)requireActivity()).disableTabs();

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
                            (tourMarketAdapter).clear();
                            (tourMarketAdapter).addAll(tours);
                            tourMarketAdapter.copyTours(tours);

                            // Stop showing refresh decorator
                            swipeRefreshLayout.setRefreshing(false);

                            if (isAdded())
                                ((MainActivity)requireContext()).enableTabs();
                        }
                        else {
                            Log.w(TAG, "Error retrieving tours from firestore: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        // Show the top app bar with the search icon
        inflater.inflate(R.menu.tour_market_search_menu, menu);

        // Get the menu item
        MenuItem item = menu.findItem(R.id.tour_market_search_itm);

        SearchView searchView = (SearchView) item.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchTours(tourMarketAdapter, query);
                Activity currentActivity = requireActivity();
                Utilities.hideKeyboard(currentActivity);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchTours(tourMarketAdapter, newText);

                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setActionBarTitle("Tour Market");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String key = (String) parent.getItemAtPosition(position);
        sortTours(tourMarketAdapter, key);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    public void searchTours(TourMarketAdapter adapter, String newText){
        ArrayList<Tour> data = new ArrayList<>(adapter.getDataSet());

        List<Tour> filteredTourList = findTours(data, newText);

        adapter.clear();
        adapter.setDataSetFiltered(filteredTourList);
        adapter.addAll(filteredTourList);
    }

    public List<Tour> findTours(List<Tour> data, String newText){
        ArrayList<Tour> originalList = new ArrayList<>(data);
        List<Tour> filteredTourList = new ArrayList<>();

        if (newText == null || newText.length() == 0) {

            filteredTourList.addAll(originalList);

        } else {

            String key = newText.toLowerCase();

            for(Tour tour: originalList){
                if(tour.getName().toLowerCase().contains(key)){
                    filteredTourList.add(tour);
                }
            }
        }

        return filteredTourList;
    }

    public void sortTours(TourMarketAdapter adapter, String key){

        ArrayList<Tour> data = new ArrayList<>(adapter.getDataSetFiltered());

        List<Tour> temp = sortedTours(data, key);
        adapter.clear();
        adapter.addAll(temp);
    }

    public List<Tour> sortedTours(List<Tour> data, String key){
        List<Tour> temp = new ArrayList<>(data);
        switch (key){

            case "Name Ascending":
                Collections.sort(temp, new TourNameSorter());
                break;

            case "Location Ascending":
                Collections.sort(temp, new TourLocationSorter());
                break;

            case "Duration Ascending":
                Collections.sort(temp, new TourLengthSorter());
                break;

            case "Rating Ascending":
                Collections.sort(temp, new TourRatingSorter());
                break;

            case "Name Descending":
                Collections.sort(temp, new TourNameSorter());
                Collections.reverse(temp);
                break;

            case "Location Descending":
                Collections.sort(temp, new TourLocationSorter());
                Collections.reverse(temp);
                break;

            case "Duration Descending":
                Collections.sort(temp, new TourLengthSorter());
                Collections.reverse(temp);
                break;

            case "Rating Descending":
                Collections.sort(temp, new TourRatingSorter());
                Collections.reverse(temp);
                break;

            default:
                return temp;
        }

        return temp;
    }

}

