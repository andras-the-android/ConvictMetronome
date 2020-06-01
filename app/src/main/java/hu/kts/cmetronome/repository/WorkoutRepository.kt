package hu.kts.cmetronome.repository

import hu.kts.cmetronome.WorkoutStatus
import hu.kts.cmetronome.di.AppScope
import javax.inject.Inject

@AppScope
class WorkoutRepository @Inject constructor() {

    var repCount = 0
        private set
    var setCount = 0
        private set
    var workoutStatus: WorkoutStatus? = null
    var stopwatchStartTime: Long = 0

    fun increaseRepCounter() {
        ++repCount
    }

    fun increaseSetCounter() {
        ++setCount
    }

    fun resetCounters() {
        repCount = 0
        setCount = 0
        stopwatchStartTime = 0
    }

    fun resetRepCounter() {
        repCount = 0
    }
}
