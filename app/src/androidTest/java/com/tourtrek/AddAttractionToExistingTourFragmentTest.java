package com.tourtrek;

import android.util.Log;

import com.tourtrek.activities.MainActivity;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.commons.text.TextRandomProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.tourtrek.EspressoExtensions.waitId;
import static org.apache.commons.text.RandomStringGenerator.*;


/*
This test file assumes that AddAttractionFragment is reached through selecting a user's tour through the personal tour tab
 */
public class AddAttractionToExistingTourFragmentTest {

    private RandomStringGenerator stringGenerator = new RandomStringGenerator.Builder().withinRange('a','z').build();
    public static final String TAG = "AddAttractionFragment";
    private ActivityScenario mainActivityScenario;

    @Rule
    public final ActivityScenarioRule<MainActivity> mainActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {

        mainActivityScenario = mainActivityScenarioRule.getScenario();

        // log out of any current account, log into the test account, navigate to the personal tours tab, and select the first tour in the future tours section
        try {
            onView(isRoot()).perform(waitId(R.id.navigation_profile, TimeUnit.SECONDS.toMillis(15)));
            onView(withId(R.id.navigation_profile)).perform(click());
            onView(withId(R.id.profile_logout_btn)).perform(click());
        } catch (Exception NoMatchingViewException) {
            Log.w(TAG, "Not logged in");
        } finally {
            onView(withId(R.id.navigation_tours)).perform(click());
            onView(withId(R.id.login_email_et)).perform(typeText("jrawlins@wisc.edu"), closeSoftKeyboard());
            onView(withId(R.id.login_password_et)).perform(typeText("123456"), closeSoftKeyboard());
            onView(withId(R.id.login_login_btn)).perform(click());
            onView(isRoot()).perform(waitId(R.id.personal_future_tours_title_btn, TimeUnit.SECONDS.toMillis(15)));
            onView(withId(R.id.personal_future_tours_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            //onView(withId(R.id.personal_future_tours_rv)).perform(waitId(R.id.edit_tour_add_attraction_btn, TimeUnit.SECONDS.toMillis(5)));
            onView(withId(R.id.edit_tour_add_attraction_btn)).perform(scrollTo());
            onView(withId(R.id.edit_tour_add_attraction_btn)).perform(click());
        }
//        Random rand = new Random();


    }

    /**
     * An error message should display when the user inputs no name, but every other field
     */
    @Test
    public void noAttractionName() {
        // populate edit text fields
        onView(withId(R.id.attraction_name_et)).perform(typeText(""));
        onView(withId(R.id.attraction_location_et)).perform(typeText("330 N. Orchard St., Madison, WI, USA"), closeSoftKeyboard());
        onView(withId(R.id.attraction_cost_et)).perform(typeText("0"), closeSoftKeyboard());
        onView(withId(R.id.attraction_time_start_et)).perform(scrollTo());
        onView(withId(R.id.attraction_time_start_et)).perform(typeText("11-11-2019T08:10"), closeSoftKeyboard());
        onView(withId(R.id.attraction_time_end_et)).perform(typeText("11-11-2019T10:10"), closeSoftKeyboard());

        // scroll to the "add attraction" button and click it
        onView(withId(R.id.attraction_add_btn)).perform(scrollTo());
        onView(withId(R.id.attraction_add_btn)).perform(click());

        // final check to determine pass or fail
        onView(withId(R.id.attraction_error_tv)).check(matches(withText("Enter at least name, location, and start and end time information in the indicated formats")));
    }

    // TODO an error message should appear when the user inputs a name in the wrong format

    /**
     * An error message should appear when the user inputs no location, but every other field
     */
    @Test
    public void noLocation() {
        // populate edit text fields
        onView(withId(R.id.attraction_name_et)).perform(typeText("Wisconsin Institute for Discovery"));
        onView(withId(R.id.attraction_location_et)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.attraction_cost_et)).perform(typeText("0"), closeSoftKeyboard());
        onView(withId(R.id.attraction_time_start_et)).perform(scrollTo());
        onView(withId(R.id.attraction_time_start_et)).perform(typeText("11-11-2019T08:10"), closeSoftKeyboard());
        onView(withId(R.id.attraction_time_end_et)).perform(typeText("11-11-2019T10:10"), closeSoftKeyboard());

        // scroll to the "add attraction" button and click it
        onView(withId(R.id.attraction_add_btn)).perform(scrollTo());
        onView(withId(R.id.attraction_add_btn)).perform(click());

        // final check to determine pass or fail
        onView(withId(R.id.attraction_error_tv)).check(matches(withText("Enter at least name, location, and start and end time information in the indicated formats")));
    }

    // TODO an error message should appear when the user inputs a location in the wrong format

    /**
     * An error message should appear when the user inputs no start time, but every other field
     */
    @Test
    public void noStartTime() {
        // populate edit text fields
        onView(withId(R.id.attraction_name_et)).perform(typeText("Wisconsin Institute for Discovery"));
        onView(withId(R.id.attraction_location_et)).perform(typeText("330 N. Orchard St., Madison, WI, USA"), closeSoftKeyboard());
        onView(withId(R.id.attraction_cost_et)).perform(typeText("0"), closeSoftKeyboard());
        onView(withId(R.id.attraction_time_start_et)).perform(scrollTo());
        onView(withId(R.id.attraction_time_start_et)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.attraction_time_end_et)).perform(typeText("11-11-2019T10:10"), closeSoftKeyboard());

        // scroll to the "add attraction" button and click it
        onView(withId(R.id.attraction_add_btn)).perform(scrollTo());
        onView(withId(R.id.attraction_add_btn)).perform(click());

        // final check to determine pass or fail
        onView(withId(R.id.attraction_error_tv)).check(matches(withText("Enter at least name, location, and start and end time information in the indicated formats")));
    }

