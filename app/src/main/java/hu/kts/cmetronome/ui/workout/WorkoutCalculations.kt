package hu.kts.cmetronome.ui.workout

import hu.kts.cmetronome.Settings
import java.util.concurrent.TimeUnit

class WorkoutCalculations(private val settings: Settings) {

    private val directionOrder = arrayOf(IndicatorAnimation.Direction.DOWN, IndicatorAnimation.Direction.RIGHT, IndicatorAnimation.Direction.UP, IndicatorAnimation.Direction.LEFT)

    fun getNextDirection(count: Long): IndicatorAnimation.Direction? {
        val completeRepDuration = settings.repUpTime + settings.repPause1Time + settings.repDownTime + settings.repPause2Time
        val elapsedMillisInCurrentRep = (TimeUnit.SECONDS.toMillis(count) / 2) % completeRepDuration

        var i = 0
        var nextDirectionChange = 0L
        while (nextDirectionChange < elapsedMillisInCurrentRep) {
            nextDirectionChange += getTimeForDirection(directionOrder[i++])
        }

        while (i < directionOrder.size && getTimeForDirection(directionOrder[i]) == 0L) ++i

        return when (elapsedMillisInCurrentRep) {
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
}