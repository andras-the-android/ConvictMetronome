package hu.kts.cmetronome

import android.app.Application

import com.google.android.gms.ads.MobileAds
import hu.kts.cmetronome.di.AppComponent

class ConvictMetronomeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppComponent.init(this)
//        CmLog.init(this)
        MobileAds.initialize(this, getString(R.string.admob_app_id))
    }
}
