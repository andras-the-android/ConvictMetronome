package hu.kts.cmetronome;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.metronome_indicator)
    View indicatorView;

    @InjectView(R.id.rep_counter)
    TextView repCounterTextView;

    int repCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.inject(this);
        startAnimation();
    }

    private void startAnimation() {
        final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.beep);
        float shortPath = getResources().getDimensionPixelSize(R.dimen.indicator_path_short);
        float longPath = getResources().getDimensionPixelSize(R.dimen.indicator_path_long);
        ObjectAnimator down = ObjectAnimator.ofFloat(indicatorView, "translationY", 0f, longPath).setDuration(2000);
        ObjectAnimator right = ObjectAnimator.ofFloat(indicatorView, "translationX", 0f, shortPath).setDuration(1000);
        ObjectAnimator up = ObjectAnimator.ofFloat(indicatorView, "translationY", longPath, 0f).setDuration(2000);
        ObjectAnimator left = ObjectAnimator.ofFloat(indicatorView, "translationX", shortPath, 0f).setDuration(1000);

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(down).before(right);
        animatorSet.play(right).before(up);
        animatorSet.play(up).before(left);
        animatorSet.play(left);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {

                super.onAnimationEnd(animation);
                ++repCount;
                repCounterTextView.setText(String.valueOf(repCount));
                animatorSet.start();
                mp.start();

            }

        });
        animatorSet.start();


    }


}
