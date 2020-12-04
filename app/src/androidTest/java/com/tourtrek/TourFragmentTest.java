package com.tourtrek;

import android.content.Intent;
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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.uiautomator.UiObjectNotFoundException;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.tourtrek.EspressoExtensions.nestedScrollTo;
import static com.tourtrek.EspressoExtensions.waitForView;
import static java.lang.Thread.sleep;


public class TourFragmentTest {

    public static final String TAG = "TourFragmentTest";
    private ActivityScenario mainActivityScenario;

    @Rule
    public final ActivityScenarioRule<MainActivity> mainActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() throws InterruptedException, UiObjectNotFoundException {

        mainActivityScenario = mainActivityScenarioRule.getScenario();

        // log out of any current account, log into the test account, navigate to the personal tours tab, and select the first tour in the future tours section
        try {
            onView(isRoot()).perform(waitForView(R.id.navigation_profile, TimeUnit.SECONDS.toMillis(20)));
            onView(withId(R.id.navigation_profile)).perform(click());
            onView(withId(R.id.profile_logout_btn)).perform(click());
        } catch (Exception NoMatchingViewException) {
            Log.w(TAG, "Not logged in");
        } finally {
            onView(withId(R.id.navigation_tours)).perform(click());
            onView(isRoot()).perform(waitForView(R.id.login_email_et, TimeUnit.SECONDS.toMillis(20)));
            onView(withId(R.id.login_email_et)).perform(replaceText("cctest@gmail.com"), closeSoftKeyboard());
            onView(withId(R.id.login_password_et)).perform(replaceText("123456"), closeSoftKeyboard());
            onView(withId(R.id.login_login_btn)).perform(click());
            onView(isRoot()).perform(waitForView(R.id.personal_future_tours_title_btn, TimeUnit.SECONDS.toMillis(20)));
            onView(withId(R.id.personal_future_tours_title_btn)).perform(click());
        }
    }

    /**
     * An error message should display when the user inputs no name, but every other field
     * https://stackoverflow.com/questions/44835094/check-datepicker-calendar-value-in-android-espresso-framework#44840330
     * https://stackoverflow.com/questions/43149728/select-date-from-calendar-in-android-espresso/43180527
     */
    @Test
    public void noTourNameTest() throws InterruptedException {
        tourConditionsTest("noTourName");
        onView(withText("Not all fields entered")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));

