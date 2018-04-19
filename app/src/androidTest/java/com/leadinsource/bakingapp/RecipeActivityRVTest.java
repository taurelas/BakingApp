package com.leadinsource.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.leadinsource.bakingapp.ui.main.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class RecipeActivityRVTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickRVItem_OpensRecipeActivity() {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.recyclerView)).check(matches(hasDescendant(withText("Nutella Pie"))));


        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.rv_steps_list)).check(matches(hasDescendant(withText("Recipe Introduction"))));

        onView(withId(R.id.rv_steps_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.step_description)).check(matches(withText("Recipe Introduction")));


        /*Recipe recipe = new Recipe();

        recipe.setName("Nutella Pie");

        Step[] steps = new Step[7];

        for(int i=0;i<7;i++) {
            steps[i] = new Step();
        }

        steps[0].setId("0");
        steps[0].setDescription("Recipe Introduction");
        steps[1].setId("1");
        steps[1].setDescription("Starting prep");
        steps[2].setId("2");
        steps[2].setDescription("Prep the cookie crust.");
        steps[3].setId("3");
        steps[3].setDescription("Press the crust into baking form.");
        steps[4].setId("4");
        steps[4].setDescription("Start filling prep");
        steps[5].setId("5");
        steps[5].setDescription("Finish filling prep");
        steps[6].setId("6");
        steps[6].setDescription("Finishing Steps");
        recipe.setSteps(steps);


        Intent intent = new Intent();
        intent.putExtra("extra_recipe", recipe);

        activityTestRule.launchActivity(intent);

        onData(anything()).inAdapterView(withId(R.id.rv_steps_list)).atPosition(0).perform(click());

        onView(withId(R.id.step_description)).check(matches(withText("Recipe introduction")));*/
    }
/*
    @Test
    void Bogota() {

    }*/

}
