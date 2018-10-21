package hu.kts.cmetronome.ui.workout

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import hu.kts.cmetronome.R
import kotlinx.android.synthetic.main.activity_workout.*

/**
 * AnimatorSet is avoided because it behaved strange on cancel.
 */
class IndicatorAnimation(private val activity: Activity, private val callback: (Event) -> Unit) {

    private var animationRunning: Boolean = false
    private val down: ObjectAnimator
    private val right: ObjectAnimator
    private val up: ObjectAnimator
    private val left: ObjectAnimator
    private var currentAnimation: ObjectAnimator? = null

    private val animatorListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {
            invokeStartCallback(animation)
        }

        override fun onAnimationEnd(animation: Animator) {
            if (animationRunning) {
                currentAnimation = getNextAnimation(animation)
                if (isLastPartOfTheCycle(animation)) {
                    callback(Event.CYCLE_FINISHED)
                }
                currentAnimation?.start()
            }
        }

        override fun onAnimationCancel(animation: Animator) {
            resetIndicatorPosition()
        }

        override fun onAnimationRepeat(animation: Animator) {
        }
    }

    init {
        val resources = activity.resources
        val indicatorDiameter = resources.getDimensionPixelSize(R.dimen.indicator_diameter).toFloat()
        val metronomePadding = resources.getDimensionPixelSize(R.dimen.metronome_padding).toFloat() * 2
        val longPath = activity.metronomeView.height.toFloat() - indicatorDiameter - metronomePadding
        val shortPath = activity.metronomeView.width.toFloat() - indicatorDiameter - metronomePadding

        down = ObjectAnimator.ofFloat(activity.indicatorView, "translationY", 0f, longPath).setDuration(2000)
        down.addListener(animatorListener)

        right = ObjectAnimator.ofFloat(activity.indicatorView, "translationX", 0f, shortPath).setDuration(1000)
        right.addListener(animatorListener)

        up = ObjectAnimator.ofFloat(activity.indicatorView, "translationY", longPath, 0f).setDuration(2000)
        up.addListener(animatorListener)

        left = ObjectAnimator.ofFloat(activity.indicatorView, "translationX", shortPath, 0f).setDuration(1000)
        left.addListener(animatorListener)
    }

    fun start() {
        animationRunning = true
        currentAnimation = down
        currentAnimation?.start()
    }

    fun stop() {
        if (animationRunning) {
            animationRunning = false
            currentAnimation?.cancel()
        }
    }

    private fun invokeStartCallback(animation: Animator) {
        when {
            animation === down -> callback(Event.DOWN)
            animation === right -> callback(Event.RIGHT)
            animation === up -> callback(Event.UP)
            animation === left -> callback(Event.LEFT)
        }
    }

    private fun getNextAnimation(animation: Animator): ObjectAnimator {
        return when {
            animation === down -> right
            animation === right -> up
            animation === up -> left
            else -> down
        }
    }

    private fun isLastPartOfTheCycle(animation: Animator): Boolean {
        return animation === left
    }

    private fun resetIndicatorPosition() {
        activity.indicatorView.translationX = 0f
        activity.indicatorView.translationY = 0f
    }

    enum class Event {
        DOWN, RIGHT, UP, LEFT, CYCLE_FINISHED
    }


}
