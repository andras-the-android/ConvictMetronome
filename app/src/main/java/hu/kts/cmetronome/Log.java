package hu.kts.cmetronome;

/**
 * Created by andrasnemeth on 10/02/16.
 */

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.PrintWriter;
import java.io.StringWriter;

import hu.kts.cmetronome.R;

/**
 * Created by Andras_Nemeth on 2015.11.04..
 */
public class Log {

    private static Tracker tracker;

    private Log() {
    }

    static void init(Context context) {
        createTracker(context);
    }

    private static void createTracker(Context context) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        tracker = analytics.newTracker(R.xml.google_analytics_tracker);
    }

    public static void enableTracker(Context context, boolean enable) {
        GoogleAnalytics.getInstance(context).setAppOptOut(enable);
    }

    public static void d(String tag, String msg) {
        android.util.Log.d(tag, msg);
    }

    public static void e(String tag, String msg, Throwable th) {
        android.util.Log.e(tag, msg, th);

        tracker.send(new HitBuilders.ExceptionBuilder()
                .setDescription(th.getMessage() + " : " + getStackTrace(th))
                .setFatal(false)
                .build());
    }

    private static String getStackTrace(Throwable th) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        th.printStackTrace(pw);
        return sw.toString();
    }
}
