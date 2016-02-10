package hu.kts.cmetronome;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hu.kts.cmetronome.functional.SimpleMethod;

/**
 * Created by andrasnemeth on 25/01/16.
 */
public class Countdowner1 {

    @InjectView(R.id.rep_counter)
    TextView repCounterTextView;

    private final SimpleMethod callback;
    private TimeProvider countDownTimeProvider = new TimeProvider(this::onCountDownTick, this::onCountDownFinished);
    Settings settings = Settings.INSTANCE;
    private int countDownColor;
    private int normalColor;

    public Countdowner1(Activity activity, SimpleMethod callback) {
        this.callback = callback;
        ButterKnife.inject(this, activity);
        countDownColor = ContextCompat.getColor(activity, R.color.accent);
        normalColor = ContextCompat.getColor(activity, R.color.secondary_text);
    }

    public void onCountDownTick(long remainingInSeconds) {
        repCounterTextView.setText(String.valueOf(remainingInSeconds));
    }

    public void onCountDownFinished() {
        repCounterTextView.setTextColor(normalColor);
        callback.call();
    }

    public void start() {
        repCounterTextView.setTextColor(countDownColor);
        countDownTimeProvider.startDown(settings.getCountdownStartValue());
    }

    public void stop() {
        countDownTimeProvider.stop();
    }
}
