package hu.kts.cmetronome.ui.workout

import android.animation.ObjectAnimator
import android.app.Activity
import android.view.ViewTreeObserver
import hu.kts.cmetronome.R
import hu.kts.cmetronome.Settings
import kotlinx.android.synthetic.main.activity_workout.*

/**
 * AnimatorSet is avoided because it behaved strange on cancel.
 */
class IndicatorAnimation(private val activity: Activity, private val settings: Settings) {

    private var animationRunning: Boolean = false
    private var down: ObjectAnimator = ObjectAnimator.ofFloat(activity.indicatorView, "translationY", 0f, 0f)
    private var right: ObjectAnimator = ObjectAnimator.ofFloat(activity.indicatorView, "translationY", 0f, 0f)
    private var up: ObjectAnimator = ObjectAnimator.ofFloat(activity.indicatorView, "translationY", 0f, 0f)
    private var left: ObjectAnimator = ObjectAnimator.ofFloat(activity.indicatorView, "translationY", 0f, 0f)
    private var currentAnimation: ObjectAnimator? = null
    private var longPath: Float = 0F
    private var shortPath: Float = 0F

    init {
        activity.indicatorView.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        activity.indicatorView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        init()
                    }
                })
    }

    private fun init() {
        val resources = activity.resources
        val indicatorDiameter = resources.getDimensionPixelSize(R.dimen.indicator_diameter).toFloat()
        val metronomePadding = resources.getDimensionPixelSize(R.dimen.metronome_padding).toFloat() * 2
        longPath = activity.metronomeView.height.toFloat() - indicatorDiameter - metronomePadding
        shortPath = activity.metronomeView.width.toFloat() - indicatorDiameter - metronomePadding

        down = ObjectAnimator.ofFloat(activity.indicatorView, "translationY", 0f, longPath)

        right = ObjectAnimator.ofFloat(activity.indicatorView, "translationX", 0f, shortPath)

        up = ObjectAnimator.ofFloat(activity.indicatorView, "translationY", longPath, 0f)

        left = ObjectAnimator.ofFloat(activity.indicatorView, "translationX", shortPath, 0f)
        resetIndicatorPosition()
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
            Direction.DOWN -> down.setDuration(settings.repDownTime)
            Direction.RIGHT -> right.setDuration(settings.repPauseUpTime)
            Direction.UP -> up.setDuration(settings.repUpTime)
            Direction.LEFT -> left.setDuration(settings.repPauseDownTime)
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
        if (settings.repStartsWithUp) {
            moveIndicator(shortPath, longPath)
        } else {
            moveIndicator(0f, 0f)
        }
    }

    private fun moveIndicator(x: Float, y: Float) {
        activity.indicatorView.translationX = x
        activity.indicatorView.translationY = y
    }

    enum class Direction {
        DOWN, RIGHT, UP, LEFT
    }


}
