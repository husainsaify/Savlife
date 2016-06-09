package com.hackerkernel.blooddonar.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.activity.HomeActivity;

import java.util.Random;


/**
 * Class to show notification
 */
public class NotificationUtil {
    private Context context;
    private Random random;

    //notification fields
    private int NOTIFICATION_ID = (int) System.currentTimeMillis();
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    //notification sound URI
    private Uri sound;

    public NotificationUtil(Context context){
        this.context = context;
        this.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        this.random = new Random();

        //init notification
        this.notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.builder = new NotificationCompat.Builder(context)
                .setSound(sound)
                .setAutoCancel(true);
    }

    public void sendReferSuccessNotification(String title,String message){
        //Create intent
        Intent intent = new Intent(context, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .setContentIntent(pendingIntent);

        NOTIFICATION_ID += random.nextInt(5000);

        notificationManager.notify(NOTIFICATION_ID,builder.build());
    }

}
