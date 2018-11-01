package com.example.somayahalharbi.momsplanner.helpers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.somayahalharbi.momsplanner.widget.MomsPlannerWidgetProvider;

public class WidgetUpdateHelper {

    public static void updateWidgetData(final Context context) {
        Activity activity=(Activity)context;
        Log.w("to do activity", "update widget data is just called");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MomsPlannerWidgetProvider.sendRefreshBroadcast(context);
            }
        });
    }
}
