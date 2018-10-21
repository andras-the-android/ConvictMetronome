package hu.kts.cmetronome.ui.settings

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.MenuItem
import hu.kts.cmetronome.Log
import hu.kts.cmetronome.R
import hu.kts.cmetronome.Settings

class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var settingsChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(android.R.id.content, MyPreferenceFragment()).commit()
        }
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

    private fun handleDiagnosticChange(enabled: Boolean) {
        Log.enableTracker(this, enabled)
        if (!enabled) {
            AlertDialog.Builder(this).setMessage(R.string.settings_diagnostics_message).setPositiveButton(android.R.string.ok) { dialog, which -> }.show()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        settingsChanged = true
        if (Settings.KEY_USE_DIAGNOSTICS == key) {
            handleDiagnosticChange(sharedPreferences.getBoolean(Settings.KEY_USE_DIAGNOSTICS, true))
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

    class MyPreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)
        }

        override fun onCreatePreferences(bundle: Bundle, s: String) {

        }
    }
}
