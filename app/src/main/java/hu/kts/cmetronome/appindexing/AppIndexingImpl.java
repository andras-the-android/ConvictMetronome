package hu.kts.cmetronome.appindexing;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.net.Uri;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import hu.kts.cmetronome.R;

public class AppIndexingImpl implements AppIndexing {

    private GoogleApiClient client;
    private String appName;

    public AppIndexingImpl(Context context) {
        client = new GoogleApiClient.Builder(context).addApi(AppIndex.API).build();
        appName = context.getString(R.string.app_name);
    }

    private Action getAction() {
        return Action.newAction(
                Action.TYPE_ACTIVATE,
                appName,
                Uri.parse("android-app://hu.kts.cmetronome/http/convictmetronome.wordpress.com")
        );
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        client.connect();
        AppIndex.AppIndexApi.start(client, getAction());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        AppIndex.AppIndexApi.end(client, getAction());
        client.disconnect();
    }

}
