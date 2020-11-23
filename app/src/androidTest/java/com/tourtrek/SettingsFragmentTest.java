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
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.tourtrek.EspressoExtensions.waitForView;
import static androidx.test.espresso.Espresso.pressBack;

public class SettingsFragmentTest {

    public static final String TAG = "SettingsFragmentTest";
    private ActivityScenario mainActivityScenario;

    @Rule
    public final ActivityScenarioRule<MainActivity> mainActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {
        // If any user is logged in, make sure to log them out
        try {
            onView(isRoot()).perform(EspressoExtensions.waitForView(R.id.navigation_profile, TimeUnit.SECONDS.toMillis(15)));
            onView(withId(R.id.navigation_profile)).perform(click());
            onView(withId(R.id.profile_logout_btn)).perform(click());
        } catch (Exception NoMatchingViewException) {
            Log.w(TAG, "No user is not logged in, continuing test execution");
        } finally {
            //then log into test profile
            onView(withId(R.id.navigation_tours)).perform(click());
            onView(withId(R.id.login_email_et)).perform(typeText("testingaccount@gmail.com"));
            Espresso.closeSoftKeyboard();
            onView(withId(R.id.login_password_et)).perform(typeText("password"));
            Espresso.closeSoftKeyboard();
            onView(withId(R.id.login_login_btn)).perform(click());
            onView(isRoot()).perform(waitForView(R.id.personal_current_tours_title_btn, TimeUnit.SECONDS.toMillis(15)));
            onView(withId(R.id.navigation_profile)).perform(click());
            onView(isRoot()).perform(waitForView(R.id.profile_settings_btn, TimeUnit.SECONDS.toMillis(15)));
            onView(withId(R.id.profile_settings_btn)).perform(click());
            onView(isRoot()).perform(waitForView(R.id.settings_change_password_btn, TimeUnit.SECONDS.toMillis(15)));
        }
    }

    @After
    public void cleanUp(){

    }

//    @Test
//    public void WorkingChangePassword() throws InterruptedException {
//        //change password
//        onView(withId(R.id.settings_change_password_btn)).perform(click());
//        onView(isRoot()).perform(waitForView(R.id.change_password_current_password_et, TimeUnit.SECONDS.toMillis(15)));
//        onView(withId(R.id.change_password_current_password_et)).perform(typeText("password"));
//        Espresso.closeSoftKeyboard();
//        onView(withId(R.id.change_password_new_password_et)).perform(typeText("testpassword123"));
//        Espresso.closeSoftKeyboard();
//        onView(withId(R.id.change_password_confirm_new_password_et)).perform(typeText("testpassword123"));
//        Espresso.closeSoftKeyboard();
//        onView(withId(R.id.change_password_update_password_btn)).perform(click());
//
//
//        //log out and try logging in again
//        Thread.sleep(5000);
//        onView(isRoot()).perform(EspressoExtensions.waitForView(R.id.navigation_profile, TimeUnit.SECONDS.toMillis(15)));
//        onView(withId(R.id.navigation_profile)).perform(click());
//
//        onView(isRoot()).perform(waitForView(R.id.profile_logout_btn, TimeUnit.SECONDS.toMillis(15)));
//        onView(withId(R.id.profile_logout_btn)).perform(click());
//        onView(isRoot()).perform(waitForView(R.id.login_email_et, TimeUnit.SECONDS.toMillis(15)));
//        onView(withId(R.id.login_email_et)).perform(typeText("testingaccount@gmail.com"));
//        Espresso.closeSoftKeyboard();
//        onView(withId(R.id.login_password_et)).perform(typeText("testpassword123"));
//        Espresso.closeSoftKeyboard();
//        onView(withId(R.id.login_login_btn)).perform(click());
//
//        //then change password back
//        onView(isRoot()).perform(waitForView(R.id.profile_settings_btn, TimeUnit.SECONDS.toMillis(15)));
//        onView(withId(R.id.profile_settings_btn)).perform(click());
//        onView(isRoot()).perform(waitForView(R.id.settings_change_password_btn, TimeUnit.SECONDS.toMillis(15)));
//        onView(withId(R.id.settings_change_password_btn)).perform(click());
//        onView(isRoot()).perform(waitForView(R.id.change_password_current_password_et, TimeUnit.SECONDS.toMillis(15)));
//        onView(withId(R.id.change_password_current_password_et)).perform(typeText("testpassword123"));
//        Espresso.closeSoftKeyboard();
//        onView(withId(R.id.change_password_new_password_et)).perform(typeText("password"));
//        Espresso.closeSoftKeyboard();
//        onView(withId(R.id.change_password_confirm_new_password_et)).perform(typeText("password"));
//        Espresso.closeSoftKeyboard();
//        onView(withId(R.id.change_password_update_password_btn)).perform(click());
//        Thread.sleep(5000);
//        onView(isRoot()).perform(EspressoExtensions.waitForView(R.id.navigation_profile, TimeUnit.SECONDS.toMillis(15)));
//        onView(withId(R.id.navigation_profile)).perform(click());
//        onView(isRoot()).perform(waitForView(R.id.profile_settings_btn, TimeUnit.SECONDS.toMillis(100)));
//
//    }

