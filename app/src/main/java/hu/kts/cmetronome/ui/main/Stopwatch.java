package hu.kts.cmetronome.ui.main;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.util.Formatter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hu.kts.cmetronome.R;
import hu.kts.cmetronome.TimeProvider;

/**
 * Created by andrasnemeth on 12/01/16.
 */
public class Stopwatch {

    @InjectView(R.id.stopwarch)
    TextView stopwatchTextView;

    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    private TimeProvider timeProvider = new TimeProvider(this::onStopwatchTick, null);

    public Stopwatch(Activity activity) {
        ButterKnife.inject(this, activity);
    }

    public void start() {
        stopwatchTextView.setVisibility(View.VISIBLE);
        timeProvider.startUp();
    }

    public void start(long originalStartTime) {
        stopwatchTextView.setVisibility(View.VISIBLE);
        timeProvider.continueSeamlesslyUp(originalStartTime);
    }

    public void stop() {
        stopwatchTextView.setVisibility(View.INVISIBLE);
        timeProvider.stop();
    }

    private void onStopwatchTick(long totalSeconds) {
        stopwatchTextView.setText(format(totalSeconds));
    }


    private String format(long totalSeconds) {
        long minutes = (totalSeconds / 60) % 60;
        long seconds = totalSeconds % 60;
        sb.setLength(0);
        formatter.format("%02d:%02d", minutes, seconds);
        return sb.toString();
    }

    public long getStartTime() {
        return timeProvider.getStartTime();
    }
}
