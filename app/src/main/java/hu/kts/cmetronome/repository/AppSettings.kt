package hu.kts.cmetronome.repository

import android.content.SharedPreferences
import hu.kts.cmetronome.BuildConfig
import hu.kts.cmetronome.di.AppScope
import javax.inject.Inject

@AppScope
class AppSettings @Inject constructor(
        private val sharedPreferences: SharedPreferences
) {

    val isAnalyticsEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_USE_DIAGNOSTICS, true)

    val whatsNewVersion: Int
        get() = sharedPreferences.getInt(KEY_WHATS_NEW_VERSION, 10)

    val isShowHelp: Boolean
        get() = sharedPreferences.getBoolean(KEY_SHOW_HELP, true)

    fun updateWhatsNewVersion() {
        sharedPreferences.edit().putInt(KEY_WHATS_NEW_VERSION, BuildConfig.VERSION_CODE).apply()
    }

    fun addListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun runMigration11() {
        sharedPreferences.edit()
                .putString(WorkoutSettings.KEY_REP_UP_TIME, sharedPreferences.getString(WorkoutSettings.KEY_REP_UP_DOWN_TIME, "2000"))
                .putString(WorkoutSettings.KEY_REP_PAUSE_UP_TIME, sharedPreferences.getString(WorkoutSettings.KEY_REP_PAUSE_TIME, "1000"))
                .apply()
    }

    companion object {
        const val KEY_USE_DIAGNOSTICS = "useDiagnostics"
        const val KEY_WHATS_NEW_VERSION  = "whatsNewVersion"
        const val KEY_SHOW_HELP = "showHelp"
    }
}