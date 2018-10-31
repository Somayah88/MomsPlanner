package com.example.somayahalharbi.momsplanner.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.somayahalharbi.momsplanner.AppointmentsActivity;
import com.example.somayahalharbi.momsplanner.R;
import com.example.somayahalharbi.momsplanner.ToDoActivity;

public class NotificationsHelper extends ContextWrapper {
    private NotificationManager notificationManager;
    public static final String APPOINTMENT_CHANNEL_ID="appointment_channel";
    public static final String TODO_CHANNEL_ID="to_do_channel_id";

    public NotificationsHelper(Context context) {
        super(context);
        if(notificationManager==null)
        {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel appointmentsChannel = new NotificationChannel(APPOINTMENT_CHANNEL_ID, getResources().getString(R.string.appointment_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
           notificationManager.createNotificationChannel(appointmentsChannel);
           NotificationChannel toDoChannel=new NotificationChannel(TODO_CHANNEL_ID,getResources().getString(R.string.to_do_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
           notificationManager.createNotificationChannel(toDoChannel);

        }

    }

    public void notify(int id, NotificationCompat.Builder notification) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(id, notification.build());

    }
    public NotificationCompat.Builder getAppointmentNotifications(int apptNo){
        Intent intent = new Intent(this, AppointmentsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        return new NotificationCompat.Builder(getApplicationContext(),APPOINTMENT_CHANNEL_ID)
                .setContentTitle(getResources().getString(R.string.appointment_notification_title))
                .setContentText(String.format(getResources().getString(R.string.appointments_notifications_content),apptNo))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground);


    }
    public NotificationCompat.Builder getOverDueTasksNotifications(int count){
        Intent intent = new Intent(this, ToDoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        return new NotificationCompat.Builder(getApplicationContext(),TODO_CHANNEL_ID)
                .setContentTitle(getResources().getString(R.string.overdue_tasks_notification_title))
                .setContentText(String.format(getResources().getString(R.string.overdue_tasks_notifications_content),count))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent);



    }

}
