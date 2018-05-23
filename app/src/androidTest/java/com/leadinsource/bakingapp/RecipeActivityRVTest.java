package com.leadinsource.bakingapp;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;

import com.leadinsource.bakingapp.ui.recipe.RecipeActivity;
import com.leadinsource.bakingapp.widget.ListRemoteViewsFactory;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.is;

;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class RecipeActivityRVTest {

    @Rule
    public ActivityTestRule<RecipeActivity> activityTestRule = new ActivityTestRule<>(RecipeActivity.class, false, false);

    public Intent setupIntent() {
        int recipeId = 1;

        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), RecipeActivity.class);
        intent.putExtra(ListRemoteViewsFactory.EXTRA_RECIPE_ID, recipeId);

        return intent;
    }

    @Test
    public void clickRVItem_OpensRecipeActivity() {

        Intent intent = setupIntent();

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.rv_steps_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));


        onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is("Nutella Pie"))));
    }

    // as per http://blog.sqisland.com/2015/05/espresso-match-toolbar-title.html
    public static Matcher<Object> withToolbarTitle(final Matcher<String> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }

}
