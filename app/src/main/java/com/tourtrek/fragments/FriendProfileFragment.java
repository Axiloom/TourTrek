package com.tourtrek.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tourtrek.R;
import com.tourtrek.activities.MainActivity;
import com.tourtrek.adapters.FriendsOfFriendsAdapter;
import com.tourtrek.adapters.ToursOfFriendsAdapter;
import com.tourtrek.data.Tour;
import com.tourtrek.data.User;
import com.tourtrek.utilities.Firestore;
import com.tourtrek.viewModels.FriendViewModel;
import com.tourtrek.viewModels.TourViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class FriendProfileFragment extends Fragment {

    private static final String TAG = "AddFriendFragment";
    private RecyclerView friendsRecyclerView;
    private SwipeRefreshLayout friendsSwipeRefreshLayout;
    private Button deleteFriendButton;


    private SwipeRefreshLayout friendsOfFriendsSwipeRefreshLayout;
    private RecyclerView toursOfFriendsRecyclerView;
    private SwipeRefreshLayout toursOfFriendsSwipeRefreshLayout;
    private FriendsOfFriendsAdapter friendsListAdapter;
    private ToursOfFriendsAdapter toursListAdapter;

    private FriendViewModel friendViewModel;
    private TourViewModel tourViewModel;

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
        View friendProfileView = inflater.inflate(R.layout.fragment_friend, container, false);

        // Initialize view models
        friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);
        tourViewModel = new ViewModelProvider(requireActivity()).get(TourViewModel.class);

        //Set Username
        TextView usernameTextView = friendProfileView.findViewById(R.id.profile_username_tv);
        usernameTextView.setText(friendViewModel.getSelectedFriend().getUsername());

        //Set Profile Picture
        ImageView profileUserImageView = friendProfileView.findViewById(R.id.profile_user_iv);
        Glide.with(getContext())
                .load(friendViewModel.getSelectedFriend().getProfileImageURI())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_profile)
                .circleCrop()
                .into(profileUserImageView);

        //configure delete button
        configureDeleteBtn(friendProfileView);

        //populate friends list of user
        friendsRecyclerView = friendProfileView.findViewById(R.id.friend_friends_rv);
        configureRecyclerViewsFriends(friendsRecyclerView);
        configureSwipeRefreshLayouts(friendProfileView);

        //populate tours list of user
        toursOfFriendsRecyclerView = friendProfileView.findViewById(R.id.friend_tours_rv);
        configureRecyclerViewsTours(toursOfFriendsRecyclerView);
        configureSwipeRefreshLayoutsTours(friendProfileView);


        return friendProfileView;
    }

    private void configureDeleteBtn(View friendProfileView) {

        deleteFriendButton=friendProfileView.findViewById(R.id.friend_delete_btn);

        User currentFriend = friendViewModel.getSelectedFriend();
        final String email=currentFriend.getEmail();

        // Get instance of firestore
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Setup collection reference
        CollectionReference usersCollection = db.collection("Users");

        // Query database
        usersCollection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                        // Go through each document and compare the dates
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {

                            // First check that the document belongs to the user
                            if (document.get("email").equals(email)) {
                                final DocumentReference friendReference = document.getReference();

                                //check if selected friend is in current user's friendlist
                                if(MainActivity.user.getFriends().contains(friendReference)){
                                    deleteFriendButton.setVisibility(View.VISIBLE);
                                    deleteFriendButton.setOnClickListener(view -> {
                                        List newFriendsList= MainActivity.user.getFriends();
                                        newFriendsList.remove(friendReference);
                                        MainActivity.user.setFriends(newFriendsList);
                                        Firestore.updateUser();
                                        // toast message
                                        Toast.makeText(getContext(), "Friend removed", Toast.LENGTH_SHORT).show();


                                        // go back
                                        getParentFragmentManager().popBackStack();
                                    });
                                }
                            }
                        }
                });


    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setActionBarTitle("Add Friend");
    }


    /**
     * Configure the recycler view for friends
     *
     * @param RV current recycler view needed
     */
    public void configureRecyclerViewsFriends(RecyclerView RV ) {

        // Improves performance because content does not change size
        RV.setHasFixedSize(true);

        // Only load 10 friends before loading more
        RV.setItemViewCacheSize(10);

        // Enable drawing cache
        RV.setDrawingCacheEnabled(true);
        RV.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // User linear layout manager
        RecyclerView.LayoutManager friendsLayoutManager = new LinearLayoutManager(getContext());
        RV.setLayoutManager(friendsLayoutManager);

        // Get all friends
        fetchFriendUsersAsync();

        // Specify an adapter
        friendsListAdapter = new FriendsOfFriendsAdapter(getContext());
        RV.setAdapter(friendsListAdapter);

        // Stop showing progressBar when items are loaded
        RV
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(() -> ((FriendsOfFriendsAdapter) friendsListAdapter).stopLoading());


    }
    /**
     * Configure the recycler view for friends
     *
     * @param RV current recycler view needed
     */
    public void configureRecyclerViewsTours(RecyclerView RV ) {

        // Improves performance because content does not change size
        RV.setHasFixedSize(true);

        // Only load 10 friends before loading more
        RV.setItemViewCacheSize(10);

        // Enable drawing cache
        RV.setDrawingCacheEnabled(true);
        RV.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // User linear layout manager
        RecyclerView.LayoutManager toursLayoutManager = new LinearLayoutManager(getContext());
        RV.setLayoutManager(toursLayoutManager);

        // Get all friends
        fetchToursAsync();

        // Specify an adapter
        toursListAdapter = new ToursOfFriendsAdapter(getContext());
        RV.setAdapter(toursListAdapter);

        // Stop showing progressBar when items are loaded
        RV
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(() -> ((ToursOfFriendsAdapter) toursListAdapter).stopLoading());


    }

    private void fetchToursAsync() {
        // Get instance of firestore
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Setup collection reference
        CollectionReference toursCollection = db.collection("Tours");

        // Pull out the UID's of each friend that belongs to this user
        List<String> toursUIDs = new ArrayList<>();
        if (!friendViewModel.getSelectedFriend().getTours().isEmpty()) {
            for (DocumentReference documentReference : friendViewModel.getSelectedFriend().getTours()) {
                toursUIDs.add(documentReference.getId());
            }
        }

        // Query database
        toursCollection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.w(TAG, "No documents found in the Users collection");
                    }
                    else {

                        // Final list of friends for this category
                        List<Tour> usersTours = new ArrayList<>();

                        // Go through each document and compare the user IDs
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {

                            // First check that the document belongs to the user
                            if (toursUIDs.contains(document.getId())) {

                                usersTours.add(document.toObject(Tour.class));

                            }
                        }
                        ((ToursOfFriendsAdapter) toursListAdapter).clear();
                        ((ToursOfFriendsAdapter) toursListAdapter).addAll(usersTours);

                        friendsSwipeRefreshLayout.setRefreshing(false);

                    }
                });
    }

    /**
     * Configure the swipe down to refresh function of our recycler view
     *
     * @param view current view
     */
    public void configureSwipeRefreshLayoutsTours(View view) {


        friendsSwipeRefreshLayout = view.findViewById(R.id.friend_tours_srl);
        friendsSwipeRefreshLayout.setOnRefreshListener(() -> fetchToursAsync());

    }
    /**
     * Configure the swipe down to refresh function of our recycler view
     *
     * @param view current view
     */
    public void configureSwipeRefreshLayouts(View view) {


        friendsSwipeRefreshLayout = view.findViewById(R.id.friend_friends_srl);
        friendsSwipeRefreshLayout.setOnRefreshListener(() -> fetchFriendUsersAsync());

    }




    /**
     * Retrieve all friends belonging to the friend selected
     *
     */
    private void fetchFriendUsersAsync() {

        // Get instance of firestore
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Setup collection reference
        CollectionReference usersCollection = db.collection("Users");

        // Pull out the UID's of each friend that belongs to this user
        List<String> usersUIDs = new ArrayList<>();
        if (!friendViewModel.getSelectedFriend().getFriends().isEmpty()) {
            for (DocumentReference documentReference : friendViewModel.getSelectedFriend().getFriends()) {
                usersUIDs.add(documentReference.getId());
            }
        }

        // Query database
        usersCollection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.w(TAG, "No documents found in the Users collection");
                    }
                    else {

                        // Final list of friends for this category
                        List<User> usersFriends = new ArrayList<>();

                        // Go through each document and compare the user IDs
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {

                            // First check that the document belongs to the user
                            if (usersUIDs.contains(document.getId())) {

                                usersFriends.add(document.toObject(User.class));

                            }
                        }
                        ((FriendsOfFriendsAdapter) friendsListAdapter).clear();
                        ((FriendsOfFriendsAdapter) friendsListAdapter).addAll(usersFriends);

                        friendsSwipeRefreshLayout.setRefreshing(false);

                    }
                });
    }

}
