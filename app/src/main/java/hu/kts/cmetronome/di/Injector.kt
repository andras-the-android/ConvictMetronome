package hu.kts.cmetronome.di


import android.app.Application

import hu.kts.cmetronome.Settings
import hu.kts.cmetronome.Sounds
import hu.kts.cmetronome.TimeProvider
import hu.kts.cmetronome.repository.WorkoutRepository
import hu.kts.cmetronome.ui.workout.*

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
        sounds = Sounds(settings)
        timeProviderRep = TimeProvider(500)
        timeProviderStopwatch = TimeProvider()
        timeProviderCountdowner = TimeProvider()
    }

    fun inject(activity: WorkoutActivity) {
        val calculations = WorkoutCalculations(settings)
        val indicatorAnimation = IndicatorAnimation(activity, settings)
        val stopWatch = Stopwatch(activity, timeProviderStopwatch, sounds)
        val help = Help(activity)
        val countdowner = Countdowner(activity, settings, timeProviderCountdowner)
        activity.workoutController = WorkoutController(activity, workoutRepository, settings, sounds, timeProviderRep, calculations, indicatorAnimation, stopWatch, help, countdowner)
    }

}
