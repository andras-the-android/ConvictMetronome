package hu.kts.cmetronome.di


import android.app.Application
import android.content.Context

import hu.kts.cmetronome.Settings
import hu.kts.cmetronome.Sounds
import hu.kts.cmetronome.TimeProvider
import hu.kts.cmetronome.repository.WorkoutRepository
import hu.kts.cmetronome.ui.workout.*

object Injector {

    lateinit var settings: Settings
    lateinit var appContext: Context

    private lateinit var workoutRepository: WorkoutRepository
    private lateinit var sounds: Sounds
    private lateinit var timeProviderRep: TimeProvider
    private lateinit var timeProviderStopwatch: TimeProvider
    private lateinit var timeProviderCountdowner: TimeProvider

    fun init(context: Application) {
        settings = Settings(context)
        appContext = context
        workoutRepository = WorkoutRepository()
        sounds = Sounds(settings)
        timeProviderRep = TimeProvider(500)
        timeProviderStopwatch = TimeProvider()
        timeProviderCountdowner = TimeProvider()
    }

    fun inject(fragment: WorkoutFragment) {
        val calculations = WorkoutCalculations(settings)
        val indicatorAnimation = IndicatorAnimation(fragment, settings)
        val stopWatch = Stopwatch(fragment, timeProviderStopwatch, sounds)
        val help = Help(fragment)
        val countdowner = Countdowner(appContext, fragment, settings, timeProviderCountdowner)
        fragment.workoutController = WorkoutController(appContext, fragment, workoutRepository, settings, sounds, timeProviderRep, calculations, indicatorAnimation, stopWatch, help, countdowner)
        fragment.whatsNew = WhatsNew(fragment, settings)
    }

}
