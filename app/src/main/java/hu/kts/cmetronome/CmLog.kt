package hu.kts.cmetronome

import android.content.Context
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import hu.kts.cmetronome.repository.Settings
import java.io.PrintWriter
import java.io.StringWriter

object CmLog {

    private lateinit var tracker: Tracker

    internal fun init(context: Context, settings: Settings) {
        createTracker(context)
        enableTracker(context, settings.isAnalyticsEnabled)
    }

    private fun createTracker(context: Context) {
        val analytics = GoogleAnalytics.getInstance(context)
        tracker = analytics.newTracker(R.xml.google_analytics_tracker)
    }

    fun enableTracker(context: Context, enable: Boolean) {
        GoogleAnalytics.getInstance(context).appOptOut = enable
    }

    fun d(tag: String, msg: String) {
        android.util.Log.d(tag, msg)
    }

    fun e(tag: String, msg: String, th: Throwable) {
        android.util.Log.e(tag, msg, th)

        tracker.send(HitBuilders.ExceptionBuilder()
                .setDescription(th.message + " : " + getStackTrace(th))
                .setFatal(false)
                .build())
    }

    private fun getStackTrace(throwable: Throwable): String {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        throwable.printStackTrace(printWriter)
        return stringWriter.toString()
    }
}
