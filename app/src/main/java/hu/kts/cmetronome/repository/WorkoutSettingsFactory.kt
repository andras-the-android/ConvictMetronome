package hu.kts.cmetronome.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import hu.kts.cmetronome.di.AppContext
import hu.kts.cmetronome.di.AppScope
import hu.kts.cmetronome.ui.Toaster
import javax.inject.Inject

@AppScope
class WorkoutSettingsFactory @Inject constructor(
        @AppContext private val context: Context,
        private val toaster: Toaster
) {

    private val cache = HashMap<Int, WorkoutSettings>()

    fun create(workoutId: Int): WorkoutSettings {
        if (cache.containsKey(workoutId)) return cache[workoutId]!!

        val sharedPreferences = context.getSharedPreferences(WORKOUT_SETTINGS_PREFERENCE_PREFIX + workoutId, MODE_PRIVATE)

        return WorkoutSettings(sharedPreferences, toaster).also {
            cache[workoutId] = it
        }
    }

    companion object {
        private const val WORKOUT_SETTINGS_PREFERENCE_PREFIX = "workoutSettings"
    }


}