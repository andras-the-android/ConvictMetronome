package hu.kts.cmetronome;

import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.annotation.ColorRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsAnything;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by andrasnemeth on 19/02/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @BeforeClass
    public static void setUp() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
        preferences.edit().putString(Settings.KEY_COUNTDOWN_START_VALUE, "3").apply();
    }


    @Test
    public void testSomething() {
        clickOnCounter()
                .perform(waitMillis(1100))
                .check(matches(withText("2")))
                .check(matches(withTextColor(R.color.accent)));
    }

    /** Perform action of waiting for a specific view id. */
    public static ViewAction waitMillis(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return new IsAnything<>();
            }

            @Override
            public String getDescription() {
                return "waits " + millis + " millis";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                SystemClock.sleep(millis);
            }
        };
    }

    public static ViewInteraction clickOnCounter() {
        return onView(withId(R.id.rep_counter))
                .perform(click());

    }

    public static ViewInteraction longClickOnCounter() {
        return onView(withId(R.id.rep_counter))
                .perform(click());

    }

    public static Matcher<View> withTextColor(@ColorRes int colorResId) {
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            protected boolean matchesSafely(TextView textView) {
                int textColorResId = ContextCompat.getColor(InstrumentationRegistry.getTargetContext(), colorResId);
                return textView.getCurrentTextColor() == textColorResId;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Text color" );
            }
        };
    }
}