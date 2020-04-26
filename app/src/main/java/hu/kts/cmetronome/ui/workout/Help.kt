package hu.kts.cmetronome.ui.workout

import android.view.View
import hu.kts.cmetronome.R
import hu.kts.cmetronome.WorkoutStatus
import javax.inject.Inject

class Help @Inject constructor(private val fragment: WorkoutFragment) {

    fun setEnabled(enabled: Boolean) {
        fragment.binding.helpTextView.visibility = if (enabled) View.VISIBLE else View.GONE
    }

    fun setHelpTextByWorkoutStatus(status: WorkoutStatus) {
        fragment.binding.helpTextView.setText(getHelpTextIdByWorkoutStatus(status))
    }

    private fun getHelpTextIdByWorkoutStatus(status: WorkoutStatus): Int {
        return when (status) {
            WorkoutStatus.BEFORE_START -> R.string.help_before_start
            WorkoutStatus.IN_PROGRESS -> R.string.help_in_progress
            WorkoutStatus.PAUSED -> R.string.help_paused
            WorkoutStatus.BETWEEN_SETS -> R.string.help_between_sets
            else -> R.string.empty_string
        }
    }
}