    // TODO an error message should appear when the user inputs a start time in the wrong format

    /**
     * An error message should appear when the user inputs no end time, but every other field
     */
    @Test
    public void noEndTime() {
        // populate edit text fields
        onView(withId(R.id.attraction_name_et)).perform(typeText("Wisconsin Institute for Discovery"));
        onView(withId(R.id.attraction_location_et)).perform(typeText("330 N. Orchard St., Madison, WI, USA"), closeSoftKeyboard());
        onView(withId(R.id.attraction_cost_et)).perform(typeText("0"), closeSoftKeyboard());
        onView(withId(R.id.attraction_time_start_et)).perform(scrollTo());
        onView(withId(R.id.attraction_time_start_et)).perform(typeText("11-11-2019T08:10"), closeSoftKeyboard());
        onView(withId(R.id.attraction_time_end_et)).perform(typeText(""), closeSoftKeyboard());

        // scroll to the "add attraction" button and click it
        onView(withId(R.id.attraction_add_btn)).perform(scrollTo());
        onView(withId(R.id.attraction_add_btn)).perform(click());

        // final check to determine pass or fail
        onView(withId(R.id.attraction_error_tv)).check(matches(withText("Enter at least name, location, and start and end time information in the indicated formats")));
    }

    // TODO an error message should appear when the user inputs an end time in the wrong format
    // there should be no spaces, periods, nor characters other than 'T"

