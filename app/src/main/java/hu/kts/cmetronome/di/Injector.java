package hu.kts.cmetronome.di;


import android.app.Application;

import hu.kts.cmetronome.BuildConfig;
import hu.kts.cmetronome.Settings;
import hu.kts.cmetronome.appindexing.AppIndexing;
import hu.kts.cmetronome.appindexing.AppIndexingImpl;
import hu.kts.cmetronome.appindexing.AppIndexingStub;
import hu.kts.cmetronome.repository.WorkoutRepository;
import hu.kts.cmetronome.ui.workout.WorkoutActivity;
import hu.kts.cmetronome.ui.workout.WorkoutController;

public class Injector {

    private static AppIndexing appIndexing;
    private static Settings settings;
    private static WorkoutRepository workoutRepository;

    public static void init(Application context) {
        appIndexing = BuildConfig.DEBUG ? new AppIndexingStub() : new AppIndexingImpl(context);
        settings = new Settings(context);
        workoutRepository = new WorkoutRepository();
    }

    public static void inject(WorkoutActivity workoutActivity) {
        workoutActivity.setAppIndexing(appIndexing);
        workoutActivity.setWorkoutController(new WorkoutController(workoutActivity, workoutRepository, settings));
    }

}
