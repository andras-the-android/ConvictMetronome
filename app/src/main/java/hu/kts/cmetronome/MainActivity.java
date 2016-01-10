package hu.kts.cmetronome;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.CountDownTimer;
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

    @InjectView(R.id.metronome_indicator)
    View indicatorView;
    @InjectView(R.id.rep_counter)
    TextView repCounterTextView;
//    @InjectView(R.id.help)
//    TextView helpTextView;

    int repCount = 0;
    private SoundPool soundPool;
    private int lowBeepSoundID;
    private int highBeepSoundID;
    private WorkoutStatus workoutStatus = WorkoutStatus.BEFORE_START;

    private Animator.AnimatorListener lowBeepAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            makeLowBeep();
        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private Animator.AnimatorListener highBeepAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            makeHighBeep();
        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {

             public void onTick(long millisUntilFinished) {
                 int remainingInSeconds = Math.round((float)millisUntilFinished / 1000);
                 repCounterTextView.setText(String.valueOf(remainingInSeconds));
                 Log.d(TAG, "countdown: " + millisUntilFinished + " -> " + remainingInSeconds);
             }

             public void onFinish() {
                 repCounterTextView.setText(String.valueOf(repCount));
                 workoutStatus = WorkoutStatus.IN_PROGRESS;
                 repCounterTextView.setTextColor(getResources().getColor(R.color.secondary_text));
                 startAnimation();
             }
          };


    private AnimatorSet animation;
    private int currentSoundId;
    private ObjectAnimator down;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.inject(this);
        initSoundPool();
    }


    private void initSoundPool() {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        lowBeepSoundID = soundPool.load(this, R.raw.up_sine, 1);
        highBeepSoundID = soundPool.load(this, R.raw.down_sine, 1);
    }

    private void makeLowBeep() {
        if (soundPool != null) {
//            currentSoundId = lowBeepSoundID;
            currentSoundId = soundPool.play(lowBeepSoundID, 1f, 1f, 5, 0, 1f);
        }
    }

    private void makeHighBeep() {
        if (soundPool != null) {
            //currentSoundId = highBeepSoundID;
            currentSoundId = soundPool.play(highBeepSoundID, 1f, 1f, 5, 0, 1f);
        }
    }

    private void startAnimation() {

        float longPath = getResources().getDimensionPixelSize(R.dimen.indicator_path_long);
        float shortPath = longPath / 2;
        down = ObjectAnimator.ofFloat(indicatorView, "translationY", 0f, longPath).setDuration(2000);
        down.addListener(lowBeepAnimatorListener);
        ObjectAnimator right = ObjectAnimator.ofFloat(indicatorView, "translationX", 0f, shortPath).setDuration(1000);
        //right.addListener(highBeepAnimatorListener);
        ObjectAnimator up = ObjectAnimator.ofFloat(indicatorView, "translationY", longPath, 0f).setDuration(2000);
        up.addListener(highBeepAnimatorListener);
        ObjectAnimator left = ObjectAnimator.ofFloat(indicatorView, "translationX", shortPath, 0f).setDuration(1000);
        //left.addListener(lowBeepAnimatorListener);

        animation = new AnimatorSet();
        animation.play(down).before(right);
        animation.play(right).before(up);
        animation.play(up).before(left);
        animation.play(left);
        animation.start();
        animation.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {

                super.onAnimationEnd(animation);
                if (workoutStatus == WorkoutStatus.IN_PROGRESS) {
                    ++repCount;
                    repCounterTextView.setText(String.valueOf(repCount));
                    animation.start();
                } else {
                    indicatorView.setTranslationX(0f);
                    indicatorView.setTranslationY(0f);
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                indicatorView.setTranslationX(0f);
                indicatorView.setTranslationY(0f);
            }
        });
        animation.start();
    }

    private void resetIndicator() {
        animation.cancel();
        down.cancel();
        soundPool.stop(currentSoundId);
        Log.d(TAG, "resetIndicator: " + String.valueOf(currentSoundId == lowBeepSoundID)  + " "  + String.valueOf(currentSoundId == highBeepSoundID));
        indicatorView.clearAnimation();
        indicatorView.setTranslationX(0f);
        indicatorView.setTranslationY(0f);
    }

    private void resumeAnimation() {
        countDownAndStart();
    }

    private void countDownAndStart() {
        workoutStatus = WorkoutStatus.COUNTDOWN_IN_PROGRESS;
        repCounterTextView.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        countDownTimer.start();
    }

    @OnClick(R.id.rep_counter)
    public void onRepCounterClick(View view) {
        Log.d(TAG, "onRepCounterClick: " + workoutStatus);
        switch (workoutStatus) {
            case BEFORE_START: startWorkout(); break;
            case IN_PROGRESS: pauseWorkout(); break;
            case PAUSED: resumeWorkout(); break;
        }
    }

    @OnLongClick(R.id.rep_counter)
    public boolean onRepCounterLongClick(View view) {
        workoutStatus = WorkoutStatus.BEFORE_START;
        resetIndicator();
        countDownTimer.cancel();
        repCount = 0;
        repCounterTextView.setText("0");
        return true;
    }

    private void startWorkout() {
        countDownAndStart();
//        helpTextView.setText(R.string.help_in_progress);
    }

    private void pauseWorkout() {
        workoutStatus = WorkoutStatus.PAUSED;
        resetIndicator();
//        helpTextView.setText(R.string.help_paused);
    }

    private void resumeWorkout() {
        workoutStatus = WorkoutStatus.IN_PROGRESS;
        resumeAnimation();
//        helpTextView.setText(R.string.help_in_progress);
    }

    @Override
    protected void onStop() {
        super.onStop();
        soundPool.release();
        soundPool = null;
    }

    private enum WorkoutStatus {
        BEFORE_START, IN_PROGRESS, COUNTDOWN_IN_PROGRESS, PAUSED;
    }

}
