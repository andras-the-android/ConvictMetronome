package hu.kts.cmetronome.ui.workout

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import hu.kts.cmetronome.R
import hu.kts.cmetronome.Settings
import hu.kts.cmetronome.TimeProvider

class Countdowner(context: Context, private val fragment: WorkoutFragment, internal var settings: Settings, private val timeProvider: TimeProvider) {

    private val countDownColor: Int = ContextCompat.getColor(context, R.color.accent)
    private val normalColor: Int = ContextCompat.getColor(context, R.color.secondary_text)
    var onFinish: (() -> Unit)? = null
    var onCancel: (() -> Unit)? = null

    init {
        timeProvider.observe(fragment, Observer { this.onCountDownTick(it ?: 0) })
    }

    private fun onCountDownTick(remainingSeconds: Long) {
        fragment.binding.repCounterTextView.text = remainingSeconds.toString()
        if (remainingSeconds == 0L) {
            fragment.binding.repCounterTextView.setTextColor(normalColor)
            onFinish?.invoke()
        }
    }

    fun start() {
        fragment.binding.repCounterTextView.setTextColor(countDownColor)
        timeProvider.startDown(settings.countdownStartValue)
    }

    fun cancel() {
        timeProvider.stop()
        fragment.binding.repCounterTextView.setTextColor(normalColor)
        onCancel?.invoke()
    }
}
