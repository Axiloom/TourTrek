package com.tourtrek.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tourtrek.R;
import com.tourtrek.activities.MainActivity;
import com.tourtrek.utilities.Firestore;

import java.util.UUID;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup a callback for the back button being pressed
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Leave empty
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View profileFragmentView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Get instance of Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Display login screen if no user was previous logged in
        if (mAuth.getCurrentUser() == null || MainActivity.user == null) {
            NavHostFragment.findNavController(this).navigate(R.id.navigation_login);
            return profileFragmentView;
        }

        // TODO: This is where we will load the users information into their profile

        // Set the users username on their profile
        TextView usernameTextView = profileFragmentView.findViewById(R.id.profile_username_tv);
        usernameTextView.setText(MainActivity.user.getUsername());

        // Set profile picture
        ImageView profileUserImageView = profileFragmentView.findViewById(R.id.profile_user_iv);

        // If user clicks profile image, they can change it
        profileUserImageView.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            int PICK_IMAGE = 1;
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        });

        Glide.with(this)
                .load(MainActivity.user.getProfileImageURI())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_profile)
                .circleCrop()
                .into(profileUserImageView);

        // Setup handler for logout button
        setupLogoutButtonHandler(profileFragmentView);

        return profileFragmentView;
    }

    /**
     * Setup listener for logout button
     *
     * @param view current view
     */
    public void setupLogoutButtonHandler(final View view) {

        Button logoutButton = view.findViewById(R.id.profile_logout_btn);

        logoutButton.setOnClickListener(v -> {

            // Set the global user to null
            MainActivity.user = null;

            // Sign the user out of mAuth
            mAuth.signOut();

            // Get the current navController
            NavController navController = NavHostFragment.findNavController(ProfileFragment.this);

            navController.navigate(R.id.navigation_login);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if(resultCode == Activity.RESULT_OK) {
            assert imageReturnedIntent != null;
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

        final StorageReference storageReference = storage.getReference().child("ProfilePictures/" + imageUUID);

        final UploadTask uploadTask = storageReference.putFile(selectedImage);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(exception -> Log.e(TAG, "Error adding image: " + imageUUID + " to cloud storage"))
                .addOnSuccessListener(taskSnapshot -> {
                    Log.i(TAG, "Successfully added image: " + imageUUID + " to cloud storage");

                    storage.getReference().child("ProfilePictures/" + imageUUID).getDownloadUrl()
                            .addOnSuccessListener(uri -> {

                                MainActivity.user.setProfileImageURI(uri.toString());

                                Firestore.updateUser();

                                final FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                                ft.replace(R.id.nav_host_fragment, new ProfileFragment(), "ProfileFragment");
                                ft.commit();

                            })
                            .addOnFailureListener(exception -> {
                                Log.e(TAG, "Error retrieving uri for image: " + imageUUID + " in cloud storage, " + exception.getMessage());
                            });
                });
    }

}