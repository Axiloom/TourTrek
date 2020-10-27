package com.tourtrek.fragments;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tourtrek.R;
import com.tourtrek.activities.MainActivity;
import com.tourtrek.data.Attraction;
import com.tourtrek.data.Tour;
import com.tourtrek.viewModels.TourViewModel;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

/**
 * This fragment corresponds to the user story of viewing the attractions in a tour and having the option of adding an attraction.
 * When the user opts to add an attraction, they are directed to AddAttractionFragment
 */
public class TourFragment extends Fragment {

    private static final String TAG = "TourFragment";
    ListView attractionsList;
    List<String> attractions;
    ArrayAdapter<String> arrayAdapter;
    private TourViewModel tourViewModel;
    Tour tour;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Pop the last fragment off the stack
                getActivity().getSupportFragmentManager().popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        attractions = new ArrayList<>();
        tourViewModel = new ViewModelProvider(this.getActivity()).get(TourViewModel.class);
        // Grab a reference to the current view
        View tourView = inflater.inflate(R.layout.tour_fragment, container, false);
        // get current tour
        this.tour = tourViewModel.getSelectedTour();

        // list view stuff
        // set up the list of attractions
        attractionsList = tourView.findViewById(R.id.attractions_lv);
        // pull out the names of attractions into a list of strings for populating the ArrayAdapter
        try {
            getAttractionNames(this.tour);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //attractionsList.setAdapter(arrayAdapter);
        // System.out.println(attractions + "MAIN");
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, attractions);
        attractionsList.setAdapter(arrayAdapter);
        // end list view stuff

        // set up the name of the tour
        TextView tourNameTextView = tourView.findViewById(R.id.tour_tour_name_tv);
        tourNameTextView.setText(this.tour.getName());

        // set up the cover image
        ImageView tourCoverImageView = tourView.findViewById(R.id.tour_cover_iv);
        Glide.with(getContext()).load(this.tour.getCoverImageURI()).into(tourCoverImageView);

        // set up the add attraction button
        Button tour_attractions_btn = tourView.findViewById(R.id.tour_attractions_btn);
        tour_attractions_btn.setOnClickListener(v -> {
            final FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, new AddAttractionFragment(), "AddAttractionFragment");
            ft.addToBackStack("AddAttractionFragment").commit();
        });

        // clear the attractions list to avoid duplication when the user goes back to this fragment
        attractions = null;
        return tourView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setActionBarTitle(this.tour.getName());
    }

    private void getAttractionNames(Tour tour) throws ExecutionException, InterruptedException {

        // Get instance of firestore
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Setup collection reference
        CollectionReference attractionsCollection = db.collection("Tours");

        // Pull out the UID's of each tour that belongs to this user
        List<String> usersAttractionUIDs = new ArrayList<>();
        if (!tour.getAttractions().isEmpty()) {
            for (DocumentReference documentReference : tour.getAttractions()) {
                usersAttractionUIDs.add(documentReference.getId());
            }
        }

        attractionsCollection
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (queryDocumentSnapshots.isEmpty())

                    }
                });


        for (DocumentReference current:tour.getAttractions()){
            Task<DocumentSnapshot> task = current.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Attraction attraction = documentSnapshot.toObject(Attraction.class);
                        attractions.add(attraction.getName());
                        // addAttraction(attraction.getName());
                    }
                }
            });
        }
    }

    public List<String> getAttractions() {
        if (this.attractions != null){
            return this.attractions;
        }
        return new ArrayList<String>();
    }

    public void setAttractions(List<String> attractions) {
        this.attractions = attractions;
    }

    public void addAttraction(String attr){
        if (this.attractions == null){
            this.attractions = new ArrayList<String>();
            this.attractions.add(attr);
            return;
        }
        this.attractions.add(attr);
    }

}