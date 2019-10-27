package hu.kts.cmetronome

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.widget.Toast

class Settings(private val context: Context) {

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    //we have to hold a reference to this or else it'd be gc-d
    private val listener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key -> onPreferencesChanged(key)}

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        onPreferencesChanged(KEY_REP_UP_TIME)
        onPreferencesChanged(KEY_REP_PAUSE_1_TIME)
        onPreferencesChanged(KEY_REP_DOWN_TIME)
        onPreferencesChanged(KEY_REP_PAUSE_2_TIME)
    }

    val isShowHelp: Boolean
        get() = sharedPreferences.getBoolean(KEY_SHOW_HELP, true)

    val countdownStartValue: Int
        get() = Integer.parseInt(sharedPreferences.getString(KEY_COUNTDOWN_START_VALUE, "3")!!)

    val isAnalyticsEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_USE_DIAGNOSTICS, true)

    val repStartsWithUp: Boolean
        get() = sharedPreferences.getBoolean(KEY_REP_STARTS_WITH_UP, true)

    var repUpTime: Long = 0
    var repPause1Time: Long = 0
    var repDownTime: Long = 0
    var repPause2Time: Long = 0

    fun addListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    private fun onPreferencesChanged(key: String) {
        when (key) {
            KEY_REP_UP_TIME -> {
                repUpTime = sharedPreferences.getString(KEY_REP_UP_TIME, "2000")?.toLong() ?: 2000
                if (sharedPreferences.getString(KEY_REP_DOWN_TIME, KEY_SAME_AS) == KEY_SAME_AS) repDownTime = repUpTime
                checkRepLength()
            }
            KEY_REP_PAUSE_1_TIME -> {
                repPause1Time = sharedPreferences.getString(KEY_REP_PAUSE_1_TIME, "1000")?.toLong() ?: 1000
                if (sharedPreferences.getString(KEY_REP_PAUSE_2_TIME, KEY_SAME_AS) == KEY_SAME_AS) repPause2Time = repPause1Time
            }
            KEY_REP_DOWN_TIME -> {
                repDownTime = if (sharedPreferences.getString(KEY_REP_DOWN_TIME, KEY_SAME_AS) == KEY_SAME_AS) {
                    repUpTime
                } else {
                    sharedPreferences.getString(KEY_REP_DOWN_TIME, "2000")?.toLong() ?: 2000
                }
                checkRepLength()
            }
            KEY_REP_PAUSE_2_TIME -> {
                repPause1Time = if (sharedPreferences.getString(KEY_REP_PAUSE_2_TIME, KEY_SAME_AS) == KEY_SAME_AS) {
                    repPause1Time
                } else {
                    sharedPreferences.getString(KEY_REP_PAUSE_2_TIME, "1000")?.toLong() ?: 1000
                }
            }
        }
    }

    private fun checkRepLength() {
        if (repDownTime + repUpTime == 0L) {
            Toast.makeText(context, context.getString(R.string.rep_is_empty_message), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {

        const val REQUEST_CODE = 23429

        const val KEY_SHOW_HELP = "showHelp"
        const val KEY_COUNTDOWN_START_VALUE = "countdownStartValue"
        const val KEY_USE_DIAGNOSTICS = "useDiagnostics"
        const val KEY_REP_UP_DOWN_TIME = "repUpDownTime"
        const val KEY_REP_UP_TIME = "repUpTime"
        const val KEY_REP_DOWN_TIME = "repDownTime"
        const val KEY_REP_PAUSE_TIME = "repPauseTime"
        const val KEY_REP_PAUSE_1_TIME = "repPause1Time"
        const val KEY_REP_PAUSE_2_TIME = "repPause2Time"
        const val KEY_REP_STARTS_WITH_UP= "repStartsWith"
        const val KEY_SAME_AS = "-1"
    }

}
