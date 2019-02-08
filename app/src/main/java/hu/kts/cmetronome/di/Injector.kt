package hu.kts.cmetronome.di


import android.app.Application

import hu.kts.cmetronome.Settings
import hu.kts.cmetronome.Sounds
import hu.kts.cmetronome.TimeProvider
import hu.kts.cmetronome.repository.WorkoutRepository
import hu.kts.cmetronome.ui.workout.WorkoutActivity
import hu.kts.cmetronome.ui.workout.WorkoutController

object Injector {

    lateinit var settings: Settings

    private lateinit var workoutRepository: WorkoutRepository
    private lateinit var sounds: Sounds
    private lateinit var timeProviderRep: TimeProvider
    private lateinit var timeProviderStopwatch: TimeProvider
    private lateinit var timeProviderCountdowner: TimeProvider

    fun init(context: Application) {
        settings = Settings(context)
        workoutRepository = WorkoutRepository()
        sounds = Sounds(context, settings)
        timeProviderRep = TimeProvider(500)
        timeProviderStopwatch = TimeProvider()
        timeProviderCountdowner = TimeProvider()
    }

    fun inject(workoutActivity: WorkoutActivity) {
        workoutActivity.workoutController = WorkoutController(workoutActivity, workoutRepository, settings, sounds, timeProviderRep, timeProviderStopwatch, timeProviderCountdowner)
    }

}
