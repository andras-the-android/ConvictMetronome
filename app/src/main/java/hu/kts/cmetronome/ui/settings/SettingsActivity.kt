package hu.kts.cmetronome.ui.settings

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceFragmentCompat
import hu.kts.cmetronome.CmLog
import hu.kts.cmetronome.R
import hu.kts.cmetronome.Settings

class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var settingsChanged = false
    private lateinit var fragment: CmPreferenceFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            fragment = CmPreferenceFragment()
            supportFragmentManager.beginTransaction().replace(android.R.id.content, fragment).commit()
        }
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

    private fun handleDiagnosticChange(sharedPreferences: SharedPreferences) {
        val enabled = sharedPreferences.getBoolean(Settings.KEY_USE_DIAGNOSTICS, true)
        CmLog.enableTracker(this, enabled)
        if (!enabled) {
            AlertDialog.Builder(this)
                    .setMessage(R.string.settings_diagnostics_message)
                    .setPositiveButton(R.string.settings_diagnostics_keep) { _, _ ->
                        sharedPreferences.edit().putBoolean(Settings.KEY_USE_DIAGNOSTICS, true).apply()
                        CmLog.enableTracker(this, true)
                        (fragment.findPreference("useDiagnostics") as CheckBoxPreference).isChecked = true
                    }
                    .setNegativeButton(R.string.settings_diagnostics_turn_off) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        settingsChanged = true
        if (Settings.KEY_USE_DIAGNOSTICS == key) {
            handleDiagnosticChange(sharedPreferences)
        }
    }


    override fun finish() {
        setResult(if (settingsChanged) Activity.RESULT_OK else Activity.RESULT_CANCELED)
        super.finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    class CmPreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)
        }

        override fun onCreatePreferences(bundle: Bundle?, s: String?) {

        }
    }
}
