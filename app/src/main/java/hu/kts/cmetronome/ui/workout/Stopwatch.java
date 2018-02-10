package hu.kts.cmetronome.ui.workout;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.Formatter;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.kts.cmetronome.Log;
import hu.kts.cmetronome.R;
import hu.kts.cmetronome.Sounds;
import hu.kts.cmetronome.TimeProvider;

public class Stopwatch {

    @BindView(R.id.stopwarch)
    TextView stopwatchTextView;

    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    private TimeProvider timeProvider;
    private final Sounds sounds;

    public Stopwatch(AppCompatActivity activity, TimeProvider timeProvider, Sounds sounds) {
        this.timeProvider = timeProvider;
        this.sounds = sounds;
        ButterKnife.bind(this, activity);
        timeProvider.observe(activity, this::onStopwatchTick);
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
        if (totalSeconds > 0 && totalSeconds % 60 == 0) {
            sounds.beep();
        }
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
