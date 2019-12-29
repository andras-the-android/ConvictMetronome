package hu.kts.cmetronome.ui.workout

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import hu.kts.cmetronome.Sounds
import hu.kts.cmetronome.TimeProvider
import kotlinx.android.synthetic.main.fragment_workout.*
import java.util.*

class Stopwatch(private val fragement: Fragment, private val timeProvider: TimeProvider, private val sounds: Sounds) {

    private val sb = StringBuilder()
    private val formatter = Formatter(sb)

    val startTime: Long
        get() = timeProvider.startTime

    init {
        timeProvider.observe(fragement, Observer { this.onStopwatchTick(it ?: 0) })
    }

    fun start(originalStartTime: Long = 0) {
        fragement.stopwatchTextView.visibility = View.VISIBLE
        if (originalStartTime == 0L) {
            timeProvider.startUp()
        } else {
            timeProvider.continueSeamlesslyUp(originalStartTime)
        }
    }

    fun stop() {
        fragement.stopwatchTextView.visibility = View.INVISIBLE
        timeProvider.stop()
    }

    private fun onStopwatchTick(totalSeconds: Long) {
        fragement.stopwatchTextView.text = format(totalSeconds)
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
