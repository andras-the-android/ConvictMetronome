package hu.kts.cmetronome.repository;

import hu.kts.cmetronome.WorkoutStatus;

public class WorkoutRepository {

    int repCount = 0;
    int setCount = 0;
    private WorkoutStatus workoutStatus;
    private long stopwatchStartTime;

    public void increaseRepCounter() {
        ++repCount;
    }

    public void increaseSetCounter() {
        ++setCount;
    }

    public void resetCounters() {
        repCount = 0;
        setCount = 0;
        stopwatchStartTime = 0;
    }

    public void resetRepCounter() {
        repCount = 0;
    }

    public int getRepCount() {
        return repCount;
    }

    public int getSetCount() {
        return setCount;
    }

    public WorkoutStatus getWorkoutStatus() {
        return workoutStatus;
    }

    public long getStopwatchStartTime() {
        return stopwatchStartTime;
    }

    public void setWorkoutStatus(WorkoutStatus workoutStatus) {
        this.workoutStatus = workoutStatus;
    }

    public void setStopwatchStartTime(long stopwatchStartTime) {
        this.stopwatchStartTime = stopwatchStartTime;
    }
}
