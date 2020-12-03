//package com.tourtrek;
//
//import android.util.Log;
//
//import com.tourtrek.activities.MainActivity;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.concurrent.TimeUnit;
//
//import androidx.test.core.app.ActivityScenario;
//import androidx.test.espresso.action.ViewActions;
//import androidx.test.espresso.contrib.RecyclerViewActions;
//import androidx.test.ext.junit.rules.ActivityScenarioRule;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.filters.LargeTest;
//
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
//import static androidx.test.espresso.matcher.ViewMatchers.*;
//import static androidx.test.espresso.Espresso.*;
//import static androidx.test.espresso.action.ViewActions.*;
//import static androidx.test.espresso.assertion.ViewAssertions.*;
//import static com.tourtrek.EspressoExtensions.nestedScrollTo;
//import static com.tourtrek.EspressoExtensions.waitForView;
//import static org.hamcrest.Matchers.not;
//
//@RunWith(AndroidJUnit4.class)
//@LargeTest
//public class EditTourFragmentTest {
//
//    public static final String TAG = "EditTourFragmentTest";
//    private ActivityScenario mainActivityScenario;
//
//    @Rule
//    public final ActivityScenarioRule<MainActivity> mainActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);
//
//
//    @Before
//    public void setup() {
//
//        mainActivityScenario = mainActivityScenarioRule.getScenario();
//
//        // If any user is logged in, make sure to log them out
//        try {
//
//            onView(isRoot()).perform(waitForView(R.id.navigation_profile, TimeUnit.SECONDS.toMillis(15)));
//            onView(withId(R.id.navigation_profile)).perform(click());
//            onView(withId(R.id.profile_logout_btn)).perform(click());
//        } catch (Exception NoMatchingViewException) {
//            Log.w(TAG, "No user is not logged in, continuing test execution");
//        } finally {
//            onView(withId(R.id.navigation_tours)).perform(click());
//        }
//    }


    // TODO Fix
//    @Test
//    public void editSuccessfullyFeedback() throws InterruptedException {
//        onView(withId(R.id.login_email_et)).perform(typeText("cctest@gmail.com"), ViewActions.closeSoftKeyboard());
//        onView(withId(R.id.login_password_et)).perform(typeText("123456"), ViewActions.closeSoftKeyboard());
//        onView(withId(R.id.login_login_btn)).perform(click());
//        onView(isRoot()).perform(waitForView(R.id.personal_future_tours_rv, TimeUnit.SECONDS.toMillis(50)));
//        onView(withId(R.id.navigation_tours)).perform(click());
//        onView(withId(R.id.personal_future_tours_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
//        onView(isRoot()).perform(waitForView(R.id.tour_name_et, TimeUnit.SECONDS.toMillis(50)));
//        onView(withId(R.id.tour_name_et)).perform((typeText("EditTourTestTour")), ViewActions.closeSoftKeyboard());
//        onView(withId(R.id.tour_update_btn)).perform(nestedScrollTo());
//        onView(withId(R.id.tour_update_btn)).perform(click());
//        onView(withText(R.string.Edit_Success_TOAST_STRING)).inRoot(new ToastMatcher())
//                .check(matches(isDisplayed()));
//    }
//}
