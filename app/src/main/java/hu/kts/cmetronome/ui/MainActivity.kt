package hu.kts.cmetronome.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import hu.kts.cmetronome.R

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val navController = findNavController(R.id.main_nav_controller)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener { _, _, _ -> invalidateOptionsMenu()}
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val navController = findNavController(R.id.main_nav_controller)
        when (navController.currentDestination?.id) {
            R.id.dest_workout -> menuInflater.inflate(R.menu.main, menu)
            else -> menu.clear()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.main_nav_controller)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.main_nav_controller).navigateUp()
    }
}
