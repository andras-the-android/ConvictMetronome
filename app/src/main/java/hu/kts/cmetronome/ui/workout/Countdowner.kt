package hu.kts.cmetronome.ui.workout

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import hu.kts.cmetronome.R
import hu.kts.cmetronome.Settings
import hu.kts.cmetronome.TimeProvider
import kotlinx.android.synthetic.main.activity_workout.*

class Countdowner(private val activity: AppCompatActivity, internal var settings: Settings, private val timeProvider: TimeProvider, private val onFinish: () -> Unit, private val onCancel: () -> Unit) {

    private val countDownColor: Int = ContextCompat.getColor(activity, R.color.accent)
    private val normalColor: Int = ContextCompat.getColor(activity, R.color.secondary_text)

    init {
        timeProvider.observe(activity, Observer { this.onCountDownTick(it ?: 0) })
    }

    private fun onCountDownTick(remainingSeconds: Long) {
        activity.repCounterTextView.text = remainingSeconds.toString()
        if (remainingSeconds == 0L) {
            activity.repCounterTextView.setTextColor(normalColor)
            onFinish()
        }
    }

    fun start() {
        activity.repCounterTextView.setTextColor(countDownColor)
        timeProvider.startDown(settings.countdownStartValue)
    }

    fun cancel() {
        timeProvider.stop()
        activity.repCounterTextView.setTextColor(normalColor)
        onCancel()
    }
}
