package hu.kts.cmetronome.ui.workout

import android.content.Context
import androidx.core.content.ContextCompat
import hu.kts.cmetronome.R
import hu.kts.cmetronome.TimeProvider
import hu.kts.cmetronome.di.AppContext
import hu.kts.cmetronome.di.TimeProviderCountdowner
import hu.kts.cmetronome.di.WorkoutScope
import hu.kts.cmetronome.repository.WorkoutSettings
import javax.inject.Inject

@WorkoutScope
class Countdowner @Inject constructor(
        @AppContext context: Context,
        private val fragment: WorkoutFragment,
        private val settings: WorkoutSettings,
        @TimeProviderCountdowner private val timeProvider: TimeProvider
) {

    var onFinish: (() -> Unit)? = null
    var onCancel: (() -> Unit)? = null

    private val countDownColor: Int = ContextCompat.getColor(context, R.color.accent)
    private val normalColor: Int = ContextCompat.getColor(context, R.color.secondary_text)
    private val timeProviderObserver = { counter: Long -> onCountDownTick(counter) }

    init {
        timeProvider.observe(fragment.lifecycle, timeProviderObserver)
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
