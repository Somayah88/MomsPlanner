package com.example.somayahalharbi.momsplanner.helpers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.somayahalharbi.momsplanner.widget.MomsPlannerWidgetProvider;

public class WidgetUpdateHelper {

    public static void updateWidgetData(final Context context) {
        Activity activity=(Activity)context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MomsPlannerWidgetProvider.sendRefreshBroadcast(context);
            }
        });
    }
}
