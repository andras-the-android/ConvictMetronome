package hu.kts.cmetronome.admob


import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.ads.AdView


class AdViewWrapper(private val adView: AdView) : LifecycleObserver {

    init {
        val adRequest = AdMobTestDeviceFilteredBuilderFactory.get().build()
        adView.loadAd(adRequest)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        adView.resume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        adView.pause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        adView.destroy()
    }
}
