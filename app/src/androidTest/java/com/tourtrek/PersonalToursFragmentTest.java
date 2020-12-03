package com.tourtrek;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.DatePicker;
import android.widget.TimePicker;
import com.google.firebase.firestore.DocumentReference;
import com.tourtrek.activities.MainActivity;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.uiautomator.UiObjectNotFoundException;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.tourtrek.EspressoExtensions.nestedScrollTo;
import static com.tourtrek.EspressoExtensions.waitForView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;
import static com.tourtrek.utilities.Firestore.updateUser;
import static java.lang.Thread.sleep;


public class PersonalToursFragmentTest {

    public static final String TAG = "PersonalToursFragmentTest";
    private ActivityScenario mainActivityScenario;

    @Rule
    public final ActivityScenarioRule<MainActivity> mainActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() throws InterruptedException, UiObjectNotFoundException {

        mainActivityScenario = mainActivityScenarioRule.getScenario();

        // log out of any current account, log into the test account, navigate to the personal tours tab, and tap the button for adding a tour
        try {
            onView(isRoot()).perform(waitForView(R.id.navigation_profile, TimeUnit.SECONDS.toMillis(20)));
            onView(withId(R.id.navigation_profile)).perform(click());
            onView(withId(R.id.profile_logout_btn)).perform(click());
        } catch (Exception NoMatchingViewException) {
            Log.w(TAG, "Not logged in");
        } finally {
            onView(isRoot()).perform(waitForView(R.id.navigation_tours, TimeUnit.SECONDS.toMillis(20)));
            onView(withId(R.id.navigation_tours)).perform(click());
            onView(isRoot()).perform(waitForView(R.id.login_email_et, TimeUnit.SECONDS.toMillis(20)));
          
            onView(withId(R.id.login_email_et)).perform(typeText("jrawlins@wisc.edu"), closeSoftKeyboard());
            onView(withId(R.id.login_password_et)).perform(typeText("123456"), closeSoftKeyboard());
            onView(withId(R.id.login_login_btn)).perform(click());
            onView(isRoot()).perform(waitForView(R.id.personal_future_tours_title_btn, TimeUnit.SECONDS.toMillis(20)));
            onView(withId(R.id.personal_future_tours_title_btn)).perform(click());
        }
    }

