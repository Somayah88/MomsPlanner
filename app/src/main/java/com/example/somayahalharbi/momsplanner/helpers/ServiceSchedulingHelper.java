package com.example.somayahalharbi.momsplanner.helpers;

import android.content.Context;
import android.util.Log;

import com.example.somayahalharbi.momsplanner.services.AppointmentsReminderService;
import com.example.somayahalharbi.momsplanner.services.OverdueTasksService;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.Calendar;

public class ServiceSchedulingHelper {

    public static void scheduleAppointmentReminderService(Context context){

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        // Source for the time code https://stackoverflow.com/questions/45836789/android-how-schedule-task-for-every-night-with-jobdispatcher-api?rq=1
        Calendar now = Calendar.getInstance();
        Calendar midNight = Calendar.getInstance();
        midNight.set(Calendar.HOUR_OF_DAY, 12);
        midNight.set(Calendar.MINUTE, 0);
        midNight.set(Calendar.SECOND, 0);
        midNight.set(Calendar.MILLISECOND, 0);
        midNight.set(Calendar.AM_PM, Calendar.AM);

        long diff = now.getTimeInMillis() - midNight.getTimeInMillis();

        if (diff < 0) {
            midNight.add(Calendar.DAY_OF_MONTH, 1);
            diff = midNight.getTimeInMillis() - now.getTimeInMillis();
        }

        int startSeconds = (int) (diff / 1000); // tell the start seconds
        int endSeconds = startSeconds + 300; // within Five minutes

        Log.d("MainActivity","JobDispatcher start Time "+ startSeconds+" end seconds "+endSeconds);

        /* Calendar now = new GregorianCalendar();
        Calendar tomorrowAfterMidnight = new GregorianCalendar();
        tomorrowAfterMidnight.set(Calendar.HOUR_OF_DAY, 0);
        tomorrowAfterMidnight.set(Calendar.MINUTE, 0);
        tomorrowAfterMidnight.set(Calendar.SECOND, 0);
        tomorrowAfterMidnight.set(Calendar.MILLISECOND, 0);
        tomorrowAfterMidnight.add(Calendar.DAY_OF_MONTH, 1);

        int secondsUntilMidnight = (int) ((tomorrowAfterMidnight.getTimeInMillis() - now.getTimeInMillis()) / 1000);
*/

        Job myJob = dispatcher.newJobBuilder()
                .setService(AppointmentsReminderService.class)
                .setTag("appointment-reminder")
                .setRecurring(false)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(startSeconds, endSeconds))
                .setReplaceCurrent(false)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();


        dispatcher.mustSchedule(myJob);


    }
    public static void scheduleOverdueTaskNotificationService(Context context){

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        //TODO: do it every day at midnight


        Job myJob = dispatcher.newJobBuilder()
                .setService(OverdueTasksService.class)
                .setTag("overdue_tasks-reminder")
                .setRecurring(false)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(0, 60))
                .setReplaceCurrent(false)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();


        dispatcher.mustSchedule(myJob);



    }
}
