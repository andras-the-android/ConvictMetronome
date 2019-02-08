package hu.kts.cmetronome.ui.workout

import android.animation.ObjectAnimator
import android.app.Activity
import hu.kts.cmetronome.R
import hu.kts.cmetronome.Settings
import kotlinx.android.synthetic.main.activity_workout.*

/**
 * AnimatorSet is avoided because it behaved strange on cancel.
 */
class IndicatorAnimation(private val activity: Activity, private val settings: Settings) {

    private var animationRunning: Boolean = false
    private val down: ObjectAnimator
    private val right: ObjectAnimator
    private val up: ObjectAnimator
    private val left: ObjectAnimator
    private var currentAnimation: ObjectAnimator? = null
    private val longPath: Float
    private val shortPath: Float

    init {
        val resources = activity.resources
        val indicatorDiameter = resources.getDimensionPixelSize(R.dimen.indicator_diameter).toFloat()
        val metronomePadding = resources.getDimensionPixelSize(R.dimen.metronome_padding).toFloat() * 2
        longPath = activity.metronomeView.height.toFloat() - indicatorDiameter - metronomePadding
        shortPath = activity.metronomeView.width.toFloat() - indicatorDiameter - metronomePadding

        down = ObjectAnimator.ofFloat(activity.indicatorView, "translationY", 0f, longPath).setDuration(settings.repUpDownTime)

        right = ObjectAnimator.ofFloat(activity.indicatorView, "translationX", 0f, shortPath).setDuration(settings.repPauseTime)

        up = ObjectAnimator.ofFloat(activity.indicatorView, "translationY", longPath, 0f).setDuration(settings.repUpDownTime)

        left = ObjectAnimator.ofFloat(activity.indicatorView, "translationX", shortPath, 0f).setDuration(settings.repPauseTime)
    }

    fun start(direction: Direction) {
        cancel()
        animationRunning = true
        when (direction) {
            Direction.DOWN -> moveIndicator(0f, 0f)
            Direction.RIGHT -> moveIndicator(0f, longPath)
            Direction.UP -> moveIndicator(shortPath, longPath)
            Direction.LEFT -> moveIndicator(shortPath, 0f)
        }
        currentAnimation = when (direction) {
            Direction.DOWN -> down
            Direction.RIGHT -> right
            Direction.UP -> up
            Direction.LEFT -> left
        }

        currentAnimation?.start()
    }

    fun stop() {
        cancel()
        resetIndicatorPosition()
    }

    private fun cancel() {
        if (animationRunning) {
            animationRunning = false
            currentAnimation?.cancel()
        }
    }

    private fun resetIndicatorPosition() {
        moveIndicator(0f, 0f)
    }

    private fun moveIndicator(x: Float, y: Float) {
        activity.indicatorView.translationX = x
        activity.indicatorView.translationY = y
    }

    enum class Direction {
        DOWN, RIGHT, UP, LEFT
    }


}
