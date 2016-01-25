package hu.kts.cmetronome;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import hu.kts.cmetronome.functional.AdMobTestDeviceFilteredBuilderFactory;

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
    @InjectView(R.id.set_counter)
    TextView setCounterTextView;
    @InjectView(R.id.adView)
    AdView adView;

    int repCount = 0;
    int setCount = 0;
    private WorkoutStatus workoutStatus;
    private TimeProvider countDownTimeProvider = new TimeProvider(this::onCountDownTick, this::onCountDownFinished);
    private TimeProvider stopwatchTimeProvider = new TimeProvider(this::onStopwatchTick, null);
    private StopwatchFormatter stopwatchFormatter = new StopwatchFormatter();
    private Sounds sounds;
    private IndicatorAnimation indicatorAnimation;
    private Settings settings;

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
            fillRepCounterTextViewWithTruncatedData();
        }
    };

    private void fillRepCounterTextViewWithTruncatedData() {
        repCounterTextView.setText(String.valueOf(repCount % 100));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.inject(this);
        sounds = new Sounds(this);
        settings = Settings.INSTANCE;
        initSettingsRelatedParts();
        setWorkoutStatusAndHelpText(WorkoutStatus.BEFORE_START);
        setupAd();
    }

    private void setupAd() {
        AdRequest adRequest = AdMobTestDeviceFilteredBuilderFactory.get().build();
        adView.loadAd(adRequest);
    }

    @OnClick(R.id.rep_counter)
    public void onRepCounterClick(View view) {
        Log.d(TAG, "onRepCounterClick: " + workoutStatus);
        switch (workoutStatus) {
            case BEFORE_START:
            case PAUSED:
            case BETWEEN_SETS: countDownAndStart(); break;
            case IN_PROGRESS: pauseWorkout(); break;
        }
    }

    private void pauseWorkout() {
        setWorkoutStatusAndHelpText(WorkoutStatus.PAUSED);
        resetIndicator();
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
        setWorkoutStatusAndHelpText(WorkoutStatus.COUNTDOWN_IN_PROGRESS);
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
        setWorkoutStatusAndHelpText(WorkoutStatus.IN_PROGRESS);
        repCounterTextView.setTextColor(ContextCompat.getColor(this, R.color.secondary_text));
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
        if (workoutStatus == WorkoutStatus.IN_PROGRESS || workoutStatus == WorkoutStatus.PAUSED) {
            stopSet();
            return true;
        } if (workoutStatus == WorkoutStatus.BETWEEN_SETS) {
            resetWorkout();
            return true;
        }
        return false;
    }

    private void stopSet() {
        setWorkoutStatusAndHelpText(WorkoutStatus.BETWEEN_SETS);
        resetIndicator();
        countDownTimeProvider.stop();
        startStopWatch();
        repCount = 0;
        increaseSetCounter();
    }

    private void setWorkoutStatusAndHelpText(WorkoutStatus status) {
        workoutStatus = status;
        helpTextView.setText(getHelpTextIdByWorkoutStatus(status));
    }

    private void increaseSetCounter() {
        ++setCount;
        //setcounter schould contain maximum 2 digits
        setCounterTextView.setText(String.valueOf(setCount % 100));
    }


    private void resetWorkout() {
        setWorkoutStatusAndHelpText(WorkoutStatus.BEFORE_START);
        stopStopWatch();
        resetCounters();
    }

    private void resetCounters() {
        setCount = 0;
        setCounterTextView.setText("0");
        repCount = 0;
        repCounterTextView.setText("0");
    }

    private int getHelpTextIdByWorkoutStatus(WorkoutStatus status) {
        switch (status) {
            case BEFORE_START: return R.string.help_before_start;
            case IN_PROGRESS: return R.string.help_in_progress;
            case PAUSED: return R.string.help_paused;
            case BETWEEN_SETS: return R.string.help_between_sets;
            default: return R.string.empty_string;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adView.resume();
    }

    @Override
    public void onPause() {
        adView.pause();
        super.onPause();
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        sounds.release();
        adView.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_settings) {
            openSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        startActivityForResult(new Intent(this, SettingsActivity.class), Settings.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Settings.REQUEST_CODE && resultCode == RESULT_OK) {
            initSettingsRelatedParts();
        }
    }

    private void initSettingsRelatedParts() {
        helpTextView.setVisibility(settings.isShowHelp() ? View.VISIBLE : View.GONE);
    }

    private enum WorkoutStatus {
        BEFORE_START, COUNTDOWN_IN_PROGRESS, IN_PROGRESS, PAUSED, BETWEEN_SETS
    }

}
