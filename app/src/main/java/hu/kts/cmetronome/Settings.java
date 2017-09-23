package hu.kts.cmetronome;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {


    public static final int REQUEST_CODE = 23429;

    public static final String KEY_SHOW_HELP = "showHelp";
    public static final String KEY_COUNTDOWN_START_VALUE = "countdownStartValue";
    public static final String KEY_USE_DIAGNOSTICS = "useDiagnostics";

    private SharedPreferences sharedPreferences;

    public Settings(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isShowHelp() {
        return sharedPreferences.getBoolean(KEY_SHOW_HELP, true);
    }

    public int getCountdownStartValue() {
        return Integer.parseInt(sharedPreferences.getString(KEY_COUNTDOWN_START_VALUE, "3"));
    }

}
