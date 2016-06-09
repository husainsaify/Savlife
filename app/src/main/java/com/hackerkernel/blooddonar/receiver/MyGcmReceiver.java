package com.hackerkernel.blooddonar.receiver;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.util.NotificationUtil;


/**
 * Class to listen for GCM push notifications
 */
public class MyGcmReceiver extends GcmListenerService {
    public static final String TAG = MyGcmReceiver.class.getSimpleName();
    private int TYPE_REFER_PUSH = 0;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        //get title,message & type
        String title = data.getString(Constants.COM_TITLE);
        String message = data.getString(Constants.COM_MESSAGE);
        int type = data.getInt(Constants.COM_PUSH_TYPE);

        //check which type of notification it is
        if (type == TYPE_REFER_PUSH){
            NotificationUtil notification = new NotificationUtil(this);
            notification.sendReferSuccessNotification(title,message);
        }
    }
}
