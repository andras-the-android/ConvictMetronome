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
    private Action viewAction;

    public AppIndexing(Context context) {
        client = new GoogleApiClient.Builder(context).addApi(AppIndex.API).build();
        viewAction = Action.newAction(
                Action.TYPE_ACTIVATE,
                context.getString(R.string.app_name),
                Uri.parse("android-app://hu.kts.cmetronome/")
        );
    }

    public void onStart() {
        client.connect();
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    public void onStop() {
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


}
