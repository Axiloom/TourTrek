package com.tourtrek.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.tourtrek.data.User;
import com.tourtrek.utilities.Firestore;
import com.tourtrek.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class FriendFragment extends Fragment {

    private static final String TAG = "AddFriendFragment";
    private RecyclerView friendsRecyclerView;
    private RecyclerView.Adapter friendsAdapter;
    private SwipeRefreshLayout friendsSwipeRefreshLayout;


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

        // set up the action to carry out via the search button
        setupSearchButton(addFriendView);

        // Configure recycler views
        configureRecyclerViews(addFriendView);
        configureSwipeRefreshLayouts(addFriendView);

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

                                    // Final result for the query
                                    User friend=null;

                                    // Go through each document and compare the dates
                                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {

                                        // First check that the document belongs to the user
                                        if (document.get("email").equals(email)) {
                                            final DocumentReference friendReference =document.getReference();
                                            friend =new User();
                                            friend.setEmail(email);
                                            friend.setUsername((String) document.get("username"));
                                            friend.setProfileImageURI((String) document.get("profileImageURI"));

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
                                                    Toast.makeText(getContext(), "Successfully Add Friend", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            addFriendBtn.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    if(friend==null){
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
     * @param view current view
     */
    public void configureRecyclerViews(View view) {

        // Get our recycler view from the layout
        friendsRecyclerView = view.findViewById(R.id.add_friend_my_friends_rv);

        // Improves performance because content does not change size
        friendsRecyclerView.setHasFixedSize(true);

        // Only load 10 tours before loading more
        friendsRecyclerView.setItemViewCacheSize(10);

        // Enable drawing cache
        friendsRecyclerView.setDrawingCacheEnabled(true);
        friendsRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // User linear layout manager
        RecyclerView.LayoutManager friendsLayoutManager = new LinearLayoutManager(getContext());
        friendsRecyclerView.setLayoutManager(friendsLayoutManager);

        // Get all friends
        fetchUsersAsync();

        // Specify an adapter
        friendsAdapter = new FriendsAdapter(getContext());
        friendsRecyclerView.setAdapter(friendsAdapter);

        // Stop showing progressBar when items are loaded
        friendsRecyclerView
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





}
