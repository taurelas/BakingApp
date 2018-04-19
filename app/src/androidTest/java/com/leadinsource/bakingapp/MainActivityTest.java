package com.leadinsource.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.leadinsource.bakingapp.ui.main.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Testing UI of the MainActivity
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    private IdlingResource idlingResource;

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void registerIdlingResource() {
        idlingResource = activityTestRule.getActivity().getIdlingResource();

        Espresso.registerIdlingResources(idlingResource);
    }

    @Test
    public void idlingResourceTest() {
        onView(withId(R.id.recyclerView)).check(matches(hasDescendant(withText("Nutella Pie"))));
        onView(withId(R.id.recyclerView)).check(matches(hasDescendant(withText("Brownies"))));
        onView(withId(R.id.recyclerView)).check(matches(hasDescendant(withText("Yellow Cake"))));
        onView(withId(R.id.recyclerView)).check(matches(hasDescendant(withText("Cheesecake"))));
    }


    @After
    public void unregisterIdlingResource() {
        if(idlingResource!=null) {
            Espresso.unregisterIdlingResources(idlingResource);
        }
    }
}
