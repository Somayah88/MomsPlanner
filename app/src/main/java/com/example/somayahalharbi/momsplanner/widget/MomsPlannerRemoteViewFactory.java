package com.example.somayahalharbi.momsplanner.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.somayahalharbi.momsplanner.R;
import com.example.somayahalharbi.momsplanner.models.ToDo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.somayahalharbi.momsplanner.ToDoActivity.TO_DO_NODE;

public class MomsPlannerRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {
    private static FirebaseDatabase mDatabase;
    private Context mContext;
    private int appWidgetId;
    private ArrayList<ToDo> toDoList;
    private FirebaseUser mUser;
    private FirebaseAuth mFirebaseAuth;


    public MomsPlannerRemoteViewFactory(Context context, Intent intent) {
        this.mContext = context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        toDoList = new ArrayList<>();
        Log.w("widget factory", "widget factory created");

    }

    @Override
    public void onCreate() {
        Log.w("Widget Factory", "update data called in OnCreate");

        updateData();

    }

    @Override
    public void onDataSetChanged() {
        Log.w("Widget Factory", "update data called in OnDataSetChanged");

        updateData();

    }

    @Override
    public void onDestroy() {
        toDoList.clear();

    }

    @Override
    public int getCount() {
        return toDoList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews views = new RemoteViews((mContext.getPackageName()), R.layout.widget_list_item);
        views.setTextViewText(R.id.widget_to_do_task, toDoList.get(position).getToDo());
        if (!toDoList.get(position).getDueBy().equals(""))
            views.setTextViewText(R.id.widget_due_by_date, toDoList.get(position).getDueBy());
        else
            views.setTextViewText(R.id.widget_due_by_date, mContext.getResources().getString(R.string.due_by_default));

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private void updateData() {

        DatabaseReference toDoRef;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUser = mFirebaseAuth.getCurrentUser();
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        toDoRef = mDatabase.getReference("users").child(mUser.getUid()).child(TO_DO_NODE);
        toDoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                toDoList = new ArrayList<>();
                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    ToDo todo = taskSnapshot.getValue(ToDo.class);
                    toDoList.add(todo);
                }
                Log.w("Widget Factory", "Widget Updated");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Widget", "loadToDos:onCancelled", databaseError.toException());


            }
        });


    }
}
