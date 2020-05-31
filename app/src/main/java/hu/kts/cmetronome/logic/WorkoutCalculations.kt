package hu.kts.cmetronome.logic

import android.content.SharedPreferences
import hu.kts.cmetronome.di.WorkoutScope
import hu.kts.cmetronome.repository.WorkoutSettings
import hu.kts.cmetronome.ui.workout.IndicatorAnimation
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@WorkoutScope
class WorkoutCalculations @Inject constructor(private val settings: WorkoutSettings) {

    private var millisToIncreaseRepCounter = 0L
    private var directionOrder: Array<IndicatorAnimation.Direction>
    //we have to hold a reference to this or else it'd be gc-d
    private val listener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key -> onSettingsChanged(key) }
    private var completeRepDuration = 0L

    init {
        settings.addListener(listener)
        directionOrder = if (settings.repStartsWithUp) directionOrderUp else directionOrderDown
        calculateRepData()
    }

    fun getNextDirection(count: Long): IndicatorAnimation.Direction? {
        val elapsedMillisInCurrentRep = convertTicksToMillis(count) % completeRepDuration

        var i = 0
        var nextDirectionChange = 0L
        while (nextDirectionChange < elapsedMillisInCurrentRep) {
            nextDirectionChange += getTimeForDirection(directionOrder[i++])
        }

        //step through the directions with 0 time
        while (i < directionOrder.size && getTimeForDirection(directionOrder[i]) == 0L) ++i

        return when (elapsedMillisInCurrentRep) {
            nextDirectionChange -> directionOrder[i]
            else -> null
        }
    }

    fun shouldIncreaseRepCounter(count: Long): Boolean {
        //if millisToIncreaseRepCounter is 0, that means that we only start incrementing in the second round
        if (count == 0L) return false
        return convertTicksToMillis(count) % completeRepDuration == millisToIncreaseRepCounter
    }

    private fun onSettingsChanged(key: String?) {
        when (key) {
            WorkoutSettings.KEY_REP_UP_TIME, WorkoutSettings.KEY_REP_DOWN_TIME, WorkoutSettings.KEY_REP_PAUSE_UP_TIME, WorkoutSettings.KEY_REP_PAUSE_DOWN_TIME, WorkoutSettings.KEY_REP_STARTS_WITH_UP -> calculateRepData()
        }
    }

    private fun calculateRepData() {
        completeRepDuration = settings.repUpTime + settings.repPauseUpTime + settings.repDownTime + settings.repPauseDownTime
        directionOrder = if (settings.repStartsWithUp) directionOrderUp else directionOrderDown
        millisToIncreaseRepCounter = calcMillisToIncreaseRepCounter()
    }

    private fun calcMillisToIncreaseRepCounter(): Long {
        var millis = 0L
        for (i in 0..directionOrder.size - 2) {
            millis += getTimeForDirection(directionOrder[i])
        }
        //this means that in this case we'll increase rep count on the beginning of the next rep
        if (millis == completeRepDuration) millis = 0L
        return millis
    }

    private fun convertTicksToMillis(count: Long): Long = (TimeUnit.SECONDS.toMillis(count) / 2)

    private fun getTimeForDirection(direction: IndicatorAnimation.Direction): Long =
            when (direction) {
                IndicatorAnimation.Direction.DOWN -> settings.repDownTime
                IndicatorAnimation.Direction.RIGHT -> settings.repPauseDownTime
                IndicatorAnimation.Direction.UP -> settings.repUpTime
                IndicatorAnimation.Direction.LEFT -> settings.repPauseUpTime
            }

    companion object {
        val directionOrderUp = arrayOf(IndicatorAnimation.Direction.UP, IndicatorAnimation.Direction.LEFT, IndicatorAnimation.Direction.DOWN, IndicatorAnimation.Direction.RIGHT)
        val directionOrderDown = arrayOf(IndicatorAnimation.Direction.DOWN, IndicatorAnimation.Direction.RIGHT, IndicatorAnimation.Direction.UP, IndicatorAnimation.Direction.LEFT)
    }
}