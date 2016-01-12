package hu.kts.cmetronome;

import android.os.Handler;

import hu.kts.cmetronome.functional.IntConsumer;
import hu.kts.cmetronome.functional.SimpleMethod;

/**
 * Created by andrasnemeth on 11/01/16.
 */
public class TimeProvider {

    private IntConsumer callback;
    private SimpleMethod countDownCallback;
    private boolean inProgress;
    private Handler handler = new Handler();
    private int increment;
    private int count;

    public TimeProvider(IntConsumer callback, SimpleMethod countDownCallback) {
        this.callback = callback;
        this.countDownCallback = countDownCallback;
    }

    public void startUp() {
        inProgress = true;
        increment = 1;
        count = 0;
        startCycle();
    }
    public void startDown(int startValue) {
        inProgress = true;
        increment = -1;
        count = startValue;
        startCycle();
    }

    private void startCycle() {
        if (inProgress) {
            if (isCountDownLastRound()) {
                countDownCallback.call();
            } else {
                callback.accept(count);
                count += increment;
                handler.postDelayed(this::startCycle, 1000);
            }
        }
    }

    private boolean isCountDownLastRound() {
        return count == 0 && increment == -1;
    }

    public void stop() {
        inProgress = false;
    }






}
