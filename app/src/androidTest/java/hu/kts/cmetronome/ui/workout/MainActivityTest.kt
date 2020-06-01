package hu.kts.cmetronome.ui.workout

import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import hu.kts.cmetronome.R
import hu.kts.cmetronome.repository.WorkoutSettings
import hu.kts.cmetronome.ui.MainActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsAnything
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityRule = ActivityTestRule(
            MainActivity::class.java)

    @Test
    fun testApp() {
        testBeforeStart()
        clickOnCounter()
        testCountdown()
        testInProgress(0, 0)
        clickOnCounter()
        testPause(2, 0)
        clickOnCounter()
        testCountdown()
        clickOnCounter()
        testPause(2, 0)
        longClickOnCounter()
        testBetweenSets(2, 1)
        longClickOnCounter()
        testBeforeStart()
    }

    private fun testBeforeStart() {
        matchHelpText(R.string.help_before_start)
        onCounter()
                .check(matchText("0"))
                .check(matchTextColor(R.color.secondary_text))
        matchSetCounter(0)
        checkIndicatorIsOnTheBaseSpot()
        checkIsStopperInvisible()
    }

    private fun testCountdown() {

        onCounter()
                .perform(waitMillis(1100))
                .check(matchText("2"))
                .check(matchTextColor(R.color.accent))
        matchHelpText(R.string.empty_string)
        checkIndicatorIsOnTheBaseSpot()
        checkIsStopperInvisible()
    }

    private fun testInProgress(initialRepCount: Int, setCount: Int) {
        onCounter()
                .perform(waitMillis(2000))
                .check(matchText(initialRepCount.toString()))
                .check(matchTextColor(R.color.secondary_text))

                .perform(waitMillis(6000))
                .check(matchText((initialRepCount + 1).toString()))
                .check(matchTextColor(R.color.secondary_text))

                .perform(waitMillis(6000))
                .check(matchText((initialRepCount + 2).toString()))
                .check(matchTextColor(R.color.secondary_text))

        matchHelpText(R.string.help_in_progress)
        matchSetCounter(setCount)
        checkIndicatorIsNotOnTheBaseSpot()
        checkIsStopperInvisible()
    }

    private fun testPause(repCount: Int, setCount: Int) {
        checkIndicatorIsOnTheBaseSpot()
        matchHelpText(R.string.help_paused)
        onCounter()
                .perform(waitMillis(6000))
                .check(matches(withText(repCount.toString())))
                .check(matches(withTextColor(R.color.secondary_text)))
        matchSetCounter(setCount)
        checkIsStopperInvisible()
    }

    companion object {

        @BeforeClass
        fun setUp() {
            val preferences = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext())
            preferences.edit().putString(WorkoutSettings.KEY_COUNTDOWN_START_VALUE, "3").apply()
        }

        private fun testBetweenSets(repCount: Int, setCount: Int) {
            checkIndicatorIsOnTheBaseSpot()
            matchHelpText(R.string.help_between_sets)
            onCounter()
                    .check(matches(withText(repCount.toString())))
                    .check(matches(withTextColor(R.color.secondary_text)))

            onView(withId(R.id.stopwatchTextView))
                    .check(matches(withText("00:00")))
                    .perform(waitMillis(5000))
                    .check(matches(withText("00:05")))

            matchSetCounter(setCount)
        }

        private fun matchTextColor(@ColorRes colorResId: Int): ViewAssertion {
            return matches(withTextColor(colorResId))
        }

        private fun matchText(text: String): ViewAssertion {
            return matches(withText(text))
        }

        /** Perform action of waiting for a specific view id.  */
        fun waitMillis(millis: Long): ViewAction {
            return object : ViewAction {
                override fun getConstraints(): Matcher<View> {
                    return IsAnything()
                }

                override fun getDescription(): String {
                    return "waits $millis millis"
                }

                override fun perform(uiController: UiController, view: View) {
                    uiController.loopMainThreadUntilIdle()
                    val startTime = System.currentTimeMillis()
                    val endTime = startTime + millis

                    do {
                        uiController.loopMainThreadForAtLeast(50)
                    } while (System.currentTimeMillis() < endTime)
                }
            }
        }

        fun clickOnCounter(): ViewInteraction {
            return onCounter().perform(click())

        }

        fun longClickOnCounter(): ViewInteraction {
            return onCounter().perform(longClick())

        }

        private fun onCounter(): ViewInteraction {
            return onView(withId(R.id.repCounterTextView))
        }

        private fun matchHelpText(@StringRes helpTextResId: Int) {
            onView(withId(R.id.helpTextView)).check(matches(withText(helpTextResId)))
        }

        private fun matchSetCounter(setCount: Int) {
            onView(withId(R.id.setCounterTextView)).check(matches(withText(setCount.toString())))
        }

        private fun withTextColor(@ColorRes colorResId: Int): Matcher<View> {
            return object : BoundedMatcher<View, TextView>(TextView::class.java) {
                override fun matchesSafely(textView: TextView): Boolean {
                    val textColorResId = ContextCompat.getColor(InstrumentationRegistry.getTargetContext(), colorResId)
                    return textView.currentTextColor == textColorResId
                }

                override fun describeTo(description: Description) {
                    description.appendText("Text color")
                }
            }
        }

        private fun checkIndicatorIsOnTheBaseSpot() {
            onView(withId(R.id.indicatorView)).check(matches(withZeroTranslationXY()))
        }

        private fun checkIndicatorIsNotOnTheBaseSpot() {
            onView(withId(R.id.indicatorView)).check(matches(not(withZeroTranslationXY())))
        }

        private fun withZeroTranslationXY(): Matcher<View> {
            return object : TypeSafeMatcher<View>() {
                override fun matchesSafely(indicator: View): Boolean {
                    return indicator.translationX == 0f && indicator.translationY == 0f
                }

                override fun describeTo(description: Description) {
                    description.appendText("Text color")
                }
            }
        }

        private fun checkIsStopperInvisible() {
            onView(withId(R.id.stopwatchTextView)).check(matches(not(isDisplayed())))
        }
    }

}