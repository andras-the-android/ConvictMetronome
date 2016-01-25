package hu.kts.cmetronome;

import android.app.Application;

/**
 * Created by andrasnemeth on 25/01/16.
 */
public class ConvictMetronomeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initSettings();
    }

    private void initSettings() {
        Settings.INSTANCE.init(this);
    }
}
