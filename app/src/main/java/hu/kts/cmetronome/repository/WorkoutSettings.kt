package hu.kts.cmetronome.repository

import android.content.SharedPreferences
import hu.kts.cmetronome.R
import hu.kts.cmetronome.ui.Toaster
import java.lang.ref.WeakReference

class WorkoutSettings(private val sharedPreferences: SharedPreferences, private val toaster: Toaster) {

    //we have to hold a reference to this or else it'd be gc-d
    private val listener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key -> onPreferencesChanged(key); callExternalListeners(sharedPreferences, key) }

    private val listeners: MutableSet<WeakReference<SharedPreferences.OnSharedPreferenceChangeListener>> = mutableSetOf()

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        onPreferencesChanged(KEY_REP_UP_TIME)
        onPreferencesChanged(KEY_REP_PAUSE_UP_TIME)
        onPreferencesChanged(KEY_REP_DOWN_TIME)
        onPreferencesChanged(KEY_REP_PAUSE_DOWN_TIME)
    }

    val countdownStartValue: Int
        get() = sharedPreferences.getString(KEY_COUNTDOWN_START_VALUE, "3")!!.toInt()


    var repStartsWithUp: Boolean = sharedPreferences.getBoolean(KEY_REP_STARTS_WITH_UP, true)
        private set
    var playSound: Boolean = sharedPreferences.getBoolean(KEY_PLAY_SOUND, true)
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
            toaster.showShort(R.string.rep_is_empty_message)
        }
    }

    private fun callExternalListeners(sharedPreferences: SharedPreferences, key: String) {
        listeners.forEach {
            val listener = it.get()
            if (listener == null) listeners.remove(it) else listener.onSharedPreferenceChanged(sharedPreferences, key)
        }
    }

    companion object {
        const val KEY_COUNTDOWN_START_VALUE = "countdownStartValue"
        const val KEY_REP_UP_DOWN_TIME = "repUpDownTime"
        const val KEY_REP_UP_TIME = "repUpTime"
        const val KEY_REP_DOWN_TIME = "repDownTime"
        const val KEY_REP_PAUSE_TIME = "repPauseTime"
        const val KEY_REP_PAUSE_UP_TIME = "repPauseUpTime"
        const val KEY_REP_PAUSE_DOWN_TIME = "repPauseDownTime"
        const val KEY_REP_STARTS_WITH_UP= "repStartsWith"
        const val KEY_SAME_AS = "-1"
        const val KEY_PLAY_SOUND = "playSound"
    }

}
