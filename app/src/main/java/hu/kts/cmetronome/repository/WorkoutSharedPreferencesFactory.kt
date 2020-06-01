package hu.kts.cmetronome.repository

import android.content.Context
import android.content.SharedPreferences
import hu.kts.cmetronome.di.AppContext
import javax.inject.Inject

class WorkoutSharedPreferencesFactory @Inject constructor(
        @AppContext private val context: Context
) {

    fun create(workoutId: Int): SharedPreferences {
        return context.getSharedPreferences(WORKOUT_SETTINGS_PREFERENCE_PREFIX + workoutId, Context.MODE_PRIVATE)
    }

    companion object {
        private const val WORKOUT_SETTINGS_PREFERENCE_PREFIX = "workoutSettings"
    }
}