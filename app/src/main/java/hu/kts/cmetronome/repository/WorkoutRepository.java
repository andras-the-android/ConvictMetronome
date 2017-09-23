package hu.kts.cmetronome.repository;

import hu.kts.cmetronome.WorkoutStatus;
import lombok.Getter;
import lombok.Setter;

public class WorkoutRepository {

    @Getter int repCount = 0;
    @Getter int setCount = 0;
    @Getter @Setter private WorkoutStatus workoutStatus;
    @Getter @Setter private long stopwatchStartTime;

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
}
