package com.tourtrek.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tourtrek.R;
import com.tourtrek.data.User;
import com.tourtrek.fragments.ProfileFragment;
import com.tourtrek.utilities.Firestore;
import com.tourtrek.utilities.PlacesLocal;
import com.tourtrek.utilities.Weather;
import com.tourtrek.viewModels.AttractionViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 4588;
    private FirebaseAuth mAuth;
    public static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        // Initialize Places API
        PlacesLocal.initialize(this);

        // If the user was previously logged in, load their information here
        if (mAuth.getCurrentUser() != null) {

            // Get instance of firestore
            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Get the users info from the firestore
            db.collection("Users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            // Get the result from the db
                            DocumentSnapshot userDocument = task.getResult();

                            // Convert data into user object
                            if (userDocument != null && userDocument.exists()) {
                                MainActivity.user = userDocument.toObject(User.class);
                            }

                            // Set the content view and load
                            setContentView(R.layout.activity_main);

                            BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);

                            // Passing each menu ID as a set of Ids because each
                            // menu should be considered as top level destinations.
                            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                                    R.id.navigation_tour_market,
                                    R.id.navigation_tours,
                                    R.id.navigation_profile)
                                    .build();

                            NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);

                            NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, appBarConfiguration);

                            NavigationUI.setupWithNavController(bottomNavigationView, navController);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Failed to read user from firestore");
                        }
                    });
        }
        else {
            // Set the content view
            setContentView(R.layout.activity_main);

            BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);

            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_tour_market,
                    R.id.navigation_tours,
                    R.id.navigation_profile)
                    .build();

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setTitle(title);
    }

    public void showTimePickerDialog(Button button) {

        final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

                int hour = hourOfDay % 12;

                button.setText(String.format("%02d:%02d %s", hour == 0 ? 12 : hour,
                        minute, hourOfDay < 12 ? "am" : "pm"));

                button.setBackgroundColor(Color.parseColor("#10000000"));

            }
        };

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, timeSetListener, hour, minute, DateFormat.is24HourFormat(this));

        timePickerDialog.show();
    }

    public void showDatePickerDialog(Button button) {

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                String date = (month + 1) + "/" + day + "/" + year;
                button.setText(date);
                button.setBackgroundColor(Color.parseColor("#10000000"));
            }
        };

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);

        datePickerDialog.show();
    }

    public void showDatePickerDialog(Button button, TextView weather, Context context) {

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                String date = (month + 1) + "/" + day + "/" + year;
                button.setText(date);
                button.setBackgroundColor(Color.parseColor("#10000000"));

                AttractionViewModel attractionViewModel = new ViewModelProvider((MainActivity)context).get(AttractionViewModel.class);

                // Wait for the weather api to receive the data
                if (attractionViewModel.getSelectedAttraction().getWeather() != null) {

                    for (Map.Entry<String, String> entry : attractionViewModel.getSelectedAttraction().getWeather().entrySet()) {
                        String aDateString = entry.getKey();

                        java.text.DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");

                        Calendar calendar = Calendar.getInstance();

                        try {
                            Date aDate = formatter.parse(aDateString);
                            calendar.setTime(aDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error converting string date");
                        }

                        String temperature = entry.getValue();

                        int aMonth = calendar.get(Calendar.MONTH);
                        int aDay = calendar.get(Calendar.DAY_OF_MONTH);
                        int aYear = calendar.get(Calendar.YEAR);

                        if (aMonth == month && aDay == day && aYear == year) {
                            weather.setText(String.format("%s℉", temperature));
                            break;
                        }
                        else
                            weather.setText("N/A");

                    }
                }
            }
        };

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);

        datePickerDialog.show();
    }

}