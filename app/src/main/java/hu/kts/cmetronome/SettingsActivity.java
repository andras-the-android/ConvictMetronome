package hu.kts.cmetronome;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.MenuItem;

/**
 * Created by andrasnemeth on 21/01/16.
 */
public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private boolean settingsChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void handleDiagnosticChange(boolean enabled) {
        Log.enableTracker(this, enabled);
        if (!enabled) {
            new AlertDialog.Builder(this).setMessage(R.string.settings_diagnostics_message).setPositiveButton(android.R.string.ok, (dialog, which) -> {}).show();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        settingsChanged = true;
        if (Settings.KEY_USE_DIAGNOSTICS.equals(key)) {
            handleDiagnosticChange(sharedPreferences.getBoolean(Settings.KEY_USE_DIAGNOSTICS, true));
        }
    }


    public static class MyPreferenceFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {

        }
    }

    @Override
    public void finish() {
        setResult(settingsChanged ? RESULT_OK : RESULT_CANCELED);
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
