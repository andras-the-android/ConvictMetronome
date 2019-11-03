package hu.kts.cmetronome.ui.workout

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import hu.kts.cmetronome.R
import hu.kts.cmetronome.admob.AdViewWrapper
import hu.kts.cmetronome.di.Injector
import hu.kts.cmetronome.ui.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_workout.*

class WorkoutActivity : AppCompatActivity() {


    lateinit var workoutController: WorkoutController
    lateinit var whatsNew: WhatsNew

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        Injector.inject(this)

        lifecycle.addObserver(AdViewWrapper(adView))
        lifecycle.addObserver(workoutController)
        
        repCounterTextView.setOnClickListener { workoutController.onRepCounterClick() }
        repCounterTextView.setOnLongClickListener { workoutController.onRepCounterLongClick() }

        whatsNew.run()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_settings) {
            openSettings()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun openSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }
}
