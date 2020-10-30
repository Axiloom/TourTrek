package com.tourtrek;

import android.app.Activity;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.tourtrek.activities.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.tourtrek.EspressoExtensions.waitId;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;


public class AddTourFragmentTest {

    public static final String TAG = "AddTourFragmentTest";
    private ActivityScenario mainActivityScenario;

    @Rule
    public final ActivityScenarioRule<MainActivity> mainActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {
        // If any user is logged in, make sure to log them out
        try {
            onView(isRoot()).perform(waitId(R.id.navigation_profile, TimeUnit.SECONDS.toMillis(15)));
            onView(withId(R.id.navigation_profile)).perform(click());
            onView(withId(R.id.profile_logout_btn)).perform(click());
        } catch (Exception NoMatchingViewException) {
            Log.w(TAG, "No user is not logged in, continuing test execution");
        } finally {
            //then log into test profile
            onView(withId(R.id.navigation_tours)).perform(click());
            onView(withId(R.id.login_email_et)).perform(typeText("robert@gmail.com"));
            Espresso.closeSoftKeyboard();
            onView(withId(R.id.login_password_et)).perform(typeText("password"));
            Espresso.closeSoftKeyboard();
            onView(withId(R.id.login_login_btn)).perform(click());
            onView(isRoot()).perform(waitId(R.id.navigation_tours, TimeUnit.SECONDS.toMillis(1000)));
            onView(withId(R.id.navigation_tours)).perform(click());
            onView(isRoot()).perform(waitId(R.id.personal_future_tours_title_btn, TimeUnit.SECONDS.toMillis(1000)));
            onView(withId(R.id.personal_future_tours_title_btn)).perform(click());
        }

    }

//    @Test
//    public void CreateTourWithNoName() {
//        String ToastString = "Not All Fields entered";
//        onView(withId(R.id.edit_tour_2_tour_name_ct)).perform(typeText(""));
//        Espresso.closeSoftKeyboard();
//        onView(withId(R.id.edit_tour_2_tour_location_ct)).perform(typeText("Test"));
//        Espresso.closeSoftKeyboard();
//        onView(withId(R.id.edit_tour_2_length_ct)).perform(typeText("12"));
//        Espresso.closeSoftKeyboard();
//        onView(withId(R.id.edit_tour_2_startDate_ct)).perform(typeText("01-01-2021"));
//        Espresso.closeSoftKeyboard();
//        onView(withId(R.id.edit_tour_2_save_bt)).perform(scrollTo());
//        onView(withId(R.id.edit_tour_2_save_bt)).perform(click());
//        onView(withText("Not All Fields Entered")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
////        ArrayList<Activity> activityList = new ArrayList<>();
////
////        mainActivityScenarioRule.getScenario().onActivity(activity -> {
////            activityList.add(activity);
////        });
////
////        Result onView(withText(R.string.add_tour_toast_message)).inRoot(withDecorView(not(activityList.get(0).getWindow().getDecorView()))).check(matches(isDisplayed()));
////
////        Log.i(TAG, "TEST");
//        //onView(withId(R.id.register_error_tv)).check(matches(withText("Not All Fields entered")));
//
//    }

//    @Test
//    public void CheckIfTourGetsCreated() {
//        onView(withId(R.id.edit_tour_2_tour_name_ct)).perform(typeText("Test"));
//        onView(withId(R.id.edit_tour_2_tour_location_ct)).perform(typeText("Test"));
//        onView(withId(R.id.edit_tour_2_length_ct)).perform(typeText("12"));
//        onView(withId(R.id.edit_tour_2_startDate_ct)).perform(typeText("01-01-2021"));
//        Espresso.closeSoftKeyboard();
//        onView(withId(R.id.edit_tour_2_save_bt)).perform(click());
//        onView(withId(R.id.tour_attractions_rv)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("Test")),
//                scrollTo()));
//
//    }




}
