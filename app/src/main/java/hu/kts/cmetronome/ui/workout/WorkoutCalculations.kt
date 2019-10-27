package hu.kts.cmetronome.ui.workout

import android.content.SharedPreferences
import hu.kts.cmetronome.Settings
import java.util.concurrent.TimeUnit

class WorkoutCalculations(private val settings: Settings) {

    private var millisToIncreaseRepCounter = 0L
    private val directionOrder = arrayOf(IndicatorAnimation.Direction.UP, IndicatorAnimation.Direction.LEFT, IndicatorAnimation.Direction.DOWN, IndicatorAnimation.Direction.RIGHT)
    //we have to hold a reference to this or else it'd be gc-d
    private val listener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key -> onSettingsChanged(key) }
    private var completeRepDuration = 0L

    init {
        settings.addListener(listener)
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

    fun shouldIncreaseRepCounter(count: Long, reps: Int) = convertTicksToMillis(count) == millisToIncreaseRepCounter + reps * completeRepDuration

    private fun onSettingsChanged(key: String?) {
        when (key) {
            Settings.KEY_REP_UP_TIME, Settings.KEY_REP_DOWN_TIME, Settings.KEY_REP_PAUSE_1_TIME, Settings.KEY_REP_PAUSE_2_TIME -> calculateRepData()
        }
    }

    private fun calculateRepData() {
        completeRepDuration = settings.repUpTime + settings.repPause1Time + settings.repDownTime + settings.repPause2Time
        millisToIncreaseRepCounter = calcMillisToIncreaseRepCounter()
    }

    private fun calcMillisToIncreaseRepCounter(): Long {
        var millis = 0L
        for (i in 0..directionOrder.size - 2) {
            millis += getTimeForDirection(directionOrder[i])
        }
        return millis
    }

    private fun convertTicksToMillis(count: Long): Long = (TimeUnit.SECONDS.toMillis(count) / 2)

    private fun getTimeForDirection(direction: IndicatorAnimation.Direction): Long =
            when (direction) {
                IndicatorAnimation.Direction.DOWN -> settings.repDownTime
                IndicatorAnimation.Direction.RIGHT -> settings.repPause1Time
                IndicatorAnimation.Direction.UP -> settings.repUpTime
                IndicatorAnimation.Direction.LEFT -> settings.repPause2Time
            }
}