package hu.kts.cmetronome.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AlertDialog
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceFragmentCompat
import hu.kts.cmetronome.CmLog
import hu.kts.cmetronome.R
import hu.kts.cmetronome.repository.WorkoutSettings

class CmPreferenceFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
        PreferenceManager.getDefaultSharedPreferences(activity).registerOnSharedPreferenceChangeListener(this)
    }

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {

    }

    override fun onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(activity).unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

    private fun handleDiagnosticChange(sharedPreferences: SharedPreferences) {
        val activity = activity ?: return
        val enabled = sharedPreferences.getBoolean(WorkoutSettings.KEY_USE_DIAGNOSTICS, true)
        CmLog.enableTracker(activity, enabled)
        if (!enabled) {
            AlertDialog.Builder(activity)
                    .setMessage(R.string.settings_diagnostics_message)
                    .setPositiveButton(R.string.settings_diagnostics_keep) { _, _ ->
                        sharedPreferences.edit().putBoolean(WorkoutSettings.KEY_USE_DIAGNOSTICS, true).apply()
                        CmLog.enableTracker(activity, true)
                        findPreference<CheckBoxPreference>("useDiagnostics")?.isChecked = true
                    }
                    .setNegativeButton(R.string.settings_diagnostics_turn_off) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (WorkoutSettings.KEY_USE_DIAGNOSTICS == key) {
            handleDiagnosticChange(sharedPreferences)
        }
    }
}