    /**
     * Clicking the "add attraction" button, assuming that name, location, and time fields have been properly entered,
     * should take the user back to the edit tour screen
     */
    @Test
    public void backToEditTour() {
        // populate edit text fields
        onView(withId(R.id.attraction_name_et)).perform(typeText("Wisconsin Institute for Discovery"));
        onView(withId(R.id.attraction_location_et)).perform(typeText("330 N. Orchard St., Madison, WI, USA"), closeSoftKeyboard());
        onView(withId(R.id.attraction_cost_et)).perform(typeText("0"), closeSoftKeyboard());
        onView(withId(R.id.attraction_time_start_et)).perform(scrollTo());
        onView(withId(R.id.attraction_time_start_et)).perform(typeText("11-11-2019T08:10"), closeSoftKeyboard());
        onView(withId(R.id.attraction_time_end_et)).perform(typeText("11-11-2019T10:10"), closeSoftKeyboard());

        // scroll to the "add attraction" button and click it
        onView(withId(R.id.attraction_add_btn)).perform(scrollTo());
        onView(withId(R.id.attraction_add_btn)).perform(click());

        // final check to determine pass or fail
        // this check will only pass if we have successfully returned to the edit tour page
        onView(withId(R.id.edit_tour_update_btn)).check(matches(withText("Update Tour")));
    }
//
//    /**
//     * Duplicate name
//     */
//    @Test public void duplicateName(){
//
//        onView(withId(R.id.attraction_name_et)).perform(typeText("WID"));
//        onView(withId(R.id.attraction_location_et)).perform(typeText("331 N. Orchard St., Madison, WI, USA"), closeSoftKeyboard());
//        onView(withId(R.id.attraction_cost_et)).perform(typeText("1"), closeSoftKeyboard());
//        onView(withId(R.id.attraction_time_start_et)).perform(scrollTo());
//        onView(withId(R.id.attraction_time_start_et)).perform(typeText("11-11-2019T08:10"), closeSoftKeyboard());
//        onView(withId(R.id.attraction_time_end_et)).perform(typeText("12-11-2019T10:10"), closeSoftKeyboard());
//
//        // scroll to the "add attraction" button and click it
//        onView(withId(R.id.attraction_add_btn)).perform(scrollTo());
//        onView(withId(R.id.attraction_add_btn)).perform(click());
//
//        setup();
//
//        onView(withId(R.id.attraction_name_et)).perform(typeText("WID"));
//        onView(withId(R.id.attraction_location_et)).perform(typeText("330 N. Orchard St., Madison, WI, USA"), closeSoftKeyboard());
//        onView(withId(R.id.attraction_cost_et)).perform(typeText("0"), closeSoftKeyboard());
//        onView(withId(R.id.attraction_time_start_et)).perform(scrollTo());
//        onView(withId(R.id.attraction_time_start_et)).perform(typeText("11-11-2019T08:10"), closeSoftKeyboard());
//        onView(withId(R.id.attraction_time_end_et)).perform(typeText("11-11-2019T10:10"), closeSoftKeyboard());
//
//        // scroll to the "add attraction" button and click it
//        onView(withId(R.id.attraction_add_btn)).perform(scrollTo());
//        onView(withId(R.id.attraction_add_btn)).perform(click());
//        // check1: duplicate name entered
//        onView(withId(R.id.attraction_error_tv)).check(matches(withText("No duplicate names are allowed.")));
//    }
//
//    /**
//     *  When creating an attraction for an existing tour from the personal tours tab, the attraction is reflected in the database under the
//     *  the attractions collection. This is evidenced by its presence in the attractions recycler view of the tour after refreshing it.
//     *  Until we implement code to avoid duplicate names, it is important that the name of the new attraction is one not already in the database
//     *  for the test to pass. I therefore use a random string.
//     *
//     *  This also tests for prevention of duplicate names
//     *
//     *
//     *  Make it pass when the duplicate error message comes up or a new attraction can be found
//     */
//    @Test
//    public void addedToAttractionRecycler(){
//        // use a random string to avoid duplication
//        // String name = stringGenerator.generate(10);
//         String name = "Nazarick";
//        //UniformRandomProvider rng = RandomSource.create(...);
////        try{
//            // populate edit text fields
//            onView(withId(R.id.attraction_name_et)).perform(typeText(name));
//            onView(withId(R.id.attraction_location_et)).perform(typeText("330 N. Orchard St., Madison, WI, USA"), closeSoftKeyboard());
//            onView(withId(R.id.attraction_cost_et)).perform(typeText("0"), closeSoftKeyboard());
//            onView(withId(R.id.attraction_time_start_et)).perform(scrollTo());
//            onView(withId(R.id.attraction_time_start_et)).perform(typeText("11-11-2019T08:10"), closeSoftKeyboard());
//            onView(withId(R.id.attraction_time_end_et)).perform(typeText("11-11-2019T10:10"), closeSoftKeyboard());
//
//            // scroll to the "add attraction" button and click it
//            onView(withId(R.id.attraction_add_btn)).perform(scrollTo());
//            onView(withId(R.id.attraction_add_btn)).perform(click());
//
//
//            // check1: duplicate name entered
//            onView(withId(R.id.attraction_error_tv)).check(matches(withText("No duplicate names are allowed.")));
//
//            // check2: not a duplicate name
//            // navigate to tour market
//            onView(withId(R.id.navigation_profile)).perform(click());
//            // navigate to your profile
//            onView(withId(R.id.navigation_tours)).perform(click());
//            // select the tour
//            onView(withId(R.id.personal_future_tours_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
//            // if the item can be found in the recycler view and clicked, then the test passes
//            onView(withId(R.id.tour_attractions_rv)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(name)),
//                    scrollTo()));
////        }
////        catch (androidx.test.espresso.PerformException e){
////            // get a new random string if the one used was already in the recycler
////            name = stringGenerator.generate(10);
////            System.out.println(name);
////            System.out.flush();
////            // populate edit text fields
////            setup();
////            onView(withId(R.id.attraction_name_et)).perform(typeText(name));
////            onView(withId(R.id.attraction_location_et)).perform(typeText("330 N. Orchard St., Madison, WI, USA"), closeSoftKeyboard());
////            onView(withId(R.id.attraction_cost_et)).perform(typeText("0"), closeSoftKeyboard());
////            onView(withId(R.id.attraction_time_start_et)).perform(scrollTo());
////            onView(withId(R.id.attraction_time_start_et)).perform(typeText("11-11-2019T08:10"), closeSoftKeyboard());
////            onView(withId(R.id.attraction_time_end_et)).perform(typeText("11-11-2019T10:10"), closeSoftKeyboard());
////
////            // scroll to the "add attraction" button and click it
////            onView(withId(R.id.attraction_add_btn)).perform(scrollTo());
////            onView(withId(R.id.attraction_add_btn)).perform(click());
////
////            // navigate to tour market
////            onView(withId(R.id.navigation_profile)).perform(click());
////            // navigate to your profile
////            onView(withId(R.id.navigation_tours)).perform(click());
////            // select the tour
////            onView(withId(R.id.personal_future_tours_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
////            // if the item can be found in the recycler view and clicked, then the test passes
////            onView(withId(R.id.tour_attractions_rv)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(name)),
////                    scrollTo()));
////        }
//    }
}