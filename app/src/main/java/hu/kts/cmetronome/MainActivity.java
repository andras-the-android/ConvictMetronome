package hu.kts.cmetronome;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class MainActivity extends AppCompatActivity {


    @InjectView(R.id.adView)
    AdView adView;

    private WorkoutController workoutController;
    private AppIndexing appIndexing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ButterKnife.inject(this);
        workoutController = new WorkoutController(this, savedInstanceState);
        setupAd();
        appIndexing = new AppIndexing(this);
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
