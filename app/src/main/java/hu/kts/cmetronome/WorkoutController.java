package hu.kts.cmetronome;

import android.app.Activity;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by andrasnemeth on 25/01/16.
 */
public class WorkoutController {

    @InjectView(R.id.rep_counter)
    TextView repCounterTextView;
    @InjectView(R.id.set_counter)
    TextView setCounterTextView;

    private Activity activity;
    int repCount = 0;
    int setCount = 0;
    private WorkoutStatus workoutStatus;


    private StopWatch stopWatch;
    private Sounds sounds;
    private IndicatorAnimation indicatorAnimation;
    private Settings settings;
    private Help help;
    private CountDowner countDowner;

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

    public WorkoutController(Activity activity) {
        this.activity = activity;
        ButterKnife.inject(this, activity);

        sounds = new Sounds(activity);
        help = new Help(activity);
        countDowner = new CountDowner(activity, this::startWorkout);
        stopWatch = new StopWatch(activity);
        settings = Settings.INSTANCE;
        initSettingsRelatedParts();
        setWorkoutStatusAndHelpText(WorkoutStatus.BEFORE_START);
    }

    public void onRepCounterClick() {
        switch (workoutStatus) {
            case BEFORE_START:
            case PAUSED:
            case BETWEEN_SETS: countDownAndStart(); break;
            case IN_PROGRESS: pauseWorkout(); break;
        }
    }

    public boolean onRepCounterLongClick() {
        if (workoutStatus == WorkoutStatus.IN_PROGRESS || workoutStatus == WorkoutStatus.PAUSED) {
            stopSet();
            return true;
        } if (workoutStatus == WorkoutStatus.BETWEEN_SETS) {
            resetWorkout();
            return true;
        }
        return false;
    }

    private void pauseWorkout() {
        setWorkoutStatusAndHelpText(WorkoutStatus.PAUSED);
        resetIndicator();
    }

    private void resetIndicator() {
        indicatorAnimation.stop();
        sounds.stop();
    }


    private void countDownAndStart() {
        stopWatch.stop();
        setWorkoutStatusAndHelpText(WorkoutStatus.COUNTDOWN_IN_PROGRESS);
        countDowner.start();
    }

    private void startWorkout() {
        repCounterTextView.setText(String.valueOf(repCount));
        setWorkoutStatusAndHelpText(WorkoutStatus.IN_PROGRESS);

        repCounterTextView.setText("0");
        getIndicatorAnimation().start();
    }

    private IndicatorAnimation getIndicatorAnimation() {
        if (indicatorAnimation == null) {
            indicatorAnimation = new IndicatorAnimation(activity, indicatorAnimationCallback);
        }
        return indicatorAnimation;
    }

    private void stopSet() {
        setWorkoutStatusAndHelpText(WorkoutStatus.BETWEEN_SETS);
        resetIndicator();
        countDowner.stop();
        stopWatch.start();
        repCount = 0;
        increaseSetCounter();
    }

    private void setWorkoutStatusAndHelpText(WorkoutStatus status) {
        workoutStatus = status;
        help.setHelpTextByWorkoutStatus(status);
    }

    private void increaseSetCounter() {
        ++setCount;
        //setcounter should contain maximum 2 digits
        setCounterTextView.setText(String.valueOf(setCount % 100));
    }


    private void resetWorkout() {
        setWorkoutStatusAndHelpText(WorkoutStatus.BEFORE_START);
        stopWatch.stop();
        resetCounters();
    }

    private void resetCounters() {
        setCount = 0;
        setCounterTextView.setText("0");
        repCount = 0;
        repCounterTextView.setText("0");
    }

    public void initSettingsRelatedParts() {
        help.setEnabled(settings.isShowHelp());
    }

    public void onDestroy() {
        sounds.release();
    }


}
