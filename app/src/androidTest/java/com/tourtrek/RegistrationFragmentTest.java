package com.tourtrek;

import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.tourtrek.activities.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.tourtrek.EspressoExtensions.waitForView;
import static androidx.test.espresso.Espresso.pressBack;


public class RegistrationFragmentTest {

    public static final String TAG = "RegistrationFragmentTest";
    private ActivityScenario mainActivityScenario;

    @Rule
    public final ActivityScenarioRule<MainActivity> mainActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {

        // If any user is logged in, make sure to log them out
        try {
            onView(isRoot()).perform(waitForView(R.id.navigation_profile, TimeUnit.SECONDS.toMillis(15)));
            onView(withId(R.id.navigation_profile)).perform(click());
            onView(isRoot()).perform(waitForView(R.id.profile_logout_btn, TimeUnit.SECONDS.toMillis(15)));
            onView(withId(R.id.profile_logout_btn)).perform(click());
        } catch (Exception NoMatchingViewException) {
            Log.w(TAG, "No user is not logged in, continuing test execution");
        } finally {
            onView(withId(R.id.navigation_tours)).perform(click());
            onView(isRoot()).perform(waitForView(R.id.login_register_btn, TimeUnit.SECONDS.toMillis(15)));
            onView(withId(R.id.login_register_btn)).perform(scrollTo(), click());
        }

    }

    @Test
    public void RegisterWithNoUsername() throws InterruptedException {
        onView(withId(R.id.register_username_et)).perform(typeText(""));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_email_et)).perform(typeText("fakeEmail@fake.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_password1_et)).perform(typeText("password"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_password2_et)).perform(scrollTo());
        onView(withId(R.id.register_password2_et)).perform(typeText("password"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_register_btn)).perform(scrollTo());
        onView(withId(R.id.register_register_btn)).perform(click());
        onView(isRoot()).perform(waitForView(R.id.register_error_tv, TimeUnit.SECONDS.toMillis(20)));
        onView(withId(R.id.register_error_tv)).check(matches(withText("Not all fields entered")));
    }
    @Test
    public void RegisterWithNoEmail() {
        onView(withId(R.id.register_username_et)).perform(typeText("FakeUsername"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_email_et)).perform(typeText(""));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_password1_et)).perform(typeText("password"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_password2_et)).perform(scrollTo());
        onView(withId(R.id.register_password2_et)).perform(typeText("password"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_register_btn)).perform(scrollTo());
        onView(withId(R.id.register_register_btn)).perform(click());
        onView(isRoot()).perform(waitForView(R.id.register_error_tv, TimeUnit.SECONDS.toMillis(20)));
        onView(withId(R.id.register_error_tv)).check(matches(withText("Not all fields entered")));
    }

    @Test
    public void RegisterWithWrongEmail1() throws InterruptedException {
        onView(withId(R.id.register_username_et)).perform(typeText("FakeUsername"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_email_et)).perform(typeText("test123"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_password1_et)).perform(typeText("password"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_password2_et)).perform(scrollTo());
        onView(withId(R.id.register_password2_et)).perform(typeText("password"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_register_btn)).perform(scrollTo());
        onView(withId(R.id.register_register_btn)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.register_error_tv)).check(matches(withText("The email address is badly formatted.")));
    }

    @Test
    public void RegisterWithWrongEmail2() throws InterruptedException {
        onView(withId(R.id.register_username_et)).perform(typeText("FakeUsername"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_email_et)).perform(typeText("@hello.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_password1_et)).perform(typeText("password"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_password2_et)).perform(scrollTo());
        onView(withId(R.id.register_password2_et)).perform(typeText("password"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_register_btn)).perform(scrollTo());
        onView(withId(R.id.register_register_btn)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.register_error_tv)).check(matches(withText("The email address is badly formatted.")));
    }
    @Test
    public void RegisterWithNoPassword() throws InterruptedException {
        onView(withId(R.id.register_username_et)).perform(typeText("FakeUsername"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_email_et)).perform(typeText("fakeEmail@fake.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_password1_et)).perform(typeText(""));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_password2_et)).perform(scrollTo());
        onView(withId(R.id.register_password2_et)).perform(typeText("password"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_register_btn)).perform(scrollTo());
        onView(withId(R.id.register_register_btn)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.register_error_tv)).check(matches(withText("Not all fields entered")));
    }

    @Test
    public void RegisterWithNoPassword2() throws InterruptedException {
        onView(withId(R.id.register_username_et)).perform(typeText("FakeUsername"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_email_et)).perform(typeText("fakeEmail@fake.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_password1_et)).perform(typeText("password"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_password2_et)).perform(scrollTo());
        onView(withId(R.id.register_password2_et)).perform(typeText(""));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_register_btn)).perform(scrollTo());
        onView(withId(R.id.register_register_btn)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.register_error_tv)).check(matches(withText("Not all fields entered")));
    }

    @Test
    public void RegisterWithShortPasswords() throws InterruptedException {
        onView(withId(R.id.register_username_et)).perform(typeText("FakeUsername"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_email_et)).perform(typeText("fakeEmail@fake.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_password1_et)).perform(typeText("hey"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_password2_et)).perform(scrollTo());
        onView(withId(R.id.register_password2_et)).perform(typeText("hey"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_register_btn)).perform(scrollTo());
        onView(withId(R.id.register_register_btn)).perform(click());
        Thread.sleep(1000);
        onView(isRoot()).perform(waitForView(R.id.register_error_tv, TimeUnit.SECONDS.toMillis(15)));
        onView(withId(R.id.register_error_tv)).check(matches(withText("The given password is invalid. [ Password should be at least 6 characters ]")));
    }

    @Test
    public void RegisterWithMismatchedPasswords() throws InterruptedException {
        onView(withId(R.id.register_username_et)).perform(typeText("FakeUsername"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_email_et)).perform(typeText("fakeEmail@fake.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_password1_et)).perform(typeText("password"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_password2_et)).perform(scrollTo(),typeText("pasdword"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.register_register_btn)).perform(scrollTo(),click());
        Thread.sleep(1000);
        onView(withId(R.id.register_error_tv)).check(matches(withText("Passwords do not match")));
    }

}



