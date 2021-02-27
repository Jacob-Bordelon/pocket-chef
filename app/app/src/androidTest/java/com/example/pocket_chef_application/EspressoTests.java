package com.example.pocket_chef_application;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;


@RunWith(AndroidJUnit4.class)
public class EspressoTests {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule
            = new ActivityScenarioRule<>(MainActivity.class);

    // Test FAB
    @Test
    public void test_fab(){
        onView(withId(R.id.fab))
                .perform(click())
                .check(matches(isDisplayed()));
    }

    // test generate fragment is displayed
    @Test
    public void test_goto_generate(){
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.imageButton1)).perform(click());
        onView(withId(R.id.secondFragment)).check(matches(isDisplayed()));
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
    }

    // test pantry fragment is displayed
    @Test
    public void test_goto_pantry(){
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.imageButton3)).perform(click());
        onView(withId(R.id.thirdFragment)).check(matches(isDisplayed()));
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
    }

    // test upload fragment is displayed
    @Test
    public void test_goto_upload(){
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.imageButton4)).perform(click());
        onView(withId(R.id.upload_fragment)).check(matches(isDisplayed()));
    }

    // test splashpage fragment is displayed
    @Test
    public void test_goto_splashpage(){
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.imageButton3)).perform(click());
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.imageButton2)).perform(click());

        onView(withId(R.id.firstFragment)).check(matches(isDisplayed()));
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
    }
}
