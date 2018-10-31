package com.example.somayahalharbi.momsplanner.helpers;

import android.app.Activity;
import android.util.Log;

import com.example.somayahalharbi.momsplanner.widget.MomsPlannerWidgetProvider;

public class WidgetUpdateHelper {

    public static void updateWidgetData(final Activity activity) {
        Log.w("to do activity", "update widget data is just called");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MomsPlannerWidgetProvider.sendRefreshBroadcast(activity);
            }
        });
    }
}
