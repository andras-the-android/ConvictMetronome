package hu.kts.cmetronome.ui.main;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hu.kts.cmetronome.R;
import hu.kts.cmetronome.ui.main.IndicatorAnimationCallback;

/**
 * Created by andrasnemeth on 12/01/16.
 *
 * AnimatorSet is avoided because it behaved strange on cancel.
 */
public class IndicatorAnimation {

    @InjectView(R.id.metronome)
    View metronomeView;
    @InjectView(R.id.metronome_indicator)
    View indicatorView;

    private IndicatorAnimationCallback callback;
    private boolean animationRunning;
    private ObjectAnimator down;
    private ObjectAnimator right;
    private ObjectAnimator up;
    private ObjectAnimator left;
    private ObjectAnimator currentAnimation;

    private Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            invokeStartCallback(animation);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (animationRunning) {
                currentAnimation = getNextAnimation(animation);
                if (isLastPartOfTheCycle(animation)) {
                    callback.cycleFinished();
                }
                currentAnimation.start();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            resetIndicatorPosition();
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private void invokeStartCallback(Animator animation) {
        if (animation == down) {
            callback.onDownStarted();
        } else if (animation == right) {
            callback.onRightStarted();
        } else if (animation == up) {
            callback.onUpStarted();
        } else if (animation == left) {
            callback.onLeftStarted();
        }
    }

    private ObjectAnimator getNextAnimation(Animator animation) {
        if (animation == down) {
            return right;
        } else if (animation == right) {
            return up;
        } else if (animation == up) {
            return left;
        } else {
            return down;
        }
    }

    private boolean isLastPartOfTheCycle(Animator animation) {
        return animation == left;
    }

    private void resetIndicatorPosition() {
        indicatorView.setTranslationX(0f);
        indicatorView.setTranslationY(0f);
    }

    public IndicatorAnimation(Activity activity, IndicatorAnimationCallback callback) {
        this.callback = callback;
        ButterKnife.inject(this, activity);

        Resources resources = activity.getResources();
        float indicatorDiameter = (float)resources.getDimensionPixelSize(R.dimen.indicator_diameter);
        float metronomePadding = (float)resources.getDimensionPixelSize(R.dimen.metronome_padding) * 2;
        float longPath = metronomeView.getHeight() - indicatorDiameter - metronomePadding;
        float shortPath = metronomeView.getWidth() - indicatorDiameter - metronomePadding;

        down = ObjectAnimator.ofFloat(indicatorView, "translationY", 0f, longPath).setDuration(2000);
        down.addListener(animatorListener);

        right = ObjectAnimator.ofFloat(indicatorView, "translationX", 0f, shortPath).setDuration(1000);
        right.addListener(animatorListener);

        up = ObjectAnimator.ofFloat(indicatorView, "translationY", longPath, 0f).setDuration(2000);
        up.addListener(animatorListener);

        left = ObjectAnimator.ofFloat(indicatorView, "translationX", shortPath, 0f).setDuration(1000);
        left.addListener(animatorListener);
    }

    public void start() {
        animationRunning = true;
        currentAnimation = down;
        currentAnimation.start();
    }

    public void stop() {
        if (animationRunning) {
            animationRunning = false;
            currentAnimation.cancel();
        }
    }



}
