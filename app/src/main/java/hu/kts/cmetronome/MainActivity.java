package hu.kts.cmetronome;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.metronome_indicator)
    View indicatorView;
    @InjectView(R.id.rep_counter)
    TextView repCounterTextView;
    @InjectView(R.id.help)
    TextView helpTextView;

    int repCount = 0;
    private SoundPool soundPool;
    private int soundID;
    private WorkoutStatus workoutStatus = WorkoutStatus.BEFORE_START;

    private Animator.AnimatorListener beepAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            makeBeep();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };
    private AnimatorSet animation;

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
        soundID = soundPool.load(this, R.raw.beep, 1);
    }

    private void makeBeep() {
        if (soundPool != null) {
            soundPool.play(soundID, 1f, 1f, 5, 0, 1f);
        }
    }

    private void startAnimation() {

        float shortPath = getResources().getDimensionPixelSize(R.dimen.indicator_path_short);
        float longPath = getResources().getDimensionPixelSize(R.dimen.indicator_path_long);
        ObjectAnimator down = ObjectAnimator.ofFloat(indicatorView, "translationY", 0f, longPath).setDuration(2000);
        down.addListener(beepAnimatorListener);
        ObjectAnimator right = ObjectAnimator.ofFloat(indicatorView, "translationX", 0f, shortPath).setDuration(1000);
        right.addListener(beepAnimatorListener);
        ObjectAnimator up = ObjectAnimator.ofFloat(indicatorView, "translationY", longPath, 0f).setDuration(2000);
        up.addListener(beepAnimatorListener);
        ObjectAnimator left = ObjectAnimator.ofFloat(indicatorView, "translationX", shortPath, 0f).setDuration(1000);
        left.addListener(beepAnimatorListener);

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
                ++repCount;
                repCounterTextView.setText(String.valueOf(repCount));
                animation.start();

            }

        });
        animation.start();
    }

    private void pauseAnimation() {
        animation.pause();
    }

    private void resumeAnimation() {
        animation.resume();
    }

    @OnClick(R.id.rep_counter)
    public void onRepCounterClick(View view) {
        switch (workoutStatus) {
            case BEFORE_START: startWorkout(); break;
            case IN_PROGRESS: pauseWorkout(); break;
            case PAUSED: resumeWorkout(); break;
        }
    }

    private void startWorkout() {
        workoutStatus = WorkoutStatus.IN_PROGRESS;
        startAnimation();
        helpTextView.setText(R.string.help_in_progress);
    }

    private void pauseWorkout() {
        workoutStatus = WorkoutStatus.PAUSED;
        pauseAnimation();
        helpTextView.setText(R.string.help_paused);
    }

    private void resumeWorkout() {
        workoutStatus = WorkoutStatus.IN_PROGRESS;
        resumeAnimation();
        helpTextView.setText(R.string.help_in_progress);
    }

    @Override
    protected void onStop() {
        super.onStop();
        soundPool.release();
        soundPool = null;
    }

    private enum WorkoutStatus {
        BEFORE_START, IN_PROGRESS, PAUSED;
    }

}
