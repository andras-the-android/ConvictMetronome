package hu.kts.cmetronome.ui.workout

import hu.kts.cmetronome.di.WorkoutScope
import javax.inject.Inject

@WorkoutScope
class Counters @Inject constructor(private val fragment: WorkoutFragment) {

    /**
     * counter should contain maximum 2 digits
     */
    fun setRepCount(count: Int) {
        fragment.binding.repCounterTextView.text = (count % 100).toString()
    }

    /**
     * counter should contain maximum 2 digits
     */
    fun setSetCount(count: Int) {
        fragment.binding.setCounterTextView.text = (count% 100).toString()
    }
}