package hu.kts.cmetronome.ui.workout;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.kts.cmetronome.R;
import hu.kts.cmetronome.Settings;
import hu.kts.cmetronome.Sounds;
import hu.kts.cmetronome.WorkoutStatus;
import hu.kts.cmetronome.repository.WorkoutRepository;

public class WorkoutController implements LifecycleObserver {

    @BindView(R.id.rep_counter)
    TextView repCounterTextView;
    @BindView(R.id.set_counter)
    TextView setCounterTextView;

    private Activity activity;
    private final WorkoutRepository repository;

    private Stopwatch stopWatch;
    private Sounds sounds;
    private IndicatorAnimation indicatorAnimation;
    private Settings settings;
    private Help help;
    private Countdowner countdowner;

    public WorkoutController(Activity activity, WorkoutRepository workoutRepository, Settings settings) {
        this.activity = activity;
        this.repository = workoutRepository;
        this.settings = settings;
        ButterKnife.bind(this, activity);
        help = new Help(activity);
        sounds = new Sounds(activity);
        countdowner = new Countdowner(activity, this::startWorkout, this::onCountdownCancelled, settings);
        stopWatch = new Stopwatch(activity);
        initWorkoutData();
        initSettingsRelatedParts();

    }

    private void initWorkoutData() {
        if (repository.getWorkoutStatus() != null) {
            WorkoutStatus savedWorkoutStatus = repository.getWorkoutStatus();
            setWorkoutStatusAndHelpText(savedWorkoutStatus == WorkoutStatus.IN_PROGRESS || savedWorkoutStatus == WorkoutStatus.COUNTDOWN_IN_PROGRESS? WorkoutStatus.PAUSED : savedWorkoutStatus);
            if (repository.getWorkoutStatus() == WorkoutStatus.BETWEEN_SETS) {
                stopWatch.start(repository.getStopwatchStartTime());
            }
        } else {
            setWorkoutStatusAndHelpText(WorkoutStatus.BEFORE_START);
        }
        fillRepCounterTextViewWithTruncatedData();
        fillSetCounterTextViewWithTruncatedData();
    }

    public void onRepCounterClick() {
        switch (repository.getWorkoutStatus()) {
            case BEFORE_START:
            case PAUSED:
            case BETWEEN_SETS: countDownAndStart(); break;
            case COUNTDOWN_IN_PROGRESS:
            case IN_PROGRESS: pauseWorkout(); break;
        }
    }

    public boolean onRepCounterLongClick() {
        if (repository.getWorkoutStatus() == WorkoutStatus.IN_PROGRESS || repository.getWorkoutStatus() == WorkoutStatus.PAUSED) {
            stopSet();
            return true;
        } if (repository.getWorkoutStatus() == WorkoutStatus.BETWEEN_SETS) {
            resetWorkout();
            return true;
        }
        return false;
    }

    public void pauseWorkout() {
        setWorkoutStatusAndHelpText(WorkoutStatus.PAUSED);
        countdowner.cancel();
        sounds.stop();
        resetIndicator();
    }

    private void onCountdownCancelled() {
        fillRepCounterTextViewWithTruncatedData();
    }

    private void resetIndicator() {
        getIndicatorAnimation().stop();
    }


    private void countDownAndStart() {
        if (repository.getWorkoutStatus() == WorkoutStatus.BETWEEN_SETS) {
            repository.resetRepCounter();
        }
        stopWatch.stop();
        setWorkoutStatusAndHelpText(WorkoutStatus.COUNTDOWN_IN_PROGRESS);
        countdowner.start();
    }

    private void startWorkout() {
        setWorkoutStatusAndHelpText(WorkoutStatus.IN_PROGRESS);
        fillRepCounterTextViewWithTruncatedData();
        getIndicatorAnimation().start();
    }

    /**
     * Initialization can't be called from onCreate because animation needs the actual size of the elements
     */
    private IndicatorAnimation getIndicatorAnimation() {
        if (indicatorAnimation == null) {
            indicatorAnimation = new IndicatorAnimation(activity, this::onIndicatorAnimationEvent);
        }
        return indicatorAnimation;
    }

    private void onIndicatorAnimationEvent(IndicatorAnimation.Event event) {
        switch (event) {
            case DOWN:
                sounds.makeUpSound();
                break;
            case UP:
                sounds.makeDownSound();
                break;
            case LEFT:
                repository.increaseRepCounter();
                fillRepCounterTextViewWithTruncatedData();
                break;
        }
    }

    private void stopSet() {
        setWorkoutStatusAndHelpText(WorkoutStatus.BETWEEN_SETS);
        resetIndicator();
        sounds.stop();
        countdowner.cancel();
        stopWatch.start();
        repository.setStopwatchStartTime(stopWatch.getStartTime());
        increaseSetCounter();
    }

    private void setWorkoutStatusAndHelpText(WorkoutStatus status) {
        repository.setWorkoutStatus(status);
        help.setHelpTextByWorkoutStatus(status);
    }

    private void increaseSetCounter() {
        repository.increaseSetCounter();
        fillSetCounterTextViewWithTruncatedData();
    }


    private void resetWorkout() {
        setWorkoutStatusAndHelpText(WorkoutStatus.BEFORE_START);
        stopWatch.stop();
        repository.resetCounters();
        fillSetCounterTextViewWithTruncatedData();
        fillRepCounterTextViewWithTruncatedData();
    }

    public void initSettingsRelatedParts() {
        help.setEnabled(settings.isShowHelp());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        sounds.release();
        stopWatch.stop();
        countdowner.cancel();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        if (repository.getWorkoutStatus() == WorkoutStatus.IN_PROGRESS || repository.getWorkoutStatus() == WorkoutStatus.COUNTDOWN_IN_PROGRESS) {
            pauseWorkout();
        }
    }

    /**
     * counter should contain maximum 2 digits
     */
    private void fillRepCounterTextViewWithTruncatedData() {
        repCounterTextView.setText(String.valueOf(repository.getRepCount() % 100));
    }

    /**
     * counter should contain maximum 2 digits
     */
    private void fillSetCounterTextViewWithTruncatedData() {
        setCounterTextView.setText(String.valueOf(repository.getSetCount() % 100));
    }
}
