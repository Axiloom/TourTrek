package com.tourtrek;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.DatePicker;
import android.widget.TimePicker;
import com.tourtrek.activities.MainActivity;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import java.util.concurrent.TimeUnit;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.uiautomator.UiObjectNotFoundException;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.tourtrek.EspressoExtensions.nestedScrollTo;
import static com.tourtrek.EspressoExtensions.waitForView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;
import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.allOf;


public class AttractionFragmentTest {

    public static final String TAG = "AttractionFragmentTest";
    private ActivityScenario mainActivityScenario;

    @Rule
    public final ActivityScenarioRule<MainActivity> mainActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() throws InterruptedException, UiObjectNotFoundException {

        mainActivityScenario = mainActivityScenarioRule.getScenario();

        // log out of any current account, log into the test account, navigate to the personal tours tab, and select the first tour in the future tours section
        try {
            onView(isRoot()).perform(waitForView(R.id.navigation_profile, TimeUnit.SECONDS.toMillis(1000)));
            onView(withId(R.id.navigation_profile)).perform(click());
            onView(withId(R.id.profile_logout_btn)).perform(click());
        } catch (Exception NoMatchingViewException) {
            Log.w(TAG, "Not logged in");
        } finally {
            onView(withId(R.id.navigation_tours)).perform(click());
            onView(isRoot()).perform(waitForView(R.id.login_email_et, TimeUnit.SECONDS.toMillis(1000)));
            onView(withId(R.id.login_email_et)).perform(typeText("jrawlins@wisc.edu"), closeSoftKeyboard());
            onView(withId(R.id.login_password_et)).perform(typeText("123456"), closeSoftKeyboard());
            onView(withId(R.id.login_login_btn)).perform(click());
            onView(isRoot()).perform(waitForView(R.id.personal_future_tours_title_btn, TimeUnit.SECONDS.toMillis(1000)));
            sleep(1000); // sleep so that the recycler view to click is loaded
            // onView(withId(R.id.personal_future_tours_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            onView(withId(R.id.personal_future_tours_title_btn)).perform(click());
            onView(isRoot()).perform(waitForView(R.id.tour_name_et, TimeUnit.SECONDS.toMillis(1000)));
            onView(withId(R.id.tour_add_attraction_btn)).perform(nestedScrollTo());
            onView(withId(R.id.tour_add_attraction_btn)).perform(click());
        }
    }

