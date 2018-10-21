package hu.kts.cmetronome.ui.workout

import android.app.Activity
import android.view.View
import hu.kts.cmetronome.R
import hu.kts.cmetronome.WorkoutStatus
import kotlinx.android.synthetic.main.activity_workout.*

class Help(private val activity: Activity) {

    fun setEnabled(enabled: Boolean) {
        activity.helpTextView.visibility = if (enabled) View.VISIBLE else View.GONE
    }

    fun setHelpTextByWorkoutStatus(status: WorkoutStatus) {
        activity.helpTextView.setText(getHelpTextIdByWorkoutStatus(status))
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
