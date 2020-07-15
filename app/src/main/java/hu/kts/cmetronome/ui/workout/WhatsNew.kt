package hu.kts.cmetronome.ui.workout

import android.app.Activity
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import hu.kts.cmetronome.BuildConfig
import hu.kts.cmetronome.R
import hu.kts.cmetronome.Settings

class WhatsNew(val activity: Activity, val settings: Settings) {

    fun run() {
        if (settings.whatsNewVersion < LATEST_VERSION_WITH_WHATS_NEW_RECORD) {
            val view = activity.layoutInflater.inflate(R.layout.view_whats_new, null)
            val textView = view.findViewById<TextView>(R.id.releaseNotes)

            val inputStream = activity.resources.openRawResource(R.raw.release_notes)
            val bytes = ByteArray(inputStream.available())
            inputStream.read(bytes)

            //TODO improve html processing https://stackoverflow.com/questions/11214001/show-ul-li-in-android-textview
            textView.text = HtmlCompat.fromHtml(String(bytes), HtmlCompat.FROM_HTML_MODE_COMPACT)

            AlertDialog.Builder(activity)
                    .setView(view)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss(); }
                    .show()

            runMigrations()
            settings.updateWhatsNewVersion()
        }
    }

    private fun runMigrations() {
        for (version in (settings.whatsNewVersion + 1)..BuildConfig.VERSION_CODE) {
            when (version) {
                11 -> settings.runMigration11()
            }
        }
    }

    companion object {
        private const val LATEST_VERSION_WITH_WHATS_NEW_RECORD = 13
    }
}