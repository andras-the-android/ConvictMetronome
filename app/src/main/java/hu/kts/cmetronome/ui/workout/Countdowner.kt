package hu.kts.cmetronome.ui.workout

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import hu.kts.cmetronome.R
import hu.kts.cmetronome.Settings
import hu.kts.cmetronome.TimeProvider
import kotlinx.android.synthetic.main.fragment_workout.*

class Countdowner(context: Context, private val fragment: Fragment, internal var settings: Settings, private val timeProvider: TimeProvider) {

    private val countDownColor: Int = ContextCompat.getColor(context, R.color.accent)
    private val normalColor: Int = ContextCompat.getColor(context, R.color.secondary_text)
    var onFinish: (() -> Unit)? = null
    var onCancel: (() -> Unit)? = null

    init {
        timeProvider.observe(fragment, Observer { this.onCountDownTick(it ?: 0) })
    }

    private fun onCountDownTick(remainingSeconds: Long) {
        fragment.repCounterTextView.text = remainingSeconds.toString()
        if (remainingSeconds == 0L) {
            fragment.repCounterTextView.setTextColor(normalColor)
            onFinish?.invoke()
        }
    }

    fun start() {
        fragment.repCounterTextView.setTextColor(countDownColor)
        timeProvider.startDown(settings.countdownStartValue)
    }

    fun cancel() {
        timeProvider.stop()
        fragment.repCounterTextView.setTextColor(normalColor)
        onCancel?.invoke()
    }
}
