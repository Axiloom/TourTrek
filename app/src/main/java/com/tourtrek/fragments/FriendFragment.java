package com.tourtrek.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.tourtrek.adapters.FriendsAdapter;
import com.tourtrek.data.Tour;
import com.tourtrek.data.User;
import com.tourtrek.utilities.Firestore;
import com.tourtrek.utilities.ItemClickSupport;
import com.tourtrek.utilities.Utilities;
import com.tourtrek.viewModels.FriendViewModel;
import com.tourtrek.viewModels.TourViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class FriendFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "AddFriendFragment";
    private RecyclerView friendsRecyclerView;
    private SwipeRefreshLayout friendsSwipeRefreshLayout;
    private RecyclerView friendsOfFriendsRecyclerView;
    private SwipeRefreshLayout friendsOfFriendsSwipeRefreshLayout;
    private RecyclerView toursOfFriendsRecyclerView;
    private SwipeRefreshLayout toursOfFriendsSwipeRefreshLayout;
    private FriendsAdapter friendsAdapter;

    private FriendViewModel friendViewModel;

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
        View addFriendView = inflater.inflate(R.layout.fragment_add_friend, container, false);

        // Initialize view model
        friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);


        // set up the action to carry out via the search button
        setupSearchButton(addFriendView);

        // Configure recycler views
        friendsRecyclerView = addFriendView.findViewById(R.id.add_friend_my_friends_rv);
        configureRecyclerViews(friendsRecyclerView);
        configureSwipeRefreshLayouts(addFriendView);
        configureOnClickRecyclerView();

        return addFriendView;
    }

    private void setupSearchButton(View addFriendView) {


        final EditText emailEditText = addFriendView.findViewById(R.id.add_friend_email_et);
        Button searchButton = addFriendView.findViewById(R.id.add_friend_search_btn);
        final ProgressBar loadingProgressBar = addFriendView.findViewById(R.id.add_friend_loading_pb);
        final TextView errorTextView = addFriendView.findViewById(R.id.add_friend_error_tv);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Close keyboard
                Utilities.hideKeyboard(requireActivity());

                // Start loading the progress circle
                loadingProgressBar.setVisibility(View.VISIBLE);

                // Grab the information the user entered
                final String email = emailEditText.getText().toString();

                // Check to make sure some input was entered
                if (email.equals("")) {

                    // Show error to user
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("Please enter your friend's email");

                    // Stop loading progress circle
                    loadingProgressBar.setVisibility(View.GONE);
                }else if(email.equals(MainActivity.user.getEmail())){
                    // Show error to user
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("You cannot add yourself");

                    // Stop loading progress circle
                    loadingProgressBar.setVisibility(View.GONE);
                } else{

                    // Get instance of firestore
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();

                    // Setup collection reference
                    CollectionReference usersCollection = db.collection("Users");

                    // Query database
                    usersCollection
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {

                                if (queryDocumentSnapshots.isEmpty()) {
                                    Log.w(TAG, "No documents found with current email");
                                }
                                else {

                                    // Stop loading progress circle
                                    loadingProgressBar.setVisibility(View.GONE);

                                    Boolean exists = false;

                                    // Go through each document and compare the dates
                                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {

                                        // First check that the document belongs to the user
                                        if (document.get("email").equals(email)) {
                                            final DocumentReference friendReference = document.getReference();

                                            exists = true;

                                            User friend = document.toObject(User.class);

                                            final ImageView friendImageView = addFriendView.findViewById(R.id.add_friend_profile_iv);
                                            final TextView friendNameTextView= addFriendView.findViewById(R.id.add_friend_friendName_tv);
                                            final Button addFriendBtn= addFriendView.findViewById(R.id.add_friend_add_btn);

                                            //load profile picture for friend
                                            Glide.with(getContext())
                                                    .load(friend.getProfileImageURI())
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .placeholder(R.drawable.ic_profile)
                                                    .into(friendImageView);
                                            friendImageView.setVisibility(View.VISIBLE);

                                            //load username for friend
                                            friendNameTextView.setText(friend.getUsername());
                                            friendNameTextView.setVisibility(View.VISIBLE);

                                           // setupAddFriendButton(addFriendView,friend.getEmail());
                                            addFriendBtn.setOnClickListener(view1 -> {
                                                if(MainActivity.user.getFriends().contains(friendReference)){
                                                    // Show error to user
                                                    errorTextView.setVisibility(View.VISIBLE);
                                                    errorTextView.setText("Friend already exists");
                                                }else{
                                                    MainActivity.user.getFriends().add(friendReference);
                                                    Firestore.updateUser();
                                                    ((FriendsAdapter) friendsAdapter).add(friend);
                                                    Toast.makeText(getContext(), "Successfully Add Friend", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            addFriendBtn.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    if(!exists){
                                        errorTextView.setVisibility(View.VISIBLE);
                                        errorTextView.setText("Cannot find user with email entered");
                                    }
                                }
                            });
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
     * Configure the recycler view
     *
     * @param RV current recycler view needed
     */
    public void configureRecyclerViews(RecyclerView RV ) {

        // Improves performance because content does not change size
        RV.setHasFixedSize(true);

        // Only load 10 tours before loading more
        RV.setItemViewCacheSize(10);

        // Enable drawing cache
        RV.setDrawingCacheEnabled(true);
        RV.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // User linear layout manager
        RecyclerView.LayoutManager friendsLayoutManager = new LinearLayoutManager(getContext());
        RV.setLayoutManager(friendsLayoutManager);

        // Get all friends
        fetchUsersAsync();

        // Specify an adapter
        friendsAdapter = new FriendsAdapter(getContext());
        RV.setAdapter(friendsAdapter);

        // Stop showing progressBar when items are loaded
        RV
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(() -> ((FriendsAdapter) friendsAdapter).stopLoading());


    }

    /**
     * Configure the swipe down to refresh function of our recycler view
     *
     * @param view current view
     */
    public void configureSwipeRefreshLayouts(View view) {

        // Future
        friendsSwipeRefreshLayout = view.findViewById(R.id.add_friend_my_friends_srl);
        friendsSwipeRefreshLayout.setOnRefreshListener(() -> fetchUsersAsync());

    }

    /**
     * Enables the click listener for each item in our recycler view
     */
    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(friendsRecyclerView, R.layout.item_friend)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {


                        // Reference to the current tour selected
                        User friend = friendsAdapter.getData(position);

                        // Add the selected friend to the view model so we can access the friends info inside the fragment
                        friendViewModel.setSelectedFriend(friend);

                        // Display the friend selected
                        final FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                        ft.replace(R.id.nav_host_fragment, new FriendProfileFragment(), "FriendProfileFragment");
                        ft.addToBackStack("FriendProfileFragment").commit();


                    }
                });
    }


    /**
     * Retrieve all friends belonging to this user
     *
     */
    private void fetchUsersAsync() {

        // Get instance of firestore
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Setup collection reference
        CollectionReference usersCollection = db.collection("Users");

        // Pull out the UID's of each friend that belongs to this user
        List<String> usersUIDs = new ArrayList<>();
        if (!MainActivity.user.getFriends().isEmpty()) {
            for (DocumentReference documentReference : MainActivity.user.getFriends()) {
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

                        // Final list of tours for this category
                        List<User> usersFriends = new ArrayList<>();

                        // Go through each document and compare the dates
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {

                            // First check that the document belongs to the user
                            if (usersUIDs.contains(document.getId())) {

                                usersFriends.add(document.toObject(User.class));

                            }
                        }
                        ((FriendsAdapter) friendsAdapter).clear();
                        ((FriendsAdapter) friendsAdapter).addAll(usersFriends);

                        friendsSwipeRefreshLayout.setRefreshing(false);

                    }
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(),
                "clicked on " , Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
