package hu.kts.cmetronome;

import android.os.Handler;
import android.util.Log;

import hu.kts.cmetronome.architetcture.SingleLiveEvent;

public class TimeProvider extends SingleLiveEvent<Long> {

    public static final int DELAY_MILLIS = 1000;

    private State state = State.STOPPED;
    private Handler handler = new Handler();
    private int countDownStartValue = -1;
    private long count;
    private long startTime;

    public synchronized void startUp() {
        initCount();
    }

    public synchronized void startDown(int startValue) {
        checkCountdownStartValue(startValue);
        countDownStartValue = startValue;
        initCount();
    }

    public synchronized void stop() {
        state = State.STOPPED;
    }

    public synchronized void continueSeamlesslyUp(long originalStartTime) {
        if (state == State.STOPPED) {
            startTime = originalStartTime;
            state = State.IN_PROGRESS;
            startCycle();
        }
    }

    private void initCount() {
        startTime = System.currentTimeMillis();
        state = State.IN_PROGRESS;
        startCycle();
    }

    private void startCycle() {
        if (state == State.IN_PROGRESS) {
            long t = calcCallbackValue();
            setValue(t);
            if (isCountDownLastRound()) {
                state = State.STOPPED;
            } else {
                handler.postDelayed(this::startCycle, getDelayMillis());
            }
        }
    }

    private long getDelayMillis() {
        long timestampOfDesiredNextTick = startTime + ((count + 1) * DELAY_MILLIS);
        return timestampOfDesiredNextTick - System.currentTimeMillis();
    }

    private void checkCountdownStartValue(int startValue) {
        if (startValue < 0) {
            throw new IllegalArgumentException("startValue must be greater or equal to 0");
        }
    }

    private long calcCallbackValue() {
        count = (System.currentTimeMillis() - startTime) / DELAY_MILLIS;
        return isCountDown() ? countDownStartValue - count : count;
    }

    private boolean isCountDown() {
        return countDownStartValue > -1;
    }

    private boolean isCountDownLastRound() {
        return count == countDownStartValue;
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    protected void onActive() {
        //invoked twice somehow
        if (state == State.INACTIVE) {
            state = State.IN_PROGRESS;
            startCycle();
        }
    }

    @Override
    protected void onInactive() {
        if (state == State.IN_PROGRESS) {
            state = State.INACTIVE;
        }
    }

    /**
     * Stopped when someone called stop manually or never started.
     * Inactive when the caller activity is in background but progress
     * will continue automatically when it become to foreground again.
     */
    private enum State {
        STOPPED, INACTIVE, IN_PROGRESS
    }
}
