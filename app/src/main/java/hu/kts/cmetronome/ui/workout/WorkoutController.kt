package hu.kts.cmetronome.ui.workout

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import hu.kts.cmetronome.*
import hu.kts.cmetronome.repository.WorkoutRepository
import kotlinx.android.synthetic.main.fragment_workout.*

//TODO remove android dependencies
class WorkoutController(private val appContext: Context,
                        private val fragment: Fragment,
                        private val repository: WorkoutRepository,
                        private val settings: Settings,
                        private val sounds: Sounds,
                        private val timeProviderRep: TimeProvider,
                        private val calculations: WorkoutCalculations,
                        private val indicatorAnimation: IndicatorAnimation,
                        private val stopWatch: Stopwatch,
                        private val help: Help,
                        private val countdowner: Countdowner) : DefaultLifecycleObserver {

    //we have to hold a reference to this or else it'd be gc-d
    private val listener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key -> onSettingsChanged(key) }

    init {
        initWorkoutData()
        timeProviderRep.observe(fragment, Observer { count -> onRepTimeProviderTick(count) })
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
        fillRepCounterTextViewWithTruncatedData()
        fillSetCounterTextViewWithTruncatedData()
    }

    private fun onSettingsChanged(key: String?) {
        when (key) {
            Settings.KEY_SHOW_HELP -> help.setEnabled(settings.isShowHelp)
            Settings.KEY_REP_STARTS_WITH_UP -> resetIndicator()
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
        fillRepCounterTextViewWithTruncatedData()
    }

    private fun resetIndicator() {
        indicatorAnimation.stop()
        timeProviderRep.stop()
    }


    private fun countDownAndStart() {
        if (settings.repDownTime + settings.repUpTime == 0L) {
            Toast.makeText(appContext, appContext.resources.getString(R.string.rep_is_empty_message), Toast.LENGTH_SHORT).show()
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
        fillRepCounterTextViewWithTruncatedData()
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
            fillRepCounterTextViewWithTruncatedData()
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
        fillSetCounterTextViewWithTruncatedData()
    }


    private fun resetWorkout() {
        setWorkoutStatusAndHelpText(WorkoutStatus.BEFORE_START)
        stopWatch.stop()
        repository.resetCounters()
        fillSetCounterTextViewWithTruncatedData()
        fillRepCounterTextViewWithTruncatedData()
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

    /**
     * counter should contain maximum 2 digits
     */
    private fun fillRepCounterTextViewWithTruncatedData() {
        fragment.repCounterTextView.text = (repository.repCount % 100).toString()
    }

    /**
     * counter should contain maximum 2 digits
     */
    private fun fillSetCounterTextViewWithTruncatedData() {
        fragment.setCounterTextView.text = (repository.setCount % 100).toString()
    }

    companion object {
        const val TAG = "WorkoutController"
    }

}
