package hu.kts.cmetronome;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;

import hu.kts.cmetronome.di.Injector;

public class ConvictMetronomeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.init(this);
        Injector.init(this);
        MobileAds.initialize(this, getString(R.string.admob_app_id));
    }
}
