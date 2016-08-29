package hu.kts.cmetronome.appindexing;

import android.content.Context;

import hu.kts.cmetronome.BuildConfig;

/**
 * Created by guni8 on 2016. 08. 29..
 */
public interface AppIndexing {
    void onStart();

    void onStop();

    public class Factory {
        public static AppIndexing get(Context context) {

            if (BuildConfig.DEBUG) {

                return new AppIndexingStub();
            }
            return new AppIndexingImpl(context);
        }
    }
}
