package hu.kts.cmetronome.ui.workout;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.kts.cmetronome.R;
import hu.kts.cmetronome.Settings;
import hu.kts.cmetronome.TimeProvider;
import hu.kts.cmetronome.functional.SimpleMethod;

public class Countdowner {

    @BindView(R.id.rep_counter)
    TextView repCounterTextView;

    private SimpleMethod onFinish;
    private SimpleMethod onCancel;
    private TimeProvider timeProvider;
    Settings settings;
    private int countDownColor;
    private int normalColor;

    public Countdowner(AppCompatActivity activity, Settings settings, TimeProvider timeProvider) {
        this.onCancel = onCancel;
        this.settings = settings;
        this.timeProvider = timeProvider;
        ButterKnife.bind(this, activity);
        countDownColor = ContextCompat.getColor(activity, R.color.accent);
        normalColor = ContextCompat.getColor(activity, R.color.secondary_text);
        timeProvider.observe(activity, this::onCountDownTick);
    }

    public Countdowner setOnFinish(SimpleMethod onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    public Countdowner setOnCancel(SimpleMethod onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    public void onCountDownTick(long remainingSeconds) {
        repCounterTextView.setText(String.valueOf(remainingSeconds));
        if (remainingSeconds == 0) {
            repCounterTextView.setTextColor(normalColor);
            onFinish.call();
        }
    }

    public void start() {
        repCounterTextView.setTextColor(countDownColor);
        timeProvider.startDown(settings.getCountdownStartValue());
    }

    public void cancel() {
        timeProvider.stop();
        repCounterTextView.setTextColor(normalColor);
        onCancel.call();
    }
}
