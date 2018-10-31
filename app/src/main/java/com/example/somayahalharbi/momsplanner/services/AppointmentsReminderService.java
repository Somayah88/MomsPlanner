package com.example.somayahalharbi.momsplanner.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.somayahalharbi.momsplanner.helpers.NotificationsHelper;
import com.example.somayahalharbi.momsplanner.models.Appointment;
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

import static com.example.somayahalharbi.momsplanner.AppointmentsActivity.APPOINTMENT_PATH;


public class AppointmentsReminderService extends JobService {
    private static FirebaseDatabase database;
    DatabaseReference apptRef;
    FirebaseUser user;
    FirebaseAuth mFirebaseAuth;
    private ArrayList<Appointment> appointmentList;
    private static final int NOTI_APPOINTMENT = 110;



    @Override
    public boolean onStartJob(JobParameters job) {
        //*********************************************************
        String myFormat = "MM/dd/yy";
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Calendar calendar = Calendar.getInstance();
        final Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 2);
        final Date twoDaysLater = calendar.getTime();
        //********************************************************

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        if (database == null) {
            database = FirebaseDatabase.getInstance();
        }
        apptRef = database.getReference("users").child(user.getUid()).child(APPOINTMENT_PATH);
        apptRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList = new ArrayList<>();
                for (DataSnapshot apptSnapshot : dataSnapshot.getChildren()) {
                    Appointment appt = apptSnapshot.getValue(Appointment.class);
                    try {
                        Date strDate = sdf.parse(appt.getApptDate());
                        if ( strDate.getTime()>=today.getTime() && strDate.getTime()<=twoDaysLater.getTime())
                                appointmentList.add(appt);
                    }
                    catch (ParseException e) {
                    }


                }
                Log.d("AppointmentService", "Number of upcoming appointments "+ appointmentList.size()+" ");
                if(appointmentList.size()>0)
                {
                    NotificationsHelper notifications=new NotificationsHelper(getApplicationContext());
                    notifications.notify(NOTI_APPOINTMENT,notifications.getAppointmentNotifications(appointmentList.size()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("AppointmentReminder", "loadAppointments:onCancelled", databaseError.toException());


            }
        });


        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
