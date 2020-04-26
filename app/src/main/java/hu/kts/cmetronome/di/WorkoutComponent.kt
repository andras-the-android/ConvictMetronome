package hu.kts.cmetronome.di

import dagger.BindsInstance
import dagger.Subcomponent
import hu.kts.cmetronome.ui.workout.WorkoutFragment

@Subcomponent(modules = [WorkoutModule::class])
interface WorkoutComponent {

    fun inject(fragment: WorkoutFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: WorkoutFragment): WorkoutComponent
    }
}