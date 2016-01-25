package hu.kts.cmetronome;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.util.Formatter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by andrasnemeth on 12/01/16.
 */
public class StopWatch {

    @InjectView(R.id.stopwarch)
    TextView stopwatchTextView;

    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    private TimeProvider stopwatchTimeProvider = new TimeProvider(this::onStopwatchTick, null);

    public StopWatch(Activity activity) {
        ButterKnife.inject(this, activity);
    }

    public void start() {
        stopwatchTextView.setVisibility(View.VISIBLE);
        stopwatchTimeProvider.startUp();
    }

    public void stop() {
        stopwatchTextView.setVisibility(View.INVISIBLE);
        stopwatchTimeProvider.stop();
    }

    private void onStopwatchTick(int totalSeconds) {
        stopwatchTextView.setText(format(totalSeconds));
    }


    private String format(int totalSeconds) {
        int minutes = (totalSeconds / 60) % 60;
        int seconds = totalSeconds % 60;
        sb.setLength(0);
        formatter.format("%02d:%02d", minutes, seconds);
        return sb.toString();
    }

}
