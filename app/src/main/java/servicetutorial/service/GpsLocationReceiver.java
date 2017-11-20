package servicetutorial.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by yubaraj on 11/8/17.
 */

public class GpsLocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            Toast.makeText(context, "in android.location.PROVIDERS_CHANGED",
                    Toast.LENGTH_SHORT).show();
            Intent pushIntent = new Intent(context, GoogleService.class);
            context.startService(pushIntent);
        }else {
            Intent pushIntent = new Intent(context, GoogleService.class);
            context.stopService(pushIntent);
        }
    }
}