package hu.kts.cmetronome.repository

import hu.kts.cmetronome.di.AppScope
import hu.kts.cmetronome.ui.Toaster
import javax.inject.Inject

@AppScope
class WorkoutSettingsFactory @Inject constructor(
        private val sharedPreferencesFactory: WorkoutSharedPreferencesFactory,
        private val toaster: Toaster
) {

    private val cache = HashMap<Int, WorkoutSettings>()

    fun create(workoutId: Int): WorkoutSettings {
        if (cache.containsKey(workoutId)) return cache[workoutId]!!

        return WorkoutSettings(sharedPreferencesFactory.create(workoutId), toaster).also {
            cache[workoutId] = it
        }
    }




}