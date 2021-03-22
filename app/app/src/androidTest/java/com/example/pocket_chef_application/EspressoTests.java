package com.example.pocket_chef_application;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


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
        onView(withId(R.id.fab_generate_recipes_btn)).perform(click());
        onView(withId(R.id.generateFragment)).check(matches(isDisplayed()));
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
    }

    @Test
    public void test_generate_recipe(){
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.fab_generate_recipes_btn)).perform(click());
        onView(withId(R.id.generateFragment)).check(matches(isDisplayed()));
        onView(withId(R.id.recipe_button)).perform(click());
        onView(withId(R.id.status_field)).check(matches(withText("Connected")));

    }

    // test pantry fragment is displayed
    @Test
    public void test_goto_pantry(){
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.fab_pantry_btn)).perform(click());
        onView(withId(R.id.pantryFragment)).check(matches(isDisplayed()));
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
    }

    // test upload fragment is displayed
    @Test
    public void test_goto_upload(){
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.fab_upload_recipe_btn)).perform(click());
        onView(withId(R.id.upload_fragment)).check(matches(isDisplayed()));
    }

    // test splashpage fragment is displayed
    @Test
    public void test_goto_splashpage(){
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.fab_pantry_btn)).perform(click());
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.fab_homepage_btn)).perform(click());

        onView(withId(R.id.homepageFragment)).check(matches(isDisplayed()));
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
    }


}
