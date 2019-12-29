package hu.kts.cmetronome.admob


import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdView


class AdViewWrapper(private val adView: AdView) : DefaultLifecycleObserver {

    init {
        val adRequest = AdMobTestDeviceFilteredBuilderFactory.get().build()
        adView.loadAd(adRequest)
    }

    override fun onResume(owner: LifecycleOwner) {
        adView.resume()
    }

    override fun onPause(owner: LifecycleOwner) {
        adView.pause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        adView.destroy()
    }
}