    /**
     * An error message should display when the user inputs no name, but every other field
     * https://stackoverflow.com/questions/44835094/check-datepicker-calendar-value-in-android-espresso-framework#44840330
     * https://stackoverflow.com/questions/43149728/select-date-from-calendar-in-android-espresso/43180527
     */
    @Test
    public void noAttractionNameTest() throws InterruptedException {
        attractionConditionsTest("noAttraction");
        onView(withText("Not all fields entered")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        Espresso.pressBack();
        removeAdded();
    }

    /**
     * An error message should appear when the user inputs no location, but every other field
     */
    @Test
    public void noLocationTest() throws InterruptedException {
        attractionConditionsTest("noLocation");
        onView(withText("Not all fields entered")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        Espresso.pressBack();
        removeAdded();
    }

    @Test
    public void noCostTest() throws InterruptedException {
        attractionConditionsTest("noCost");
        onView(withText("Not all fields entered")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        Espresso.pressBack();
        removeAdded();
    }

    /**
     * An error message should appear when the user inputs no start time, but every other field
     */
    @Test
    public void noStartDateTest() throws InterruptedException {
        attractionConditionsTest("noStartDate");
        onView(withText("Not all fields entered")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        Espresso.pressBack();
        removeAdded();
    }

    /**
     * An error message should appear when the user inputs no location, but every other field
     */
    @Test
    public void noStartTimeTest() throws InterruptedException {
        attractionConditionsTest("noStartTime");
        onView(withText("Not all fields entered")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        Espresso.pressBack();
        removeAdded();
    }

    @Test
    public void noEndDateTest() throws InterruptedException {
        attractionConditionsTest("noEndDate");
        onView(withText("Not all fields entered")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        Espresso.pressBack();
        removeAdded();
    }

    /**
     * An error message should appear when the user inputs no end time, but every other field
     */
    @Test
    public void noEndTimeTest() throws InterruptedException {
        attractionConditionsTest("noEndTime");
        onView(withText("Not all fields entered")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        Espresso.pressBack();
        removeAdded();
    }


    @Test
    public void invalidDatesTest() throws InterruptedException {
        attractionConditionsTest("invalidDates");
        onView(withText("Start dates must be before end dates!")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        Espresso.pressBack();
        removeAdded();
    }

    @Test
    public void noDescriptionTest() throws InterruptedException {
        attractionConditionsTest("noDescription");
        onView(withText("Not all fields entered")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        Espresso.pressBack();
        removeAdded();
    }

    /**
     * test to check the toast for a successful attraction update
     */
    @Test
    public void additionSuccessfulTest() throws InterruptedException {
        attractionConditionsTest("SUCCESSFUL ADDITION");
        onView(withText("Successfully Added Attraction")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        removeAdded();
    }

//    /**
//     * Check that updating or adding an attraction takes you back to the prior tour screen
//     */
//    @Test
//    public void backToEditTourTest() {
//        // this check will only pass if we have successfully returned to the edit tour page
//        attractionConditionsTest("");
//        onView(isRoot()).perform(waitForView(R.id.tour_update_btn, TimeUnit.SECONDS.toMIllis(1000)));
//        onView(withId(R.id.tour_update_btn)).check(matches(withText("Update Tour")));
//    }


//    /**
//     * test to check that an attraction is successfully added to the recycler view of the current tour following addition
//     *https://stackoverflow.com/questions/37736616/espresso-how-to-find-a-specific-item-in-a-recycler-view-order-is-random
//     */
//    @Test
//    public void addedToRecyclerTest() throws InterruptedException {
//        attractionConditionsTest("");
//
////        Espresso.pressBack();
//
//        onView(isRoot()).perform(waitForView(R.id.tour_attractions_rv, TimeUnit.SECONDS.toMillis(100)));
//
//        sleep(1000); // give time for the recycler view to load
//
//        onView(withId(R.id.tour_attractions_rv)).perform(nestedScrollTo());
//        onView(withId(R.id.tour_attractions_rv)).perform(RecyclerViewActions.scrollTo(hasDescendant(withText("Wisconsin Institute for Discovery"))));
//        onView(withId(R.id.tour_attractions_rv)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("Wisconsin Institute for Discovery")), click()));
//        onView(isRoot()).perform(waitForView(R.id.attraction_name_et, TimeUnit.SECONDS.toMillis(100)));
//
////        onView(withId(R.id.attraction_name_et)).check(matches(withText("Wisconsin Institute for Discovery")));
//
//        // delete the tour and attraction
//        Espresso.pressBack();
//        sleep(100);
//        removeAdded();
//
////        // scroll to the "update attraction" button and click it
////        onView(withId(R.id.attraction_delete_btn)).perform(nestedScrollTo());
////        onView(withId(R.id.attraction_delete_btn)).perform(click());
//
//    }

    /**
     * Test for updating an attraction, not making a new one
     */
    @Test
    public void updatedAttractionTest() throws InterruptedException {
        attractionConditionsTest("");

        onView(isRoot()).perform(waitForView(R.id.tour_attractions_rv, TimeUnit.SECONDS.toMillis(500)));

        sleep(500); // give time for the recycler view items to load

        // find the newly made attraction and select it
        onView(withId(R.id.tour_attractions_rv)).perform(nestedScrollTo());
        onView(withId(R.id.tour_attractions_rv)).perform(RecyclerViewActions.scrollTo(hasDescendant(withText("Wisconsin Institute for Discovery"))));
        onView(withId(R.id.tour_attractions_rv)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("Wisconsin Institute for Discovery")), click()));

        // update the attraction name
        onView(isRoot()).perform(waitForView(R.id.attraction_name_et, TimeUnit.SECONDS.toMillis(500)));
        onView(withId(R.id.attraction_name_et)).perform(typeText("New attraction name"), closeSoftKeyboard());

        // scroll to the "update attraction" button and click it
        onView(withId(R.id.attraction_update_btn)).perform(nestedScrollTo());
        onView(withId(R.id.attraction_update_btn)).perform(click());

        // check for the proper toast message
        sleep(500);
        onView(withText("Successfully Updated Attraction")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));

        // delete the tour and attraction
        Espresso.pressBack();
        removeAdded();

//        // scroll to the "update attraction" button and click it
//        onView(withId(R.id.attraction_delete_btn)).perform(nestedScrollTo());
//        onView(withId(R.id.attraction_delete_btn)).perform(click());
    }

    /**
     * Test deletion of an attraction
     */
    @Test
    public void deletionTest() throws InterruptedException {
        attractionConditionsTest("");

        onView(isRoot()).perform(waitForView(R.id.tour_attractions_rv, TimeUnit.SECONDS.toMillis(500)));

        sleep(500); // give time for the recycler view items to load

        // find the newly made attraction and select it
        onView(withId(R.id.tour_attractions_rv)).perform(nestedScrollTo());
        onView(withId(R.id.tour_attractions_rv)).perform(RecyclerViewActions.scrollTo(hasDescendant(withText("Wisconsin Institute for Discovery"))));
        onView(withId(R.id.tour_attractions_rv)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("Wisconsin Institute for Discovery")), click()));

        // update the attraction name
        onView(isRoot()).perform(waitForView(R.id.attraction_name_et, TimeUnit.SECONDS.toMillis(500)));

        // scroll to the "update attraction" button and click it
        onView(withId(R.id.attraction_delete_btn)).perform(nestedScrollTo());
        onView(withId(R.id.attraction_delete_btn)).perform(click());

        // check for the proper toast message
        sleep(500);
        onView(withText("Attraction Deleted")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));

        // delete the tour and attraction
        removeAdded();
    }

    /**
     * Helper method to minimize duplicate code
     * Possible conditions include:
     * noAttraction
     * noLocation
     * noCost
     * noDescription
     * noStartDate
     * noStartTime
     * noEndDate
     * noEndTime
     * invalidDates
     * @param condition
     */
    private void attractionConditionsTest(String condition) throws InterruptedException {
        // attraction name
        if (condition.equals("noAttraction")){ onView(withId(R.id.attraction_name_et)).perform(typeText(""), closeSoftKeyboard()); }
        else { onView(withId(R.id.attraction_name_et)).perform(typeText("Some attraction"), closeSoftKeyboard()); }

        // location
        onView(withId(R.id.attraction_location_et)).perform(nestedScrollTo());
//        if (condition.equals("noLocation")){onView(withId(R.id.attraction_location_et)).perform(typeText(""), closeSoftKeyboard()); }
//        else {onView(withId(R.id.attraction_location_et)).perform(typeText("330 N. Orchard St., Madison, WI, USA"), closeSoftKeyboard()); }
        if (condition.equals("noLocation") || condition.equals("noAttraction") ){}
        else {
            onView(withId(R.id.attraction_search_ib)).perform(click());
            sleep(1000);
            onView(withId(R.id.places_autocomplete_search_bar)).perform(typeText("Wisconsin Institute for Discovery"));
            sleep(1000);
            ViewInteraction recyclerView = onView(
                    allOf(withId(R.id.places_autocomplete_list),
                            childAtPosition(
                                    withId(R.id.places_autocomplete_content),
                                    3)));
            recyclerView.perform(actionOnItemAtPosition(0, click()));
        }

        // cost
        onView(isRoot()).perform(waitForView(R.id.attraction_name_et, TimeUnit.SECONDS.toMillis(30)));
        onView(withId(R.id.attraction_cost_et)).perform(nestedScrollTo());
        if (condition.equals("noCost")){onView(withId(R.id.attraction_cost_et)).perform(typeText(""), closeSoftKeyboard());}
        else {onView(withId(R.id.attraction_cost_et)).perform(typeText("0"), closeSoftKeyboard());}

        // description
        onView(withId(R.id.attraction_description_et)).perform(nestedScrollTo());
        if (condition.equals("noDescription")){onView(withId(R.id.attraction_description_et)).perform(typeText(""), closeSoftKeyboard());}
        else {onView(withId(R.id.attraction_description_et)).perform(typeText("Random sample description"), closeSoftKeyboard());}

        // set the start date
        onView(withId(R.id.attraction_start_date_btn)).perform(nestedScrollTo());
        if (!condition.equals("noStartDate")){
            onView(withId(R.id.attraction_start_date_btn)).perform(click());
            onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 11, 10));
            onView(withId(android.R.id.button1)).perform(click());
        }

        // set the start time
        onView(withId(R.id.attraction_start_time_btn)).perform(nestedScrollTo());
        if (!condition.equals("noStartTime")){
            onView(withId(R.id.attraction_start_time_btn)).perform(click());
            onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(6, 30));
            onView(withId(android.R.id.button1)).perform(click());
        }

        // set end date
        onView(withId(R.id.attraction_end_date_btn)).perform(nestedScrollTo());
        if (condition.equals("invalidDates")){
            onView(withId(R.id.attraction_end_date_btn)).perform(click());
            onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 11, 9));
            onView(withId(android.R.id.button1)).perform(click());
        }
        else if (!condition.equals("noEndDate")){
            onView(withId(R.id.attraction_end_date_btn)).perform(click());
            onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 11, 12));
            onView(withId(android.R.id.button1)).perform(click());
        }

        // set the end time
        onView(withId(R.id.attraction_end_time_btn)).perform(nestedScrollTo());
        if (!condition.equals("noEndTime")){
            onView(withId(R.id.attraction_end_time_btn)).perform(click());
            onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(8, 30));
            onView(withId(android.R.id.button1)).perform(click());
        }

        // scroll to the "add attraction" button and click it
        onView(withId(R.id.attraction_update_btn)).perform(nestedScrollTo());
        onView(withId(R.id.attraction_update_btn)).perform(click());
        sleep(1000);
    }

    /**
     *
     */
    private void removeAdded() throws InterruptedException {
        // find the newly made attraction and select it
        onView(isRoot()).perform(waitForView(R.id.tour_attractions_rv, TimeUnit.SECONDS.toMillis(30)));
        sleep(1000);
        onView(withId(R.id.tour_delete_btn)).perform(nestedScrollTo());
        onView(withId(R.id.tour_delete_btn)).perform(click());
    }

    // TODO - test for the attraction location marker
    // TODO - test for either a starting location marker appearing or the toast saying that user location data could not be found
    // TODO - test for the toast telling the user to tap on a marker


    /**
     * For dates and times
     * @param parentMatcher
     * @param position
     * @return
     */
    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}