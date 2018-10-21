package hu.kts.cmetronome

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class Settings(context: Context) {

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    val isShowHelp: Boolean
        get() = sharedPreferences.getBoolean(KEY_SHOW_HELP, true)

    val countdownStartValue: Int
        get() = Integer.parseInt(sharedPreferences.getString(KEY_COUNTDOWN_START_VALUE, "3")!!)

    val isAnalyticsEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_USE_DIAGNOSTICS, true)

    companion object {


        const val REQUEST_CODE = 23429

        const val KEY_SHOW_HELP = "showHelp"
        const val KEY_COUNTDOWN_START_VALUE = "countdownStartValue"
        const val KEY_USE_DIAGNOSTICS = "useDiagnostics"
    }

}
