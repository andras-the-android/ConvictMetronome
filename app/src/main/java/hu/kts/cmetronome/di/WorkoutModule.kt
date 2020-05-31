package hu.kts.cmetronome.di

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import hu.kts.cmetronome.repository.WorkoutSettings
import hu.kts.cmetronome.repository.WorkoutSettingsFactory
import hu.kts.cmetronome.ui.workout.WorkoutFragment

@Module
object WorkoutModule {

    @Provides
    fun fragment(workoutFragment: WorkoutFragment): Fragment = workoutFragment

    @Provides
    @WorkoutScope
    fun workoutSettings(@WorkoutId workoutId: Int, factory: WorkoutSettingsFactory): WorkoutSettings {
        return factory.create(workoutId)
    }
}