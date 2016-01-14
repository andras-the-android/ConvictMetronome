package hu.kts.cmetronome;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @InjectView(R.id.metronome)
    View metronomeView;
    @InjectView(R.id.metronome_indicator)
    View indicatorView;
    @InjectView(R.id.rep_counter)
    TextView repCounterTextView;
    @InjectView(R.id.help)
    TextView helpTextView;
    @InjectView(R.id.stopwarch)
    TextView stopwatchTextView;

    int repCount = 0;
    private WorkoutStatus workoutStatus = WorkoutStatus.BEFORE_START;
    private TimeProvider countDownTimeProvider = new TimeProvider(this::onCountDownTick, this::onCountDownFinished);
    private TimeProvider stopwatchTimeProvider = new TimeProvider(this::onStopwatchTick, null);
    private StopwatchFormatter stopwatchFormatter = new StopwatchFormatter();
    private Sounds sounds;
    private IndicatorAnimation indicatorAnimation;

    private IndicatorAnimationCallback indicatorAnimationCallback = new IndicatorAnimationCallback() {
        @Override
        public void onDownStarted() {
            sounds.makeUpSound();
        }

        @Override
        public void onRightStarted() {

        }

        @Override
        public void onUpStarted() {
            sounds.makeDownSound();
        }

        @Override
        public void onLeftStarted() {

        }

        @Override
        public void cycleFinished() {
            ++repCount;
            repCounterTextView.setText(String.valueOf(repCount));
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.inject(this);
        sounds = new Sounds(this);
    }

    @OnClick(R.id.rep_counter)
    public void onRepCounterClick(View view) {
        Log.d(TAG, "onRepCounterClick: " + workoutStatus);
        switch (workoutStatus) {
            case BEFORE_START: countDownAndStart(); break;
            case IN_PROGRESS: pauseWorkout(); break;
            case PAUSED: countDownAndStart(); break;
        }
    }

    private void pauseWorkout() {
        workoutStatus = WorkoutStatus.PAUSED;
        resetIndicator();
        helpTextView.setText(R.string.help_paused);
    }

    private void resetIndicator() {
        indicatorAnimation.stop();
        sounds.stop();
    }

    private void startStopWatch() {
        stopwatchTextView.setVisibility(View.VISIBLE);
        stopwatchTimeProvider.startUp();
    }

    private void stopStopWatch() {
        stopwatchTextView.setVisibility(View.INVISIBLE);
        stopwatchTimeProvider.stop();
    }

    public void onStopwatchTick(int totalSeconds) {
        stopwatchTextView.setText(stopwatchFormatter.format(totalSeconds));
    }

    private void countDownAndStart() {
        stopStopWatch();
        helpTextView.setText("");
        workoutStatus = WorkoutStatus.COUNTDOWN_IN_PROGRESS;
        repCounterTextView.setTextColor(ContextCompat.getColor(this, R.color.accent));
        countDownTimeProvider.startDown(3);
    }

    public void onCountDownTick(int remainingInSeconds) {
        repCounterTextView.setText(String.valueOf(remainingInSeconds));
    }

    public void onCountDownFinished() {
        startWorkout();
    }

    private void startWorkout() {
        repCounterTextView.setText(String.valueOf(repCount));
        helpTextView.setText(R.string.help_in_progress);
        workoutStatus = WorkoutStatus.IN_PROGRESS;
        repCounterTextView.setTextColor(getResources().getColor(R.color.secondary_text));
        repCounterTextView.setText("0");
        getIndicatorAnimation().start();
    }

    private IndicatorAnimation getIndicatorAnimation() {
        if (indicatorAnimation == null) {
            indicatorAnimation = new IndicatorAnimation(this, metronomeView, indicatorView, indicatorAnimationCallback);
        }
        return indicatorAnimation;
    }

    @OnLongClick(R.id.rep_counter)
    public boolean onRepCounterLongClick(View view) {
        resetWorkout();
        return true;
    }

    private void resetWorkout() {
        workoutStatus = WorkoutStatus.BEFORE_START;
        resetIndicator();
        countDownTimeProvider.stop();
        startStopWatch();
        repCount = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sounds.release();
    }

    private enum WorkoutStatus {
        BEFORE_START, IN_PROGRESS, COUNTDOWN_IN_PROGRESS, PAUSED;
    }

}
