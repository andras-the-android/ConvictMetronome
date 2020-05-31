package hu.kts.cmetronome.ui.workout

import android.view.View
import hu.kts.cmetronome.TimeProvider
import hu.kts.cmetronome.di.TimeProviderStopwatch
import hu.kts.cmetronome.di.WorkoutScope
import hu.kts.cmetronome.sounds.Sounds
import java.util.*
import javax.inject.Inject

@WorkoutScope
class Stopwatch @Inject constructor(
        private val fragment: WorkoutFragment,
        @TimeProviderStopwatch private val timeProvider: TimeProvider,
        private val sounds: Sounds
) {

    val startTime: Long
        get() = timeProvider.startTime

    private val sb = StringBuilder()
    private val formatter = Formatter(sb)
    private val timeProviderObserver = { counter: Long -> onStopwatchTick(counter) }

    init {
        timeProvider.observe(fragment.lifecycle, timeProviderObserver)
    }

    fun start(originalStartTime: Long = 0) {
        fragment.binding.stopwatchTextView.visibility = View.VISIBLE
        if (originalStartTime == 0L) {
            timeProvider.startUp()
        } else {
            timeProvider.continueSeamlesslyUp(originalStartTime)
        }
    }

    fun stop() {
        fragment.binding.stopwatchTextView.visibility = View.INVISIBLE
        timeProvider.stop()
    }

    private fun onStopwatchTick(totalSeconds: Long) {
        fragment.binding.stopwatchTextView.text = format(totalSeconds)
        if (totalSeconds > 0 && totalSeconds % 60 == 0L) {
            sounds.beep()
        }
    }

    private fun format(totalSeconds: Long): String {
        val minutes = totalSeconds / 60 % 60
        val seconds = totalSeconds % 60
        sb.setLength(0)
        formatter.format("%02d:%02d", minutes, seconds)
        return sb.toString()
    }
}
