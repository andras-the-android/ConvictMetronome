package hu.kts.cmetronome.admob;


import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class AdViewWrapper implements LifecycleObserver {

    private AdView adView;

    public AdViewWrapper(AdView adView) {
        this.adView = adView;
        AdRequest adRequest = AdMobTestDeviceFilteredBuilderFactory.get().build();
        adView.loadAd(adRequest);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        adView.resume();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        adView.pause();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        adView.destroy();
    }
}
