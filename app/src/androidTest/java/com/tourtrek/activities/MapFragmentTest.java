package com.tourtrek.activities;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.tourtrek.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.tourtrek.EspressoExtensions.nestedScrollTo;
import static com.tourtrek.EspressoExtensions.waitForView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MapFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");

    @Test
    public void mapFragmentTest() {
        onView(isRoot()).perform(waitForView(R.id.login_email_et, TimeUnit.SECONDS.toMillis(30)));
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.login_email_et),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatEditText.perform(scrollTo(), replaceText("jrawlins@wisc.edu"), closeSoftKeyboard());

        onView(isRoot()).perform(waitForView(R.id.login_password_et, TimeUnit.SECONDS.toMillis(30)));
        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.login_password_et),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4)));
        appCompatEditText2.perform(scrollTo(), replaceText("123456"), closeSoftKeyboard());

        onView(isRoot()).perform(waitForView(R.id.login_login_btn, TimeUnit.SECONDS.toMillis(30)));
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.login_login_btn), withText("Sign In"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                7)));
        appCompatButton.perform(scrollTo(), click());

        onView(isRoot()).perform(waitForView(R.id.personal_future_tours_title_btn, TimeUnit.SECONDS.toMillis(30)));
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.personal_future_tours_title_btn), withText("Future Tours"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.FrameLayout")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton2.perform(click());

        onView(isRoot()).perform(waitForView(R.id.tour_name_et, TimeUnit.SECONDS.toMillis(30)));
        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.tour_name_et),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.core.widget.NestedScrollView")),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("MapFragment Test"), closeSoftKeyboard());

        onView(withId(R.id.tour_location_et)).perform(nestedScrollTo());
        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.tour_location_et),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                1),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("Madison, WI"), closeSoftKeyboard());

        onView(withId(R.id.tour_cost_et)).perform(nestedScrollTo());
        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.tour_cost_et),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        3),
                                1),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("10"), closeSoftKeyboard());

        onView(withId(R.id.tour_start_date_btn)).perform(nestedScrollTo());
        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.tour_start_date_btn),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        4),
                                1),
                        isDisplayed()));
        appCompatButton3.perform(click());

        onView(isRoot()).perform(waitForView(android.R.id.button1, TimeUnit.SECONDS.toMillis(30)));
        ViewInteraction appCompatButton4 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton4.perform(scrollTo(), click());

        onView(withId(R.id.tour_end_date_btn)).perform(nestedScrollTo());
        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.tour_end_date_btn),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        5),
                                1),
                        isDisplayed()));
        appCompatButton5.perform(click());

        onView(isRoot()).perform(waitForView(android.R.id.button1, TimeUnit.SECONDS.toMillis(30)));
        ViewInteraction appCompatButton6 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton6.perform(scrollTo(), click());


        onView(withId(R.id.tour_add_attraction_btn)).perform(nestedScrollTo());
        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.tour_add_attraction_btn), withText("Add Attraction"),
                        childAtPosition(
                                allOf(withId(R.id.tour_buttons_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                9)),
                                4),
                        isDisplayed()));
        appCompatButton7.perform(click());

        onView(isRoot()).perform(waitForView(R.id.attraction_search_ib, TimeUnit.SECONDS.toMillis(30)));
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.attraction_search_ib),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        1),
                                1)));
        appCompatImageButton.perform(scrollTo(), click());

        onView(isRoot()).perform(waitForView(R.id.places_autocomplete_search_bar, TimeUnit.SECONDS.toMillis(30)));
        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.places_autocomplete_search_bar),
                        childAtPosition(
                                allOf(withId(R.id.places_autocomplete_search_bar_container),
                                        childAtPosition(
                                                withId(R.id.places_autocomplete_content),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText6.perform(click());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.places_autocomplete_search_bar),
                        childAtPosition(
                                allOf(withId(R.id.places_autocomplete_search_bar_container),
                                        childAtPosition(
                                                withId(R.id.places_autocomplete_content),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText7.perform(replaceText("University of Wisconsi"), closeSoftKeyboard());

        onView(isRoot()).perform(waitForView(R.id.places_autocomplete_list, TimeUnit.SECONDS.toMillis(30)));
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.places_autocomplete_list),
                        childAtPosition(
                                withId(R.id.places_autocomplete_content),
                                3)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        onView(isRoot()).perform(waitForView(R.id.attraction_name_et, TimeUnit.SECONDS.toMillis(30)));
        onView(withId(R.id.attraction_cost_et)).perform(nestedScrollTo());
        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.attraction_cost_et),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        4),
                                1)));
        appCompatEditText8.perform(scrollTo(), replaceText("1"), closeSoftKeyboard());

        onView(withId(R.id.attraction_start_date_btn)).perform(nestedScrollTo());
        ViewInteraction appCompatButton8 = onView(
                allOf(withId(R.id.attraction_start_date_btn),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        5),
                                1)));
        appCompatButton8.perform(scrollTo(), click());

        ViewInteraction appCompatButton9 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton9.perform(scrollTo(), click());

        onView(withId(R.id.attraction_start_time_btn)).perform(nestedScrollTo());
        ViewInteraction appCompatButton10 = onView(
                allOf(withId(R.id.attraction_start_time_btn),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        6),
                                1)));
        appCompatButton10.perform(scrollTo(), click());

        ViewInteraction appCompatButton11 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton11.perform(scrollTo(), click());

        onView(withId(R.id.attraction_end_date_btn)).perform(nestedScrollTo());
        ViewInteraction appCompatButton12 = onView(
                allOf(withId(R.id.attraction_end_date_btn),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        7),
                                1)));
        appCompatButton12.perform(scrollTo(), click());

        ViewInteraction appCompatButton13 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton13.perform(scrollTo(), click());

        onView(withId(R.id.attraction_end_time_btn)).perform(nestedScrollTo());
        ViewInteraction appCompatButton14 = onView(
                allOf(withId(R.id.attraction_end_time_btn),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        8),
                                1)));
        appCompatButton14.perform(scrollTo(), click());

        ViewInteraction numericTextView = onView(
                allOf(withClassName(is("com.android.internal.widget.NumericTextView")), withText("7"),
                        childAtPosition(
                                allOf(withClassName(is("android.widget.RelativeLayout")),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        numericTextView.perform(click());

        ViewInteraction appCompatButton15 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatButton15.perform(scrollTo(), click());

        onView(withId(R.id.attraction_description_et)).perform(nestedScrollTo());
        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.attraction_description_et),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        9),
                                1)));
        appCompatEditText9.perform(scrollTo(), replaceText("111"), closeSoftKeyboard());

        onView(withId(R.id.attraction_update_btn)).perform(nestedScrollTo());
        ViewInteraction appCompatButton16 = onView(
                allOf(withId(R.id.attraction_update_btn), withText("Add Attraction"),
                        childAtPosition(
                                allOf(withId(R.id.attraction_buttons_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                11)),
                                0)));
        appCompatButton16.perform(scrollTo(), click());

        onView(isRoot()).perform(waitForView(R.id.tour_attractions_rv, TimeUnit.SECONDS.toMillis(30)));
        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.tour_attractions_rv),
                        childAtPosition(
                                withId(R.id.tour_attractions_srl),
                                0)));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));

        onView(withId(R.id.attraction_navigation_btn)).perform(nestedScrollTo());
        ViewInteraction appCompatButton17 = onView(
                allOf(withId(R.id.attraction_navigation_btn), withText("Navigation"),
                        childAtPosition(
                                allOf(withId(R.id.attraction_buttons_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                11)),
                                1)));
        appCompatButton17.perform(scrollTo(), click());

        ViewInteraction imageView = onView(
                allOf(withContentDescription("Get directions"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        3),
                                0),
                        isDisplayed()));
        imageView.perform(click());

        ViewInteraction imageView2 = onView(
                allOf(withContentDescription("Open in Google Maps"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        3),
                                1),
                        isDisplayed()));
        imageView2.perform(click());
    }

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
