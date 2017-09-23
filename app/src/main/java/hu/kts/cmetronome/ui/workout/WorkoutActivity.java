package hu.kts.cmetronome.ui.workout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import hu.kts.cmetronome.AdMobTestDeviceFilteredBuilderFactory;
import hu.kts.cmetronome.R;
import hu.kts.cmetronome.Settings;
import hu.kts.cmetronome.appindexing.AppIndexing;
import hu.kts.cmetronome.ui.settings.SettingsActivity;

public class WorkoutActivity extends AppCompatActivity {


    @BindView(R.id.adView)
    AdView adView;

    private WorkoutController workoutController;
    private AppIndexing appIndexing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.bind(this);
        workoutController = new WorkoutController(this, savedInstanceState);
        setupAd();
        appIndexing = AppIndexing.Factory.get(this);
    }

    private void setupAd() {
        AdRequest adRequest = AdMobTestDeviceFilteredBuilderFactory.get().build();
        adView.loadAd(adRequest);
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
    public void onResume() {
        super.onResume();
        adView.resume();
    }

    @Override
    public void onPause() {
        adView.pause();
        workoutController.pauseWorkout();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        workoutController.onDestroy();
        adView.destroy();
        super.onDestroy();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        workoutController.saveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        appIndexing.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        appIndexing.onStop();
    }
}
