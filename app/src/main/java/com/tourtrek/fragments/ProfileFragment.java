package com.tourtrek.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tourtrek.R;
import com.tourtrek.activities.MainActivity;
import com.tourtrek.data.User;
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

        ((MainActivity)requireActivity()).disableTabs();

        View profileFragmentView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Get instance of Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Display login screen if no user was previous logged in
        if (mAuth.getCurrentUser() == null || MainActivity.user == null) {
            NavHostFragment.findNavController(this).navigate(R.id.navigation_login);
            return profileFragmentView;
        }


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

        // Setup handler for settings button
        setupSettingsButtonHandler(profileFragmentView);

        // Setup handler for friends button
        setupAddFriendButtonHandler(profileFragmentView);

        ((MainActivity)requireActivity()).enableTabs();

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

    /**
     * Setup listener for add_friend button
     *
     * @param view current view
     */
    public void setupAddFriendButtonHandler(final View view) {

        Button addFriendButton = view.findViewById(R.id.profile_friend_btn);

        addFriendButton.setOnClickListener(v -> {

            final FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, new FriendFragment(), "AddFriendFragment");
            ft.addToBackStack("AddFriendFragment").commit();
        });
    }

    /**
     * Setup listener for settings button
     *
     * @param view current view
     */
    public void setupSettingsButtonHandler(final View view) {

        Button settingsButton = view.findViewById(R.id.profile_settings_btn);

        settingsButton.setOnClickListener(v -> {
            final FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, new SettingsFragment(), "SettingsFragment");
            ft.addToBackStack("SettingsFragment").commit();
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

    private void updatePassword(String oldPassword, String newPassword, TextView errorTextView, AlertDialog dialog){
        FirebaseUser user = mAuth.getCurrentUser();

        //ReAuthenticate the user
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity(),
                                                "Password changed successfully", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        errorTextView.setVisibility(View.VISIBLE);
                                        errorTextView.setText("" + e.getMessage());
                                        Toast.makeText(getActivity(),
                                                "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Enter correct password", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showChangePasswordDialog(){

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_change_password, null);
        //Get elements
        EditText passwordEt0 = view.findViewById(R.id.change_password_current_password_et);
        EditText passwordEt1 = view.findViewById(R.id.change_password_new_password_et);
        EditText passwordEt2 = view.findViewById(R.id.change_password_confirm_new_password_et);
        Button updatePasswordButton = view.findViewById(R.id.change_password_update_password_btn);
        TextView errorTextView = view.findViewById(R.id.change_password_error_tv);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password0 = passwordEt0.getText().toString();
                String password1 = passwordEt1.getText().toString();
                String password2 = passwordEt2.getText().toString();

                //validate the password
                if (password1 == null || password2 == null) {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("Password fields are empty");
                } else if (!password1.equals(password2)) {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("Passwords do not match");
                } else if (password1.equals(password2) && password1.length() < 6) {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("New passwords should be at least 6 characters");
                } else {
                    updatePassword(password0,password1,errorTextView,dialog);
                }

            }
        });
    }
}