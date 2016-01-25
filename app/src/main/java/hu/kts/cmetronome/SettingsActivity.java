package hu.kts.cmetronome;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * Created by andrasnemeth on 21/01/16.
 */
public class SettingsActivity extends AppCompatActivity {

    private boolean settingsChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> settingsChanged = true);
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
}
