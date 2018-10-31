package com.example.somayahalharbi.momsplanner.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.somayahalharbi.momsplanner.R;
import com.example.somayahalharbi.momsplanner.ToDoActivity;

/**
 * Implementation of App Widget functionality.
 */
//TODO: more testing to make sure it behaves well
    //TODO: change the prevew

public class MomsPlannerWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Log.w("Widget Provider", "Update App widget is executed");

        CharSequence widgetText = context.getString(R.string.appwidget_text);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.moms_planner_widget);
        views.setTextViewText(R.id.appwidget_title, widgetText);
        Intent toDoIntent = new Intent(context, ToDoActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, toDoIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_linear_layout, pendingIntent);
        Intent intent = new Intent(context, MomsPlannerRemoteViewService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        views.setRemoteAdapter(R.id.widget_list_view, intent);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list_view);
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, MomsPlannerWidgetProvider.class));
        context.sendBroadcast(intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);

        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        final String action = intent.getAction();
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            Log.w("Widget Provider", "Broadcast is recieved");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, MomsPlannerWidgetProvider.class);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(cn), R.id.widget_list_view);
        }


    }
}

