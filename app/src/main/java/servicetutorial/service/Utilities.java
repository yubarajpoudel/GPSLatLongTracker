package servicetutorial.service;

import android.content.Context;
import android.os.BatteryManager;
import android.util.Log;

import static android.content.Context.BATTERY_SERVICE;

/**
 * Created by yubaraj on 11/20/17.
 */

public class Utilities {
    public static int getBatteryPercentage(Context context) {
        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        Log.d("Utilities", "batlevel = " + batLevel);
        return batLevel;
    }
}
