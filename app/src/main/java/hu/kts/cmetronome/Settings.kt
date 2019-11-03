package hu.kts.cmetronome

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.widget.Toast
import java.lang.ref.WeakReference

class Settings(private val context: Context) {

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    //we have to hold a reference to this or else it'd be gc-d
    private val listener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key -> onPreferencesChanged(key); callExternalListeners(sharedPreferences, key) }

    //TODO replace with LiveData
    private val listeners: MutableSet<WeakReference<SharedPreferences.OnSharedPreferenceChangeListener>> = mutableSetOf()

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        onPreferencesChanged(KEY_REP_UP_TIME)
        onPreferencesChanged(KEY_REP_PAUSE_UP_TIME)
        onPreferencesChanged(KEY_REP_DOWN_TIME)
        onPreferencesChanged(KEY_REP_PAUSE_DOWN_TIME)
    }

    val isShowHelp: Boolean
        get() = sharedPreferences.getBoolean(KEY_SHOW_HELP, true)

    val countdownStartValue: Int
        get() = Integer.parseInt(sharedPreferences.getString(KEY_COUNTDOWN_START_VALUE, "3")!!)

    val isAnalyticsEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_USE_DIAGNOSTICS, true)

    val whatsNewVersion: Int
        get() = sharedPreferences.getInt(KEY_WHATS_NEW_VERSION, 10)

    var repStartsWithUp: Boolean = true
        private set
    var playSound: Boolean = true
        private set
    var repUpTime: Long = 0
        private set
    var repPauseUpTime: Long = 0
        private set
    var repDownTime: Long = 0
        private set
    var repPauseDownTime: Long = 0
        private set

    fun addListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        listeners.add(WeakReference(listener))
    }

    fun updateWhatsNewVersion() {
        sharedPreferences.edit().putInt(KEY_WHATS_NEW_VERSION, BuildConfig.VERSION_CODE).apply()
    }

    fun runMigration11() {
        sharedPreferences.edit()
                .putString(KEY_REP_UP_TIME, sharedPreferences.getString(KEY_REP_UP_DOWN_TIME, "2000"))
                .putString(KEY_REP_PAUSE_UP_TIME, sharedPreferences.getString(KEY_REP_PAUSE_TIME, "1000"))
                .apply()
    }

    private fun onPreferencesChanged(key: String) {
        when (key) {
            KEY_REP_UP_TIME -> {
                repUpTime = sharedPreferences.getString(KEY_REP_UP_TIME, "2000")?.toLong() ?: 2000
                if (sharedPreferences.getString(KEY_REP_DOWN_TIME, KEY_SAME_AS) == KEY_SAME_AS) repDownTime = repUpTime
                checkRepLength()
            }
            KEY_REP_PAUSE_UP_TIME -> {
                repPauseUpTime = sharedPreferences.getString(KEY_REP_PAUSE_UP_TIME, "1000")?.toLong() ?: 1000
                if (sharedPreferences.getString(KEY_REP_PAUSE_DOWN_TIME, KEY_SAME_AS) == KEY_SAME_AS) repPauseDownTime = repPauseUpTime
            }
            KEY_REP_DOWN_TIME -> {
                repDownTime = if (sharedPreferences.getString(KEY_REP_DOWN_TIME, KEY_SAME_AS) == KEY_SAME_AS) {
                    repUpTime
                } else {
                    sharedPreferences.getString(KEY_REP_DOWN_TIME, "2000")?.toLong() ?: 2000
                }
                checkRepLength()
            }
            KEY_REP_PAUSE_DOWN_TIME -> {
                repPauseDownTime = if (sharedPreferences.getString(KEY_REP_PAUSE_DOWN_TIME, KEY_SAME_AS) == KEY_SAME_AS) {
                    repPauseUpTime
                } else {
                    sharedPreferences.getString(KEY_REP_PAUSE_DOWN_TIME, "1000")?.toLong() ?: 1000
                }
            }
            KEY_REP_STARTS_WITH_UP -> repStartsWithUp = sharedPreferences.getBoolean(KEY_REP_STARTS_WITH_UP, true)
            KEY_PLAY_SOUND -> playSound = sharedPreferences.getBoolean(KEY_PLAY_SOUND, true)
        }
    }

    private fun checkRepLength() {
        if (repDownTime + repUpTime == 0L) {
            Toast.makeText(context, context.getString(R.string.rep_is_empty_message), Toast.LENGTH_SHORT).show()
        }
    }

    private fun callExternalListeners(sharedPreferences: SharedPreferences, key: String) {
        listeners.forEach {
            val listener = it.get()
            if (listener == null) listeners.remove(it) else listener.onSharedPreferenceChanged(sharedPreferences, key)
        }
    }

    companion object {

        const val KEY_SHOW_HELP = "showHelp"
        const val KEY_COUNTDOWN_START_VALUE = "countdownStartValue"
        const val KEY_USE_DIAGNOSTICS = "useDiagnostics"
        const val KEY_REP_UP_DOWN_TIME = "repUpDownTime"
        const val KEY_REP_UP_TIME = "repUpTime"
        const val KEY_REP_DOWN_TIME = "repDownTime"
        const val KEY_REP_PAUSE_TIME = "repPauseTime"
        const val KEY_REP_PAUSE_UP_TIME = "repPauseUpTime"
        const val KEY_REP_PAUSE_DOWN_TIME = "repPauseDownTime"
        const val KEY_REP_STARTS_WITH_UP= "repStartsWith"
        const val KEY_SAME_AS = "-1"
        const val KEY_PLAY_SOUND = "playSound"
        const val KEY_WHATS_NEW_VERSION  = "whatsNewVersion"
    }

}
