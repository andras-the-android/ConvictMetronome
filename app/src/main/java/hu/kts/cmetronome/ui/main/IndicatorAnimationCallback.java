package hu.kts.cmetronome.ui.main;

/**
 * Created by andrasnemeth on 12/01/16.
 */
public interface IndicatorAnimationCallback {

    void onDownStarted();
    void onRightStarted();
    void onUpStarted();
    void onLeftStarted();
    void cycleFinished();

}
