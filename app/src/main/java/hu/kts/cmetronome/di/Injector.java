package hu.kts.cmetronome.di;


import android.app.Application;

import hu.kts.cmetronome.BuildConfig;
import hu.kts.cmetronome.appindexing.AppIndexing;
import hu.kts.cmetronome.appindexing.AppIndexingImpl;
import hu.kts.cmetronome.appindexing.AppIndexingStub;
import hu.kts.cmetronome.ui.workout.WorkoutActivity;

public class Injector {

    private static AppIndexing appIndexing;

    public static void init(Application context) {
        appIndexing = BuildConfig.DEBUG ? new AppIndexingStub() : new AppIndexingImpl(context);
    }

    public static void inject(WorkoutActivity workoutActivity) {
        workoutActivity.setAppIndexing(appIndexing);
    }

}
