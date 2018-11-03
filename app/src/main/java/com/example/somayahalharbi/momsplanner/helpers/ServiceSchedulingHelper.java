package com.example.somayahalharbi.momsplanner.helpers;

import android.content.Context;

import com.example.somayahalharbi.momsplanner.services.AppointmentsReminderService;
import com.example.somayahalharbi.momsplanner.services.OverdueTasksService;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ServiceSchedulingHelper {

    public static void scheduleAppointmentReminderService(Context context){

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
//----------- Runs everyday to noon-------------

        Calendar now = new GregorianCalendar();
        Calendar noon = new GregorianCalendar();
        noon.set(Calendar.HOUR_OF_DAY, 12);
        noon.set(Calendar.MINUTE, 0);
        noon.set(Calendar.SECOND, 0);
        noon.set(Calendar.MILLISECOND, 0);

        int startTime = (int) ((now.getTimeInMillis() - noon.getTimeInMillis()) / 1000);
        if (startTime < 0) {
            noon.add(Calendar.DAY_OF_MONTH, 1);
            startTime = (int) ((noon.getTimeInMillis() - now.getTimeInMillis()) / 1000);
        }
        int endTime = startTime + 300;


        Job appointmentsReminderJob = dispatcher.newJobBuilder()
                .setService(AppointmentsReminderService.class)
                .setTag("appointment-reminder")
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(startTime, endTime))
                .setReplaceCurrent(false)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();

        dispatcher.mustSchedule(appointmentsReminderJob);


    }
    public static void scheduleOverdueTaskNotificationService(Context context){

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        //---------------- Runs everyday at midnight---------------------
        Calendar now = new GregorianCalendar();
        Calendar midnight = new GregorianCalendar();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);
        midnight.add(Calendar.DAY_OF_MONTH, 1);

        int startTime = (int) ((midnight.getTimeInMillis() - now.getTimeInMillis()) / 1000);
        int endTime = startTime + 300;

        Job overdueTaskJob = dispatcher.newJobBuilder()
                .setService(OverdueTasksService.class)
                .setTag("overdue-tasks-reminder")
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(startTime, endTime))
                .setReplaceCurrent(false)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();


        dispatcher.mustSchedule(overdueTaskJob);



    }
}
