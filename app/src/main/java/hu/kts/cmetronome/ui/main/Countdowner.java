package hu.kts.cmetronome.ui.main;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hu.kts.cmetronome.R;
import hu.kts.cmetronome.Settings;
import hu.kts.cmetronome.TimeProvider;
import hu.kts.cmetronome.functional.SimpleMethod;

/**
 * Created by andrasnemeth on 25/01/16.
 */
public class Countdowner {

    @InjectView(R.id.rep_counter)
    TextView repCounterTextView;

    private final SimpleMethod callback;
    private final SimpleMethod onCancel;
    private TimeProvider countDownTimeProvider = new TimeProvider(this::onCountDownTick, this::onCountDownFinished);
    Settings settings = Settings.INSTANCE;
    private int countDownColor;
    private int normalColor;

    public Countdowner(Activity activity, SimpleMethod onFinish, SimpleMethod onCancel) {
        this.callback = onFinish;
        this.onCancel = onCancel;
        ButterKnife.inject(this, activity);
        countDownColor = ContextCompat.getColor(activity, R.color.accent);
        normalColor = ContextCompat.getColor(activity, R.color.secondary_text);
    }

    public void onCountDownTick(long remainingInSeconds) {
        repCounterTextView.setText(String.valueOf(remainingInSeconds));
    }

    public void onCountDownFinished() {
        repCounterTextView.setTextColor(normalColor);
        countDownTimeProvider.stop();
        callback.call();
    }

    public void start() {
        repCounterTextView.setTextColor(countDownColor);
        countDownTimeProvider.startDown(settings.getCountdownStartValue());
    }

    public void cancel() {
        countDownTimeProvider.stop();
        repCounterTextView.setTextColor(normalColor);
        onCancel.call();
    }
}
