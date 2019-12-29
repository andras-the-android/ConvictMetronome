package hu.kts.cmetronome.ui.workout

import android.animation.ObjectAnimator
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import hu.kts.cmetronome.R
import hu.kts.cmetronome.Settings
import kotlinx.android.synthetic.main.fragment_workout.*

/**
 * AnimatorSet is avoided because it behaved strange on cancel.
 */
class IndicatorAnimation(private val fragment: Fragment, private val settings: Settings) {

    private var animationRunning: Boolean = false
    private var down: ObjectAnimator = ObjectAnimator.ofFloat(fragment.indicatorView, "translationY", 0f, 0f)
    private var right: ObjectAnimator = ObjectAnimator.ofFloat(fragment.indicatorView, "translationY", 0f, 0f)
    private var up: ObjectAnimator = ObjectAnimator.ofFloat(fragment.indicatorView, "translationY", 0f, 0f)
    private var left: ObjectAnimator = ObjectAnimator.ofFloat(fragment.indicatorView, "translationY", 0f, 0f)
    private var currentAnimation: ObjectAnimator? = null
    private var longPath: Float = 0F
    private var shortPath: Float = 0F

    init {
        fragment.indicatorView.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        fragment.indicatorView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        init()
                    }
                })
    }

    private fun init() {
        val resources = fragment.resources
        val indicatorDiameter = resources.getDimensionPixelSize(R.dimen.indicator_diameter).toFloat()
        val metronomePadding = resources.getDimensionPixelSize(R.dimen.metronome_padding).toFloat() * 2
        longPath = fragment.metronomeView.height.toFloat() - indicatorDiameter - metronomePadding
        shortPath = fragment.metronomeView.width.toFloat() - indicatorDiameter - metronomePadding

        down = ObjectAnimator.ofFloat(fragment.indicatorView, "translationY", 0f, longPath)

        right = ObjectAnimator.ofFloat(fragment.indicatorView, "translationX", 0f, shortPath)

        up = ObjectAnimator.ofFloat(fragment.indicatorView, "translationY", longPath, 0f)

        left = ObjectAnimator.ofFloat(fragment.indicatorView, "translationX", shortPath, 0f)
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
            Direction.RIGHT -> right.setDuration(settings.repPauseDownTime)
            Direction.UP -> up.setDuration(settings.repUpTime)
            Direction.LEFT -> left.setDuration(settings.repPauseUpTime)
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
        fragment.indicatorView.translationX = x
        fragment.indicatorView.translationY = y
    }

    enum class Direction {
        DOWN, RIGHT, UP, LEFT
    }


}
