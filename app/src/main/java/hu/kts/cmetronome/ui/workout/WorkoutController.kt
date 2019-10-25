package hu.kts.cmetronome.ui.workout

import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import hu.kts.cmetronome.*
import hu.kts.cmetronome.repository.WorkoutRepository
import kotlinx.android.synthetic.main.activity_workout.*
import java.util.concurrent.TimeUnit

class WorkoutController(private val activity: AppCompatActivity,
                        private val repository: WorkoutRepository,
                        private val settings: Settings,
                        private val sounds: Sounds,
                        private val timeProviderRep: TimeProvider,
                        timeProviderStopwatch: TimeProvider,
                        timeProviderCountdowner: TimeProvider) : LifecycleObserver {

    private val stopWatch: Stopwatch = Stopwatch(activity, timeProviderStopwatch, sounds)
    private var indicatorAnimation: IndicatorAnimation? = null
    private val help: Help = Help(activity)
    private val countdowner = Countdowner(activity, settings, timeProviderCountdowner, onFinish = this::startWorkout, onCancel = this::onCountdownCancelled)

    private val directionOrder = arrayOf(IndicatorAnimation.Direction.DOWN, IndicatorAnimation.Direction.RIGHT, IndicatorAnimation.Direction.UP, IndicatorAnimation.Direction.LEFT)

    init {
        initWorkoutData()
        initSettingsRelatedParts()
        timeProviderRep.observe(activity, Observer { count -> onRepTimeProviderTick(count) })
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
//        sounds.makeUpSound()
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
        timeProviderRep.stop()
    }


    private fun countDownAndStart() {
        if (settings.repDownTime + settings.repUpTime == 0L) {
            Toast.makeText(activity, activity.getString(R.string.rep_is_empty_message), Toast.LENGTH_SHORT).show()
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

    /**
     * Initialization can't be called from onCreate because animation needs the actual size of the elements
     */
    private fun getIndicatorAnimation(): IndicatorAnimation {
        if (indicatorAnimation == null) {
            indicatorAnimation = IndicatorAnimation(activity, settings)
        }
        return indicatorAnimation!!
    }

    private fun onRepTimeProviderTick(count: Long) {
        getNextDirection(count)?.let {
            Log.d(TAG, "${count / 2} -> $it")
            val animation = getIndicatorAnimation()
            when (it) {
                IndicatorAnimation.Direction.DOWN -> {
                    sounds.makeUpSound()
                    animation.start(IndicatorAnimation.Direction.DOWN)
                }
                IndicatorAnimation.Direction.RIGHT ->
                    animation.start(IndicatorAnimation.Direction.RIGHT)
                IndicatorAnimation.Direction.UP -> {
                    sounds.makeDownSound()
                    animation.start(IndicatorAnimation.Direction.UP)
                }
                IndicatorAnimation.Direction.LEFT -> {
                    animation.start(IndicatorAnimation.Direction.LEFT)
                    repository.increaseRepCounter()
                    fillRepCounterTextViewWithTruncatedData()
                }
            }
        }
    }

    private fun getNextDirection(count: Long): IndicatorAnimation.Direction? {
        val completeRepDuration = settings.repUpTime + settings.repPause1Time + settings.repDownTime + settings.repPause2Time
        val elapsedMilliesInCurrentRep = (TimeUnit.SECONDS.toMillis(count) / 2) % completeRepDuration

        var i = 0
        var nextDirectionChange = 0L
        while (nextDirectionChange < elapsedMilliesInCurrentRep) {
            nextDirectionChange += getTimeForDirection(directionOrder[i++])
        }

        while (i < directionOrder.size && getTimeForDirection(directionOrder[i]) == 0L) ++i

        return when (elapsedMilliesInCurrentRep) {
            nextDirectionChange -> directionOrder[i]
            else -> null
        }
    }

    private fun getTimeForDirection(direction: IndicatorAnimation.Direction): Long =
            when (direction) {
                IndicatorAnimation.Direction.DOWN -> settings.repUpTime
                IndicatorAnimation.Direction.RIGHT -> settings.repPause1Time
                IndicatorAnimation.Direction.UP -> settings.repDownTime
                IndicatorAnimation.Direction.LEFT -> settings.repPause2Time
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

    companion object {
        const val TAG = "WorkoutController"
    }

}