    @Test
    public void ChangePasswordWithMismatchedPasswords(){
        //try to change password
        onView(withId(R.id.settings_change_password_btn)).perform(click());
        onView(isRoot()).perform(waitForView(R.id.change_password_current_password_et, TimeUnit.SECONDS.toMillis(15)));
        onView(withId(R.id.change_password_current_password_et)).perform(typeText("password"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.change_password_new_password_et)).perform(typeText("testpassword12"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.change_password_confirm_new_password_et)).perform(typeText("testpassword123"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.change_password_update_password_btn)).perform(click());
        onView(withId(R.id.change_password_error_tv)).check(matches(withText("Passwords do not match")));

    }

    @Test
    public void ChangeEmail() throws InterruptedException {
        //change email
        onView(withId(R.id.settings_change_email_btn)).perform(click());
        onView(isRoot()).perform(waitForView(R.id.change_email_new_email_et, TimeUnit.SECONDS.toMillis(15)));
        onView(withId(R.id.change_email_new_email_et)).perform(typeText("newtestemail@gmail.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.change_email_update_email_btn)).perform(click());


        //log out and try logging in again
        Thread.sleep(5000);
        onView(isRoot()).perform(EspressoExtensions.waitForView(R.id.navigation_profile, TimeUnit.SECONDS.toMillis(15)));
        onView(withId(R.id.navigation_profile)).perform(click());

        onView(isRoot()).perform(waitForView(R.id.profile_logout_btn, TimeUnit.SECONDS.toMillis(15)));
        onView(withId(R.id.profile_logout_btn)).perform(click());
        onView(isRoot()).perform(waitForView(R.id.login_email_et, TimeUnit.SECONDS.toMillis(15)));
        onView(withId(R.id.login_email_et)).perform(typeText("newtestemail@gmail.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.login_password_et)).perform(typeText("password"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.login_login_btn)).perform(click());

        //then change password back
        onView(isRoot()).perform(waitForView(R.id.profile_settings_btn, TimeUnit.SECONDS.toMillis(15)));
        onView(withId(R.id.profile_settings_btn)).perform(click());
        onView(isRoot()).perform(waitForView(R.id.settings_change_email_btn, TimeUnit.SECONDS.toMillis(15)));
        onView(withId(R.id.settings_change_email_btn)).perform(click());
        onView(isRoot()).perform(waitForView(R.id.change_email_new_email_et, TimeUnit.SECONDS.toMillis(15)));
        onView(withId(R.id.change_email_new_email_et)).perform(typeText("testingaccount@gmail.com"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.change_email_update_email_btn)).perform(click());
        Thread.sleep(5000);
        onView(isRoot()).perform(EspressoExtensions.waitForView(R.id.navigation_profile, TimeUnit.SECONDS.toMillis(15)));
        onView(withId(R.id.navigation_profile)).perform(click());
        onView(isRoot()).perform(waitForView(R.id.profile_settings_btn, TimeUnit.SECONDS.toMillis(100)));

    }

    @Test
    public void ChangeUsername() throws InterruptedException {

        //change username
        onView(withId(R.id.settings_change_username_btn)).perform(click());
        onView(isRoot()).perform(waitForView(R.id.change_username_new_username_et, TimeUnit.SECONDS.toMillis(15)));
        onView(withId(R.id.change_username_new_username_et)).perform(typeText("CHANGEDUSERNAME"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.change_username_update_username_btn)).perform(click());

        //go back to profile screen
        Thread.sleep(5000);
        onView(isRoot()).perform(EspressoExtensions.waitForView(R.id.navigation_profile, TimeUnit.SECONDS.toMillis(15)));
        onView(withId(R.id.navigation_profile)).perform(click());

        //make sure username changed
        onView(isRoot()).perform(EspressoExtensions.waitForView(R.id.profile_username_tv, TimeUnit.SECONDS.toMillis(15)));
        onView(withId(R.id.profile_username_tv)).check(matches(withText("CHANGEDUSERNAME")));

        //change name back
        onView(isRoot()).perform(waitForView(R.id.profile_settings_btn, TimeUnit.SECONDS.toMillis(15)));
        onView(withId(R.id.profile_settings_btn)).perform(click());
        onView(withId(R.id.settings_change_username_btn)).perform(click());
        onView(isRoot()).perform(waitForView(R.id.change_username_new_username_et, TimeUnit.SECONDS.toMillis(15)));
        onView(withId(R.id.change_username_new_username_et)).perform(typeText("Testing Account"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.change_username_update_username_btn)).perform(click());


    }



}
