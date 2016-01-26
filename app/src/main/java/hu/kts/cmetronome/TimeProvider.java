package hu.kts.cmetronome;

import android.os.Handler;
import android.util.Log;

import hu.kts.cmetronome.functional.LongConsumer;
import hu.kts.cmetronome.functional.SimpleMethod;

/**
 * Created by andrasnemeth on 11/01/16.
 */
public class TimeProvider {

    public static final int DELAY_MILLIS = 1000;
    private static final String TAG = "TimeProvider";


    private LongConsumer callback;
    private SimpleMethod countDownCallback;
    private boolean inProgress;
    private Handler handler = new Handler();
    private int countDownStartValue = -1;
    private long count;
    private long startTime;

    public TimeProvider(LongConsumer callback, SimpleMethod countDownCallback) {
        this.callback = callback;
        this.countDownCallback = countDownCallback;
    }

    public synchronized void startUp() {
        initCount();
    }

    private void initCount() {
        checkInProgress();
        startTime = System.currentTimeMillis();
        inProgress = true;
        count = 0;
        startCycle();
    }

    public synchronized void startDown(int startValue) {
        checkCountdownStartValue(startValue);
        countDownStartValue = startValue;
        initCount();
    }

    public synchronized void stop() {
        inProgress = false;
    }

    public synchronized void continueSeamlesslyUp(long originalStartTime) {
        checkInProgress();
        startTime = originalStartTime;
        long timeSinceOriginalStart = System.currentTimeMillis() - originalStartTime;
        count = timeSinceOriginalStart / DELAY_MILLIS;
        inProgress = true;
        startCycle();
    }

    private void startCycle() {
        if (inProgress) {
            if (isCountDownLastRound()) {
                countDownCallback.call();
            } else {
                callback.accept(calcCallbackValue());
                handler.postDelayed(this::startCycle, getDelayMillis());
                ++count;
            }
        }
    }

    private long getDelayMillis() {
        long timestampOfDesiredNextTick = startTime + ((count + 1) * DELAY_MILLIS);
        long result = timestampOfDesiredNextTick - System.currentTimeMillis();
        Log.d(TAG, "getDelayMillis: " + result);
        return result;
    }

    private void checkInProgress() {
        if (inProgress) {
            throw new IllegalStateException("Time provider already running");
        }
    }

    private void checkCountdownStartValue(int startValue) {
        if (startValue < 0) {
            throw new IllegalArgumentException("startValue must be greater or equal to 0");
        }
    }

    private long calcCallbackValue() {
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
}
