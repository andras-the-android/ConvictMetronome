package hu.kts.cmetronome;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by andrasnemeth on 16/02/16.
 */
public class AppIndexing {

    private GoogleApiClient client;
    private String appName;

    public AppIndexing(Context context) {
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

    public void onStart() {
        client.connect();
        AppIndex.AppIndexApi.start(client, getAction());
    }

    public void onStop() {
        AppIndex.AppIndexApi.end(client, getAction());
        client.disconnect();
    }

}
