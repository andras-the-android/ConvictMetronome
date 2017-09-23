package hu.kts.cmetronome;

import android.app.Application;

import hu.kts.cmetronome.di.Injector;

public class ConvictMetronomeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initSettings();
        Log.init(this);
        Injector.init(this);
    }

    private void initSettings() {
        Settings.INSTANCE.init(this);
    }
}
