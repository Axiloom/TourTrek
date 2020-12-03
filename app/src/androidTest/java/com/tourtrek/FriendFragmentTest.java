package com.tourtrek;

import android.util.Log;

import com.tourtrek.activities.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.tourtrek.EspressoExtensions.nestedScrollTo;
import static com.tourtrek.EspressoExtensions.waitForView;
import static java.lang.Thread.sleep;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FriendFragmentTest {

    public static final String TAG = "FriendFragmentTest";
    private ActivityScenario mainActivityScenario;

    @Rule
    public final ActivityScenarioRule<MainActivity> mainActivityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);


    @Before
    public void setup() throws InterruptedException {

        mainActivityScenario = mainActivityScenarioRule.getScenario();

        // If any user is logged in, make sure to log them out
        try {
            onView(isRoot()).perform(waitForView(R.id.navigation_profile, TimeUnit.SECONDS.toMillis(3)));

            onView(withId(R.id.navigation_profile)).perform(click());
            onView(withId(R.id.profile_logout_btn)).perform(click());
        } catch (Exception NoMatchingViewException) {
            Log.w(TAG, "No user is not logged in, continuing test execution");
        } finally {
            onView(withId(R.id.navigation_tours)).perform(click());
            onView(withId(R.id.login_email_et)).perform(typeText("cctest@gmail.com"), ViewActions.closeSoftKeyboard());
            onView(withId(R.id.login_password_et)).perform(typeText("123456"), ViewActions.closeSoftKeyboard());
            onView(withId(R.id.login_login_btn)).perform(click());
            onView(isRoot()).perform(waitForView(R.id.personal_past_tours_rv, TimeUnit.SECONDS.toMillis(1000)));
            sleep(1000);
            onView(withId(R.id.navigation_profile)).perform(click());
            onView(isRoot()).perform(waitForView(R.id.profile_friend_btn, TimeUnit.SECONDS.toMillis(1000)));
            onView(withId(R.id.profile_friend_btn)).perform(click());
        }
    }

    @Test
    public void addFriendWithNullEmail() throws InterruptedException {

        onView(withId(R.id.add_friend_search_btn)).perform(click());
        onView(isRoot()).perform(waitForView(R.id.add_friend_error_tv, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(10)));
        onView(withId(R.id.add_friend_error_tv)).check(matches(withText("Please enter your friend's email")));
    }

    @Test
    public void addFriendWithWrongEmail() throws InterruptedException {

        onView(withId(R.id.add_friend_email_et)).perform((typeText("doesNotExist@gmail.com")), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.add_friend_search_btn)).perform(click());
        onView(isRoot()).perform(waitForView(R.id.add_friend_error_tv, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(10)));
        onView(withId(R.id.add_friend_error_tv)).check(matches(withText("Cannot find user with email entered")));
    }

    @Test
    public void addFriendAlreadyExisted() throws InterruptedException {

        onView(withId(R.id.add_friend_email_et)).perform((typeText("email@gmail.com")), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.add_friend_search_btn)).perform(click());
        onView(isRoot()).perform(waitForView(R.id.add_friend_add_btn, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(10)));
        onView(withId(R.id.add_friend_add_btn)).perform(click());
        onView(isRoot()).perform(waitForView(R.id.add_friend_error_tv, TimeUnit.SECONDS.toMillis(3), TimeUnit.SECONDS.toMillis(10)));
        onView(withId(R.id.add_friend_error_tv)).check(matches(withText("Friend already exists")));
    }

//    @Test
//    public void addFriendSuccessfullyFeedback() throws InterruptedException {
//
//        onView(withId(R.id.add_friend_email_et)).perform((typeText("Robert@gmail.com")), ViewActions.closeSoftKeyboard());
//        onView(withId(R.id.add_friend_search_btn)).perform(click());
//        onView(isRoot()).perform(waitForView(R.id.add_friend_add_btn, TimeUnit.SECONDS.toMillis(5), TimeUnit.SECONDS.toMillis(10)));
//        onView(withId(R.id.add_friend_add_btn)).perform(click());
//        onView(withText(R.string.Add_Friend_Success_TOAST_STRING)).inRoot(new ToastMatcher())
//                .check(matches(isDisplayed()));
//        sleep(1000);
//        onView(withId(R.id.add_friend_my_friends_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
//        onView(isRoot()).perform(waitForView(R.id.friend_delete_btn, TimeUnit.SECONDS.toMillis(5)));
//        onView(withId(R.id.friend_delete_btn)).perform(nestedScrollTo());
//        onView(withId(R.id.friend_delete_btn)).perform(click());
//    }

    @Test
    public void deleteFriendSuccessfully() throws InterruptedException {

        onView(withId(R.id.add_friend_email_et)).perform((typeText("Robert@gmail.com")), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.add_friend_search_btn)).perform(click());
        onView(isRoot()).perform(waitForView(R.id.add_friend_add_btn, TimeUnit.SECONDS.toMillis(5), TimeUnit.SECONDS.toMillis(10)));
        onView(withId(R.id.add_friend_add_btn)).perform(click());
        sleep(1000);
        onView(withId(R.id.add_friend_my_friends_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        onView(isRoot()).perform(waitForView(R.id.friend_delete_btn, TimeUnit.SECONDS.toMillis(5)));
        onView(withId(R.id.friend_delete_btn)).perform(nestedScrollTo());
        onView(withId(R.id.friend_delete_btn)).perform(click());
        onView(withText("Friend removed")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }
}