        tourConditionsTest("noLocation");
        onView(withText("Not all fields entered")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));

        tourConditionsTest("noCost");
        onView(withText("Not all fields entered")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));

        removeAdded();
    }


    /**
     * An error message should appear when the user inputs no start time, but every other field
     */
    @Test
    public void noStartDateTest() throws InterruptedException {
        tourConditionsTest("noStartDate");
        onView(withText("Not all fields entered")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        removeAdded();
    }


    @Test
    public void noEndDateTest() throws InterruptedException {
        tourConditionsTest("noEndDate");
        onView(withText("Not all fields entered")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        removeAdded();
    }


    @Test
    public void invalidTimeTest() throws InterruptedException {
        tourConditionsTest("invalidTime");
        onView(withText("Start dates must be before end dates!")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        removeAdded();
    }


    /**
     * test to check the toast for a successful attraction update
     */
    @Test
    public void additionSuccessfulTest() throws InterruptedException {
        tourConditionsTest("SUCCESSFUL ADDITION");
        onView(withText("Successfully Updated Tour")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));

        sleep(1000); // give time for the recycler view items to load

        // find the newly made attraction and select it
        onView(withId(R.id.personal_past_tours_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));

        navigationWithoutAttractions(); // test navigation

        removeAdded();

    }


//    /**
//     * Test deletion of a tour
//     */
//    @Test
//    public void deletionTest() throws InterruptedException {
//        tourConditionsTest("deletion");
//
//        onView(isRoot()).perform(waitForView(R.id.personal_past_tours_rv, TimeUnit.SECONDS.toMillis(100)));
//
//        sleep(1000); // give time for the recycler view items to load
//
//        // find the newly made attraction and select it
//        onView(withId(R.id.personal_past_tours_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
//        onView(isRoot()).perform(waitForView(R.id.tour_name_et, TimeUnit.SECONDS.toMillis(1000)));
//        onView(withId(R.id.tour_delete_btn)).perform(nestedScrollTo());
//        onView(withId(R.id.tour_delete_btn)).perform(click());
//
//
//        sleep(1000);
//        // check for the proper toast message
//        onView(withText("Tour removed")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
//    }

//    /**
//     * Test deletion of a tour
//     */
//    @Test
//    public void deletionPublicTourTest() throws InterruptedException {
//        tourConditionsTest("deletionPublic");
//
//        onView(isRoot()).perform(waitForView(R.id.personal_past_tours_rv, TimeUnit.SECONDS.toMillis(20)));
//
//        sleep(1000); // give time for the recycler view items to load
//
//        // find the newly made attraction and select it
//        onView(withId(R.id.personal_past_tours_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
//        onView(isRoot()).perform(waitForView(R.id.tour_public_cb, TimeUnit.SECONDS.toMillis(20)));
//        onView(withId(R.id.tour_delete_btn)).perform(nestedScrollTo());
//        onView(withId(R.id.tour_delete_btn)).perform(click());
//        sleep(1000);
//
//        // check for the proper toast message
//        onView(withText("You cannot delete a public tour!")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
//
//        onView(withId(R.id.navigation_tours)).perform(click());
//
//        onView(isRoot()).perform(waitForView(R.id.personal_past_tours_rv, TimeUnit.SECONDS.toMillis(20)));
//
//        sleep(1000); // give time for the recycler view items to load
//
//        // find the newly made attraction and select it
//        onView(withId(R.id.personal_past_tours_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
//        onView(isRoot()).perform(waitForView(R.id.tour_public_cb, TimeUnit.SECONDS.toMillis(20)));
//        onView(withId(R.id.tour_public_cb)).perform(nestedScrollTo());
//        onView(withId(R.id.tour_public_cb)).perform(click());
//        onView(withId(R.id.tour_update_btn)).perform(nestedScrollTo());
//        onView(withId(R.id.tour_update_btn)).perform(click());
//
//        sleep(1000); // give time for the recycler view items to load
//
//        // find the newly made attraction and select it
//        onView(withId(R.id.personal_past_tours_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
//        onView(isRoot()).perform(waitForView(R.id.tour_delete_btn, TimeUnit.SECONDS.toMillis(20)));
//        onView(withId(R.id.tour_delete_btn)).perform(nestedScrollTo());
//        onView(withId(R.id.tour_delete_btn)).perform(click());
//    }


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
     * invalidTime
     * deletionPublic
     * @param condition
     */
    private void tourConditionsTest(String condition){
        // attraction name
        onView(withId(R.id.tour_name_et)).perform(nestedScrollTo());
        if (condition.equals("noTourName")){ onView(withId(R.id.tour_name_et)).perform(replaceText(""), closeSoftKeyboard()); }
        else { onView(withId(R.id.tour_name_et)).perform(replaceText("Some tour"), closeSoftKeyboard()); }

        // location
        onView(withId(R.id.tour_location_et)).perform(nestedScrollTo());
        if (condition.equals("noLocation")){onView(withId(R.id.tour_location_et)).perform(replaceText(""), closeSoftKeyboard()); }
        else {onView(withId(R.id.tour_location_et)).perform(replaceText("330 N. Orchard St., Madison, WI, USA"), closeSoftKeyboard()); }

        // cost
        onView(withId(R.id.tour_cost_et)).perform(nestedScrollTo());
        if (condition.equals("noCost")){onView(withId(R.id.tour_cost_et)).perform(replaceText(""), closeSoftKeyboard());}
        else {onView(withId(R.id.tour_cost_et)).perform(replaceText("0"), closeSoftKeyboard());}


        // set the start date
        onView(withId(R.id.tour_start_date_btn)).perform(nestedScrollTo());
        if (!condition.equals("noStartDate")){
            onView(withId(R.id.tour_start_date_btn)).perform(click());
            onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 11, 10));
            onView(withId(android.R.id.button1)).perform(click());
        }


        // set end date
        onView(withId(R.id.tour_end_date_btn)).perform(nestedScrollTo());
        if (condition.equals("invalidTime")){
            onView(withId(R.id.tour_end_date_btn)).perform(click());
            onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 11, 9));
            onView(withId(android.R.id.button1)).perform(click());
        }
        else if (!condition.equals("noEndDate")){
            onView(withId(R.id.tour_end_date_btn)).perform(click());
            onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 11, 12));
            onView(withId(android.R.id.button1)).perform(click());
        }

        if (condition.equals("deletionPublic")){
            onView(withId(R.id.tour_public_cb)).perform(nestedScrollTo());
            onView(withId(R.id.tour_public_cb)).perform(click());
        }


        // scroll to the "add attraction" button and click it
        onView(withId(R.id.tour_update_btn)).perform(nestedScrollTo());
        onView(withId(R.id.tour_update_btn)).perform(click());
    }

    // Generic navigation test
    public void navigationWithoutAttractions() throws InterruptedException {
        onView(isRoot()).perform(waitForView(R.id.tour_name_et, TimeUnit.SECONDS.toMillis(500)));

        sleep(500); // give time for the recycler view items to load

        // map check
        onView(withId(R.id.tour_navigation_btn)).perform(nestedScrollTo(), click());
        sleep(300);
        onView(withText("No were attractions displayed - try adding some!")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        sleep(300);
        Espresso.pressBack();
    }
    /**
     *
     */
    private void removeAdded() throws InterruptedException {
        // find the newly made attraction and select it
        onView(isRoot()).perform(waitForView(R.id.tour_name_et, TimeUnit.SECONDS.toMillis(30)));
        onView(withId(R.id.tour_delete_btn)).perform(nestedScrollTo());
        onView(withId(R.id.tour_delete_btn)).perform(click());
    }
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