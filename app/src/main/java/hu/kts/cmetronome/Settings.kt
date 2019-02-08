package hu.kts.cmetronome

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class Settings(context: Context) {

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    //we have to hold a reference to this or else it'd be gc-d
    private val listener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key -> onPreferencesChanged(key)}

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        onPreferencesChanged(KEY_REP_UP_DOWN_TIME)
        onPreferencesChanged(KEY_REP_PAUSE_TIME)
    }

    val isShowHelp: Boolean
        get() = sharedPreferences.getBoolean(KEY_SHOW_HELP, true)

    val countdownStartValue: Int
        get() = Integer.parseInt(sharedPreferences.getString(KEY_COUNTDOWN_START_VALUE, "3")!!)

    val isAnalyticsEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_USE_DIAGNOSTICS, true)

    var repUpDownTime: Long = 0

    var repPauseTime: Long = 0

    private fun onPreferencesChanged(key: String) {
        when (key) {
            KEY_REP_UP_DOWN_TIME -> repUpDownTime = sharedPreferences.getString(KEY_REP_UP_DOWN_TIME, "2000")?.toLong() ?: 2000
            KEY_REP_PAUSE_TIME -> repPauseTime = sharedPreferences.getString(KEY_REP_PAUSE_TIME, "1000")?.toLong() ?: 1000
        }
    }

    companion object {

        const val REQUEST_CODE = 23429

        const val KEY_SHOW_HELP = "showHelp"
        const val KEY_COUNTDOWN_START_VALUE = "countdownStartValue"
        const val KEY_USE_DIAGNOSTICS = "useDiagnostics"
        const val KEY_REP_UP_DOWN_TIME = "repUpDownTime"
        const val KEY_REP_PAUSE_TIME = "repPauseTime"
    }

}
