package hu.kts.cmetronome.ui.workout;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.kts.cmetronome.R;
import hu.kts.cmetronome.Settings;
import hu.kts.cmetronome.TimeProvider;
import hu.kts.cmetronome.functional.SimpleMethod;

/**
 * Created by andrasnemeth on 25/01/16.
 */
public class Countdowner {

    @BindView(R.id.rep_counter)
    TextView repCounterTextView;

    private final SimpleMethod onFinish;
    private final SimpleMethod onCancel;
    private TimeProvider countDownTimeProvider = new TimeProvider(this::onCountDownTick, this::onCountDownFinished);
    Settings settings;
    private int countDownColor;
    private int normalColor;

    public Countdowner(Activity activity, SimpleMethod onFinish, SimpleMethod onCancel, Settings settings) {
        this.onFinish = onFinish;
        this.onCancel = onCancel;
        this.settings = settings;
        ButterKnife.bind(this, activity);
        countDownColor = ContextCompat.getColor(activity, R.color.accent);
        normalColor = ContextCompat.getColor(activity, R.color.secondary_text);
    }

    public void onCountDownTick(long remainingInSeconds) {
        repCounterTextView.setText(String.valueOf(remainingInSeconds));
    }

    public void onCountDownFinished() {
        repCounterTextView.setTextColor(normalColor);
        countDownTimeProvider.stop();
        onFinish.call();
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
