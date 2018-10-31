package com.example.somayahalharbi.momsplanner.services;

import android.support.annotation.NonNull;
import android.util.Log;
import com.example.somayahalharbi.momsplanner.helpers.NotificationsHelper;
import com.example.somayahalharbi.momsplanner.models.ToDo;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.somayahalharbi.momsplanner.ToDoActivity.TO_DO_NODE;


public class OverdueTasksService extends JobService {
    private static FirebaseDatabase database;
    DatabaseReference toDoRef;
    FirebaseUser user;
    FirebaseAuth mFirebaseAuth;
    private ArrayList<ToDo> toDoList;
    private static final int NOTI_OVERDUE_TASK = 220;




    @Override
    public boolean onStartJob(JobParameters job) {


            //*********************************************************
            String myFormat = "MM/dd/yy";
            final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            Calendar calendar = Calendar.getInstance();
            final Date today = calendar.getTime();
            //********************************************************

            mFirebaseAuth = FirebaseAuth.getInstance();
            user = mFirebaseAuth.getCurrentUser();
            if (database == null) {
                database = FirebaseDatabase.getInstance();
            }
            toDoRef = database.getReference("users").child(user.getUid()).child(TO_DO_NODE);
            toDoRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    toDoList = new ArrayList<>();
                    for (DataSnapshot apptSnapshot : dataSnapshot.getChildren()) {
                        ToDo toDo = apptSnapshot.getValue(ToDo.class);
                        if(!toDo.getDueBy().isEmpty()) {
                            try {
                                Date strDate = sdf.parse(toDo.getDueBy());
                                if (strDate.getTime() < today.getTime())
                                    toDoList.add(toDo);
                            } catch (ParseException e) {
                            }
                        }


                    }
                    Log.d("toDoTaskService", "Number of overdue tasks " + toDoList.size() + " ");
                    if (toDoList.size() > 0) {

                        NotificationsHelper notifications = new NotificationsHelper(getApplicationContext());
                        notifications.notify(NOTI_OVERDUE_TASK, notifications.getOverDueTasksNotifications(toDoList.size()));
                    }
                }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("OverDueTasksService", "loadOverdueTasks:onCancelled", databaseError.toException());


                    }
                });


                    return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