    /**
     *Create a tour with future start and end dates, then make sure it goes in the future tours bin
     */
    @Test
    public void newFutureTour() throws InterruptedException {
        tourDateConditionsTest("future");

        // wait for the personal tours tab to be visible with updated recycler views
        onView(isRoot()).perform(waitForView(R.id.personal_future_tours_title_btn, TimeUnit.SECONDS.toMillis(20)));
        sleep(1000);

        // check the the tour appears in the right bin
        try{
            onView(withId(R.id.personal_future_tours_rv)).perform(RecyclerViewActions.scrollTo(hasDescendant(withText("future tour"))));
            onView(withId(R.id.personal_future_tours_rv)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("future tour")), click()));
        }
        catch(androidx.test.espresso.PerformException e){
            onView(withId(R.id.personal_future_tours_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        }
        onView(isRoot()).perform(waitForView(R.id.tour_name_et, TimeUnit.SECONDS.toMillis(20)));
        //sleep(1000);

//        onView(withId(R.id.tour_name_et)).check(matches(withText("future tour")));
        onView(withId(R.id.tour_start_date_btn)).perform(nestedScrollTo());
        onView(withId(R.id.tour_start_date_btn)).check(matches(withText("11/10/2100")));
        onView(withId(R.id.tour_end_date_btn)).perform(nestedScrollTo());
        onView(withId(R.id.tour_end_date_btn)).check(matches(withText("11/12/2101")));

        //Espresso.pressBack();
        removeAdded();
    }

    /**
     *Create a tour with current start and end dates, then make sure it goes in the current tours bin
     */
    @Test
    public void newCurrentTour() throws InterruptedException {
        tourDateConditionsTest("current");

        // wait for the personal tours tab to be visible with updated recycler views
        onView(isRoot()).perform(waitForView(R.id.personal_current_tours_rv, TimeUnit.SECONDS.toMillis(20)));
        sleep(1000);

        // check the the tour appears in the right bin
        try{
            onView(withId(R.id.personal_current_tours_rv)).perform(RecyclerViewActions.scrollTo(hasDescendant(withText("current tour"))));
            onView(withId(R.id.personal_current_tours_rv)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("current tour")), click()));
        }
        catch(androidx.test.espresso.PerformException e){
            onView(withId(R.id.personal_current_tours_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        }
        onView(isRoot()).perform(waitForView(R.id.tour_name_et, TimeUnit.SECONDS.toMillis(20)));
        sleep(1000);

//        onView(withId(R.id.tour_name_et)).check(matches(withText("current tour")));
        onView(withId(R.id.tour_start_date_btn)).perform(nestedScrollTo());
        onView(withId(R.id.tour_start_date_btn)).check(matches(withText("11/10/1900")));
        onView(withId(R.id.tour_end_date_btn)).perform(nestedScrollTo());
        onView(withId(R.id.tour_end_date_btn)).check(matches(withText("11/12/2101")));

        //Espresso.pressBack();
        removeAdded();
    }

    /**
     *Create a tour with past start and end dates, then make sure it goes in the past tours bin
     */
    @Test
    public void newPastTour() throws InterruptedException {
        tourDateConditionsTest("past");

        // wait for the personal tours tab to be visible with updated recycler views
        onView(isRoot()).perform(waitForView(R.id.personal_future_tours_title_btn, TimeUnit.SECONDS.toMillis(20)));
        sleep(1000);

        // check the the tour appears in the right bin
        try{
            onView(withId(R.id.personal_past_tours_rv)).perform(RecyclerViewActions.scrollTo(hasDescendant(withText("past tour"))));
            onView(withId(R.id.personal_past_tours_rv)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("past tour")), click()));
        }
        catch(androidx.test.espresso.PerformException e){
            onView(withId(R.id.personal_past_tours_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        }
        onView(isRoot()).perform(waitForView(R.id.tour_end_date_btn, TimeUnit.SECONDS.toMillis(20)));
        sleep(1000);

//        onView(withId(R.id.tour_name_et)).check(matches(withText("past tour")));
        onView(withId(R.id.tour_start_date_btn)).perform(nestedScrollTo());
        onView(withId(R.id.tour_start_date_btn)).check(matches(withText("11/10/1900")));
        onView(withId(R.id.tour_end_date_btn)).perform(nestedScrollTo());
        onView(withId(R.id.tour_end_date_btn)).check(matches(withText("11/12/1901")));

       // Espresso.pressBack();
        removeAdded();
    }

    /**
     * Helper method to minimize duplicate code
     * Possible conditions include:
     * past
     * current
     * future
     * @param condition
     */
    private void tourDateConditionsTest(String condition){
        // name
        onView(isRoot()).perform(waitForView(R.id.tour_name_et, TimeUnit.SECONDS.toMillis(20)));
        onView(withId(R.id.tour_name_et)).perform(typeText(condition + " tour"), closeSoftKeyboard());

        // location
        onView(withId(R.id.tour_location_et)).perform(nestedScrollTo());
        onView(withId(R.id.tour_location_et)).perform(typeText("Madison, WI"), closeSoftKeyboard());

        // cost
        onView(withId(R.id.tour_cost_et)).perform(nestedScrollTo());
        onView(withId(R.id.tour_cost_et)).perform(typeText("0"), closeSoftKeyboard());

        // set the start date
        onView(withId(R.id.tour_start_date_btn)).perform(nestedScrollTo());
        onView(withId(R.id.tour_start_date_btn)).perform(click());
        if (condition.equals("future")){
            onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2100, 11, 10));
        }
        else if (condition.equals("current") || condition.equals("past")){
            onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(1900, 11, 10));
        }
        onView(withId(android.R.id.button1)).perform(click());

        // set end date
        onView(withId(R.id.tour_end_date_btn)).perform(nestedScrollTo());
        onView(withId(R.id.tour_end_date_btn)).perform(click());
        if (condition.equals("future") || condition.equals("current")){
            onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2101, 11, 12));
        }
        else if (condition.equals("past")){
            onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(1901, 11, 12));
        }
        onView(withId(android.R.id.button1)).perform(click());

        // scroll to the "add tour" button and click it
        onView(withId(R.id.tour_update_btn)).perform(nestedScrollTo());
        onView(withId(R.id.tour_update_btn)).perform(click());
    }

    /**
     * Clean up of newly created tours
     * @throws InterruptedException
     */
    private void removeAdded() throws InterruptedException {
        // find the newly made attraction and select it
        onView(isRoot()).perform(waitForView(R.id.tour_attractions_rv, TimeUnit.SECONDS.toMillis(20)));
        sleep(1000);
        onView(withId(R.id.tour_delete_btn)).perform(nestedScrollTo());
        onView(withId(R.id.tour_delete_btn)).perform(click());
    }
}