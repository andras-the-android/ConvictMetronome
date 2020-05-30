package hu.kts.cmetronome.ui.workout

import android.animation.ObjectAnimator
import android.view.ViewTreeObserver
import hu.kts.cmetronome.repository.Settings
import javax.inject.Inject

/**
 * AnimatorSet is avoided because it behaved strange on cancel.
 */
class IndicatorAnimation @Inject constructor(private val fragment: WorkoutFragment, private val settings: Settings) {

    private var animationRunning: Boolean = false
    private var down: ObjectAnimator = ObjectAnimator.ofFloat(fragment.binding.indicatorView, "translationY", 0f, 0f)
    private var right: ObjectAnimator = ObjectAnimator.ofFloat(fragment.binding.indicatorView, "translationY", 0f, 0f)
    private var up: ObjectAnimator = ObjectAnimator.ofFloat(fragment.binding.indicatorView, "translationY", 0f, 0f)
    private var left: ObjectAnimator = ObjectAnimator.ofFloat(fragment.binding.indicatorView, "translationY", 0f, 0f)
    private var currentAnimation: ObjectAnimator? = null
    private var longPath: Float = 0F
    private var shortPath: Float = 0F

    init {
        fragment.binding.indicatorView.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        fragment.binding.indicatorView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        init()
                    }
                })
    }

    private fun init() {
        longPath = fragment.binding.metronomeIndicatorPath.height.toFloat()
        shortPath = fragment.binding.metronomeIndicatorPath.width.toFloat()

        down = ObjectAnimator.ofFloat(fragment.binding.indicatorView, "translationY", 0f, longPath)

        right = ObjectAnimator.ofFloat(fragment.binding.indicatorView, "translationX", 0f, shortPath)

        up = ObjectAnimator.ofFloat(fragment.binding.indicatorView, "translationY", longPath, 0f)

        left = ObjectAnimator.ofFloat(fragment.binding.indicatorView, "translationX", shortPath, 0f)
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
        fragment.binding.indicatorView.translationX = x
        fragment.binding.indicatorView.translationY = y
    }

    enum class Direction {
        DOWN, RIGHT, UP, LEFT
    }


}
