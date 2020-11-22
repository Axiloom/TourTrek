package com.tourtrek;

import com.tourtrek.activities.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

public class MapsFragmentTest {

    public static final String TAG = "MapsFragmentTest";
    private ActivityScenario mainActivityScenario;

    @Rule
    public final ActivityScenarioRule<MainActivity> mainActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup(){

    }

    @After
    public void tearDown(){

    }

//    @Test
    // TODO - test for the attraction location marker
    // TODO - test for either a starting location marker appearing or the toast saying that user location data could not be found
    // TODO - test for the toast telling the user to tap on a marker

}
