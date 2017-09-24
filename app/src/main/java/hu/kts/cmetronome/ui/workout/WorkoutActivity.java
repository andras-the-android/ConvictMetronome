package hu.kts.cmetronome.ui.workout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import hu.kts.cmetronome.R;
import hu.kts.cmetronome.Settings;
import hu.kts.cmetronome.admob.AdViewWrapper;
import hu.kts.cmetronome.appindexing.AppIndexing;
import hu.kts.cmetronome.di.Injector;
import hu.kts.cmetronome.ui.settings.SettingsActivity;
import lombok.Setter;

public class WorkoutActivity extends AppCompatActivity {


    @Setter private WorkoutController workoutController;
    @Setter private AppIndexing appIndexing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);
        Injector.inject(this);

        getLifecycle().addObserver(new AdViewWrapper(findViewById(R.id.adView)));
        getLifecycle().addObserver(appIndexing);
        getLifecycle().addObserver(workoutController);
    }

    @OnClick(R.id.rep_counter)
    public void onRepCounterClick(View view) {
        workoutController.onRepCounterClick();
    }

    @OnLongClick(R.id.rep_counter)
    public boolean onRepCounterLongClick(View view) {
        return workoutController.onRepCounterLongClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_settings) {
            openSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        startActivityForResult(new Intent(this, SettingsActivity.class), Settings.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Settings.REQUEST_CODE && resultCode == RESULT_OK) {
            workoutController.initSettingsRelatedParts();
        }
    }

}
