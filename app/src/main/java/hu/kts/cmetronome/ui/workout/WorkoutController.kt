package hu.kts.cmetronome.ui.workout

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.support.v7.app.AppCompatActivity
import hu.kts.cmetronome.Settings
import hu.kts.cmetronome.Sounds
import hu.kts.cmetronome.TimeProvider
import hu.kts.cmetronome.WorkoutStatus
import hu.kts.cmetronome.repository.WorkoutRepository
import kotlinx.android.synthetic.main.activity_workout.*

class WorkoutController(private val activity: AppCompatActivity,
                        private val repository: WorkoutRepository,
                        private val settings: Settings,
                        private val sounds: Sounds,
                        timeProviderStopwatch: TimeProvider,
                        timeProviderCountdowner: TimeProvider) : LifecycleObserver {

    private val stopWatch: Stopwatch = Stopwatch(activity, timeProviderStopwatch, sounds)
    private var indicatorAnimation: IndicatorAnimation? = null
    private val help: Help = Help(activity)
    private val countdowner = Countdowner(activity, settings, timeProviderCountdowner, onFinish = this::startWorkout, onCancel = this::onCountdownCancelled)

    init {
        initWorkoutData()
        initSettingsRelatedParts()
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
        getIndicatorAnimation().stop()
    }


    private fun countDownAndStart() {
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
        getIndicatorAnimation().start()
    }

    /**
     * Initialization can't be called from onCreate because animation needs the actual size of the elements
     */
    private fun getIndicatorAnimation(): IndicatorAnimation {
        if (indicatorAnimation == null) {
            indicatorAnimation = IndicatorAnimation(activity, this::onIndicatorAnimationEvent)
        }
        return indicatorAnimation!!
    }

    private fun onIndicatorAnimationEvent(event: IndicatorAnimation.Event) {
        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (event) {
            IndicatorAnimation.Event.DOWN -> sounds.makeUpSound()
            IndicatorAnimation.Event.UP -> sounds.makeDownSound()
            IndicatorAnimation.Event.LEFT -> {
                repository.increaseRepCounter()
                fillRepCounterTextViewWithTruncatedData()
            }
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

    fun initSettingsRelatedParts() {
        help.setEnabled(settings.isShowHelp)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        stopWatch.stop()
        countdowner.cancel()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        if (repository.workoutStatus == WorkoutStatus.IN_PROGRESS || repository.workoutStatus == WorkoutStatus.COUNTDOWN_IN_PROGRESS) {
            pauseWorkout()
        }
    }

    /**
     * counter should contain maximum 2 digits
     */
    private fun fillRepCounterTextViewWithTruncatedData() {
        activity.repCounterTextView.text = (repository.repCount % 100).toString()
    }

    /**
     * counter should contain maximum 2 digits
     */
    private fun fillSetCounterTextViewWithTruncatedData() {
        activity.setCounterTextView.text = (repository.setCount % 100).toString()
    }

}
