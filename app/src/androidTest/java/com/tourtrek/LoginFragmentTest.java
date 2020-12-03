package com.tourtrek;

import android.util.Log;

import com.tourtrek.activities.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static com.tourtrek.EspressoExtensions.waitForView;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginFragmentTest {

    public static final String TAG = "LoginFragmentTest";
    private ActivityScenario mainActivityScenario;

    @Rule
    public final ActivityScenarioRule<MainActivity> mainActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {

        mainActivityScenario = mainActivityScenarioRule.getScenario();

        // If any user is logged in, make sure to log them out
        try {
            onView(isRoot()).perform(waitForView(R.id.navigation_profile, TimeUnit.SECONDS.toMillis(15)));
            onView(withId(R.id.navigation_profile)).perform(click());
            onView(withId(R.id.profile_logout_btn)).perform(click());
        } catch (Exception NoMatchingViewException) {
            Log.w(TAG, "No user is not logged in, continuing test execution");
        } finally {
            onView(withId(R.id.navigation_tours)).perform(click());
        }

    }

    @Test
    public void loginWithIncorrectEmail() {
        onView(withId(R.id.login_email_et)).perform(typeText("doesNotExist@gmail.com"), closeSoftKeyboard());
        onView(isRoot()).perform(waitForView(R.id.login_register_btn, TimeUnit.SECONDS.toMillis(15)));
        onView(withId(R.id.login_password_et)).perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.login_login_btn)).perform(click());
        onView(isRoot()).perform(waitForView(R.id.login_error_tv, TimeUnit.SECONDS.toMillis(15), TimeUnit.SECONDS.toMillis(1)));
        onView(withId(R.id.login_error_tv)).check(matches(withText("There is no user record corresponding to this identifier. The user may have been deleted.")));
    }

    @Test
    public void loginWithNoUsernameOrEmail() {
        onView(withId(R.id.login_login_btn)).perform(click());
        onView(withId(R.id.login_error_tv)).check(matches(isDisplayed()));
        onView(withId(R.id.login_error_tv)).check(matches(withText("Invalid username or password")));
    }

    @Test
    public void loginWithCorrectEmailAndPassword() {
        onView(withId(R.id.login_email_et)).perform(typeText("test@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.login_password_et)).perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.login_login_btn)).perform(click());
        onView(isRoot()).perform(waitForView(R.id.personal_current_tours_title_btn, TimeUnit.SECONDS.toMillis(20)));
    }

}
