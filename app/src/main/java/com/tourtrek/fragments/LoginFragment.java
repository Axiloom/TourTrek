package com.tourtrek.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tourtrek.R;
import com.tourtrek.activities.MainActivity;
import com.tourtrek.data.User;
import com.tourtrek.fragments.RegisterFragment;
import com.tourtrek.utilities.Utilities;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private static String TAG = "LoginFragment";
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        setupLoginButton(view);

        setupRegisterButton(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_login, container, false);

    }

    /**
     * Setup login button listener
     *
     * @param view current view
     */
    private void setupLoginButton(View view) {

        Button loginButton = view.findViewById(R.id.login_login_btn);

        loginButton.setOnClickListener(v -> {

            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Close soft keyboard
            Activity currentActivity = requireActivity();
            Utilities.hideKeyboard(currentActivity);

            // Start loading progress circle
            view.findViewById(R.id.login_loading_pb).setVisibility(View.VISIBLE);

            final EditText emailEditText = view.findViewById(R.id.login_email_et);
            final EditText passwordEditText = view.findViewById(R.id.login_password_et);
            final TextView errorTextView = view.findViewById(R.id.login_error_tv);

            final String email = emailEditText.getText().toString();
            final String password = passwordEditText.getText().toString();

            if (emailEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals("")) {
                errorTextView.setVisibility(View.VISIBLE);
                errorTextView.setText("Invalid username or password");

                // Stop loading progress circle
                view.findViewById(R.id.login_loading_pb).setVisibility(View.GONE);
            }
            else {

                ((MainActivity)requireActivity()).disableTabs();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity(), task -> {
                            if (task.isSuccessful()) {

                                Log.d(TAG, "signInWithEmail:success");

                                // Get the users info from the firestore
                                db.collection("Users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                                        .get()
                                        .addOnCompleteListener(task2 -> {

                                            if (task2.isSuccessful()) {

                                                DocumentSnapshot userDocument = task2.getResult();

                                                if (userDocument != null && userDocument.exists()) {

                                                    // Convert the document to its object and save it globally
                                                    MainActivity.user = userDocument.toObject(User.class);

                                                    // Go back to the profile screen
                                                    getParentFragmentManager().popBackStack();

                                                }

                                                // This means the user does not exist in the database
                                                else {
                                                    Log.w(TAG, "firestore:failure - unable to find user in firestore");
                                                }

                                                // Stop showing progress bar
                                                view.findViewById(R.id.login_loading_pb).setVisibility(View.GONE);
                                            }

                                            ((MainActivity)requireActivity()).enableTabs();
                                        });

                            } else {
                                // If sign in fails, display a message to the user.
                                errorTextView.setVisibility(View.VISIBLE);
                                errorTextView.setText(Objects.requireNonNull(task.getException()).getLocalizedMessage());

                                Log.w(TAG, "signInWithEmail:failure - " + task.getException().getMessage());

                                // Stop loading progress circle
                                view.findViewById(R.id.login_loading_pb).setVisibility(View.GONE);

                                ((MainActivity)requireActivity()).enableTabs();
                            }
                        });
            }
        });

    }

    /**
     * Setup register button listener
     *
     * @param view current view
     */
    private void setupRegisterButton(View view) {

        Button registerButton = view.findViewById(R.id.login_register_btn);

        registerButton.setOnClickListener(v -> {
            final FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, new RegisterFragment(), "RegisterFragment");
            ft.addToBackStack("RegisterFragment").commit();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setActionBarTitle("Login");
    }
}