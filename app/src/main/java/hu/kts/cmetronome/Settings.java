package hu.kts.cmetronome;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by andrasnemeth on 21/01/16.
 */
public class Settings {


    public static final int REQUEST_CODE = 23429;
    public static final Settings INSTANCE = new Settings();

    private static final String KEY_SHOW_HELP = "showHelp";
    private static final String KEY_COUNTDOWN_START_VALUE = "countdownStartValue";

    private SharedPreferences sharedPreferences;

    private Settings() {}

    public void init(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isShowHelp() {
        return sharedPreferences.getBoolean(KEY_SHOW_HELP, true);
    }

    public int getCountdownStartValue() {
        return Integer.parseInt(sharedPreferences.getString(KEY_COUNTDOWN_START_VALUE, "3"));
    }

}
