package com.tourtrek.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.rpc.context.AttributeContext;
import com.tourtrek.R;
import com.tourtrek.activities.MainActivity;
import com.tourtrek.data.User;
import com.tourtrek.utilities.TermsAndConditionsDialogFragment;

//extends preferenceFragmentCompact
public class SettingsFragment extends Fragment {
    private FirebaseAuth mAuth;
    private EditText changeName;
    private EditText changeEmail;
    private EditText changePassword1;
    private EditText changePassword2;
    private Button updatePasswordButton;
    private Button cancelUpdatePasswordButton;
    private Button updateUsernameButton;
    private Button cancelUpdateUsernameButton;
    private Button updateEmailButton;
    private Button cancelUpdateEmailButton;
    private boolean nameUpdated = false;
    private boolean EmailUpdated = false;
    private boolean passwordUpdated = false;
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
        View SettingsView = inflater.inflate(R.layout.fragment_settings_screen, container, false);
            return SettingsView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        //setupUpdateSettingsButton(view);

        //create change password button listener
        updatePasswordButton = view.findViewById(R.id.settings_change_password_btn);
        updatePasswordButton.setOnClickListener(v -> {
            showChangePasswordDialog();
        });

        //create change username button listener
        updateUsernameButton = view.findViewById(R.id.settings_change_username_btn);
        updateUsernameButton.setOnClickListener(v -> {
            showChangeUsernameDialog();
        });

        //create change email button listener
        updateEmailButton = view.findViewById(R.id.settings_change_email_btn);
        updateEmailButton.setOnClickListener(v -> {
            showChangeEmailDialog();
        });

        Button privacyPolicyButton = view.findViewById(R.id.settings_privacy_policy_btn);
        privacyPolicyButton.setOnClickListener(v -> {
            DialogFragment dialogFragment = TermsAndConditionsDialogFragment.newInstance("Privacy Policy");
            dialogFragment.show(getParentFragmentManager(), "dialog");
        });

        Button feedbackButton = view.findViewById(R.id.settings_feedback_btn);
        feedbackButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "feedback@tourtrek.com" });
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
            intent.putExtra(Intent.EXTRA_TEXT, "<Enter Feedback Here>");
            startActivity(Intent.createChooser(intent, "Select Email Client"));
        });
    }
    private void showChangeUsernameDialog(){
        //set view to fragment_change_username.xml
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_change_username, null);
        TextView errorTextView = view.findViewById(R.id.change_username_error_tv);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        //get fields
        EditText newUsername = view.findViewById(R.id.change_username_new_username_et);
        Button updateUsernameButton = view.findViewById(R.id.change_username_update_username_btn);
        Button cancelUpdateUsernameButton = view.findViewById(R.id.change_username_cancel_btn);

        //create update username button listener
        updateUsernameButton.setOnClickListener(v -> {
            updateUsername(newUsername.getText().toString(),errorTextView, dialog);
        });
        //create cancel update username button listener
        cancelUpdateUsernameButton.setOnClickListener(v -> {
            dialog.dismiss();
        });


    }

    private void updateUsername(String newUsername, TextView errorTextView, AlertDialog dialog) {


        FirebaseUser user = mAuth.getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        User newUser = new User();
        newUser.setEmail(MainActivity.user.getEmail());
        newUser.setProfileImageURI(MainActivity.user.getProfileImageURI());
        newUser.setTours(MainActivity.user.getTours());
        newUser.setFriends(MainActivity.user.getFriends());
        newUser.setUsername(newUsername);
        db.collection("Users").document(user.getUid()).set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                MainActivity.user.setUsername(newUsername);
                Toast.makeText(getActivity(),
                        "Username changed successfully", Toast.LENGTH_SHORT).show();
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

    private void showChangeEmailDialog(){
        //set view to fragment_change_email.xml
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_change_email, null);
        TextView errorTextView = view.findViewById(R.id.change_email_error_tv);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        //get fields
        EditText newEmail = view.findViewById(R.id.change_email_new_email_et);
        Button updateEmailButton = view.findViewById(R.id.change_email_update_email_btn);
        Button cancelUpdateEmailButton = view.findViewById(R.id.change_email_cancel_btn);

        //create update email button listener
        updateEmailButton.setOnClickListener(v -> {
            if(newEmail.getText().toString().split("@").length == 2 && newEmail.getText().toString().split("@")[1].contains(".") ){
                updateEmail(newEmail.getText().toString(),errorTextView, dialog);
            }
            else{
                Toast.makeText(getActivity(),
                        "New email formatted incorrectly", Toast.LENGTH_SHORT).show();
            }
        });
        //create cancel update email button listener
        cancelUpdateEmailButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

    }

    private void updateEmail(String newEmail, TextView errorTextView, AlertDialog dialog) {


        FirebaseUser user = mAuth.getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        user.updateEmail(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                User newUser = new User();
                newUser.setEmail(newEmail);
                newUser.setProfileImageURI(MainActivity.user.getProfileImageURI());
                newUser.setTours(MainActivity.user.getTours());
                newUser.setFriends(MainActivity.user.getFriends());
                newUser.setUsername(MainActivity.user.getUsername());
                db.collection("Users").document(user.getUid()).set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MainActivity.user.setEmail(newEmail);
                        Toast.makeText(getActivity(),
                                "Email changed successfully", Toast.LENGTH_SHORT).show();
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
                        errorTextView.setVisibility(View.VISIBLE);
                        errorTextView.setText("" + e.getMessage());
                        Toast.makeText(getActivity(),
                                "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        Button cancelUpdatePasswordButton = view.findViewById(R.id.change_password_cancel_btn);

        TextView errorTextView = view.findViewById(R.id.change_password_error_tv);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                errorTextView.setVisibility(View.INVISIBLE);
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

        //create cancel update password button listener
        cancelUpdatePasswordButton.setOnClickListener(v -> {
            dialog.dismiss();
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
}
