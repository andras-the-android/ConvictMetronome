package hu.kts.cmetronome.ui.workout

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import hu.kts.cmetronome.R
import hu.kts.cmetronome.TimeProvider
import hu.kts.cmetronome.WorkoutStatus
import hu.kts.cmetronome.di.TimeProviderRep
import hu.kts.cmetronome.logic.WorkoutCalculations
import hu.kts.cmetronome.repository.WorkoutRepository
import hu.kts.cmetronome.repository.WorkoutSettings
import hu.kts.cmetronome.sounds.Sounds
import hu.kts.cmetronome.ui.Toaster
import javax.inject.Inject

class WorkoutController @Inject constructor(private val repository: WorkoutRepository,
                                            private val settings: WorkoutSettings,
                                            private val sounds: Sounds,
                                            @TimeProviderRep private val timeProviderRep: TimeProvider,
                                            private val calculations: WorkoutCalculations,
                                            private val indicatorAnimation: IndicatorAnimation,
                                            private val stopWatch: Stopwatch,
                                            private val help: Help,
                                            private val countdowner: Countdowner,
                                            private val counters: Counters,
                                            private val toaster: Toaster
) : DefaultLifecycleObserver {

    //we have to hold a reference to this or else it'd be gc-d
    private val listener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key -> onSettingsChanged(key) }

    init {
        initWorkoutData()
        timeProviderRep.observeForever { count -> onRepTimeProviderTick(count) }
        settings.addListener(listener)
        help.setEnabled(settings.isShowHelp)
        countdowner.onFinish = this::startWorkout
        countdowner.onCancel = this::onCountdownCancelled
    }

    private fun initWorkoutData() {
        if (repository.workoutStatus != null) {
            val savedWorkoutStatus = repository.workoutStatus
            setWorkoutStatusAndHelpText(if (savedWorkoutStatus == WorkoutStatus.IN_PROGRESS || savedWorkoutStatus == WorkoutStatus.COUNTDOWN_IN_PROGRESS) WorkoutStatus.PAUSED else savedWorkoutStatus!!)
            if (repository.workoutStatus == WorkoutStatus.BETWEEN_SETS) {
                stopWatch.start(repository.stopwatchStartTime)
            }
        } else {
            setWorkoutStatusAndHelpText(WorkoutStatus.BEFORE_START)
        }
        counters.setRepCount(repository.repCount)
        counters.setSetCount(repository.setCount)
    }

    private fun onSettingsChanged(key: String?) {
        when (key) {
            WorkoutSettings.KEY_SHOW_HELP -> help.setEnabled(settings.isShowHelp)
            WorkoutSettings.KEY_REP_STARTS_WITH_UP -> resetIndicator()
        }
    }

    fun onRepCounterClick() {
        when (repository.workoutStatus) {
            WorkoutStatus.BEFORE_START, WorkoutStatus.PAUSED, WorkoutStatus.BETWEEN_SETS -> countDownAndStart()
            WorkoutStatus.COUNTDOWN_IN_PROGRESS, WorkoutStatus.IN_PROGRESS -> pauseWorkout()
        }
    }

    fun onRepCounterLongClick(): Boolean {
        if (repository.workoutStatus == WorkoutStatus.IN_PROGRESS || repository.workoutStatus == WorkoutStatus.PAUSED) {
            stopSet()
            return true
        }
        if (repository.workoutStatus == WorkoutStatus.BETWEEN_SETS) {
            resetWorkout()
            return true
        }
        return false
    }

    private fun pauseWorkout() {
        setWorkoutStatusAndHelpText(WorkoutStatus.PAUSED)
        countdowner.cancel()
        sounds.stop()
        resetIndicator()
    }

    private fun onCountdownCancelled() {
        counters.setRepCount(repository.repCount)
    }

    private fun resetIndicator() {
        indicatorAnimation.stop()
        timeProviderRep.stop()
    }


    private fun countDownAndStart() {
        if (settings.repDownTime + settings.repUpTime == 0L) {
            toaster.showShort(R.string.rep_is_empty_message)
            return
        }
        if (repository.workoutStatus == WorkoutStatus.BETWEEN_SETS) {
            repository.resetRepCounter()
        }
        stopWatch.stop()
        setWorkoutStatusAndHelpText(WorkoutStatus.COUNTDOWN_IN_PROGRESS)
        countdowner.start()
    }

    private fun startWorkout() {
        setWorkoutStatusAndHelpText(WorkoutStatus.IN_PROGRESS)
        counters.setRepCount(repository.repCount)
        timeProviderRep.startUp()
    }

    private fun onRepTimeProviderTick(count: Long) {
        calculations.getNextDirection(count)?.let {
            Log.d(TAG, "${count / 2} -> $it")
            when (it) {
                IndicatorAnimation.Direction.DOWN -> {
                    sounds.makeDownSound()
                    indicatorAnimation.start(IndicatorAnimation.Direction.DOWN)
                }
                IndicatorAnimation.Direction.RIGHT ->
                    indicatorAnimation.start(IndicatorAnimation.Direction.RIGHT)
                IndicatorAnimation.Direction.UP -> {
                    sounds.makeUpSound()
                    indicatorAnimation.start(IndicatorAnimation.Direction.UP)
                }
                IndicatorAnimation.Direction.LEFT -> {
                    indicatorAnimation.start(IndicatorAnimation.Direction.LEFT)
                }
            }

        }
        if (calculations.shouldIncreaseRepCounter(count)) {
            repository.increaseRepCounter()
            counters.setRepCount(repository.repCount)
        }
    }

    private fun stopSet() {
        setWorkoutStatusAndHelpText(WorkoutStatus.BETWEEN_SETS)
        resetIndicator()
        sounds.stop()
        countdowner.cancel()
        stopWatch.start()
        repository.stopwatchStartTime = stopWatch.startTime
        increaseSetCounter()
    }

    private fun setWorkoutStatusAndHelpText(status: WorkoutStatus) {
        repository.workoutStatus = status
        help.setHelpTextByWorkoutStatus(status)
    }

    private fun increaseSetCounter() {
        repository.increaseSetCounter()
        counters.setSetCount(repository.setCount)
    }


    private fun resetWorkout() {
        setWorkoutStatusAndHelpText(WorkoutStatus.BEFORE_START)
        stopWatch.stop()
        repository.resetCounters()
        counters.setSetCount(repository.setCount)
        counters.setRepCount(repository.repCount)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        stopWatch.stop()
        countdowner.cancel()
    }

    override fun onPause(owner: LifecycleOwner) {
        if (repository.workoutStatus == WorkoutStatus.IN_PROGRESS || repository.workoutStatus == WorkoutStatus.COUNTDOWN_IN_PROGRESS) {
            pauseWorkout()
        }
    }

    companion object {
        const val TAG = "WorkoutController"
    }

}
