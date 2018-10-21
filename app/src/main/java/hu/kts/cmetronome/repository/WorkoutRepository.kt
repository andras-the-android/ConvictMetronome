package hu.kts.cmetronome.repository

import hu.kts.cmetronome.WorkoutStatus

class WorkoutRepository {

    var repCount = 0
        internal set
    var setCount = 0
        internal set
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
