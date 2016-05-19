package com.hackerkernel.blooddonar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.hackerkernel.blooddonar.activity.OtpVerificationActivity;
import com.hackerkernel.blooddonar.constant.Constants;


/**
 * Class to read OTP sms automatically
 */
public class IncomingOtp extends BroadcastReceiver {

    private static final String TAG = IncomingOtp.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        try{
            if (bundle != null){
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                //check pdubsObj is not null
                if (pdusObj != null){
                    for (Object aPdusObj : pdusObj){

                        SmsMessage currentMessage;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            String format = bundle.getString("format");
                            currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj,format);
                        }else{
                            currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                        }

                        //create a valid senderAddress for our SMS gateway
                        String oldSenderAddress = currentMessage.getOriginatingAddress();
                        String newSenderAddress = "";

                        if (oldSenderAddress != null && oldSenderAddress.contains("-")){
                            newSenderAddress = oldSenderAddress.split("-")[1];
                        }
                        String message = currentMessage.getDisplayMessageBody();

                        Log.d(TAG,"HUS: Recieved SMS: "+message+", sender: "+oldSenderAddress +", newSenderaddress "+newSenderAddress);

                        //if sms is not from our gateway ignore the sms
                        if (!newSenderAddress.toLowerCase().equals(Constants.CONFIG_OTP_SENDER_ID.toLowerCase())){
                            Log.d(TAG,"HUS: onReceive SMS not from our gateway");
                            return;
                        }

                        String verificationCode = getVerificationCode(message);

                        Log.d(TAG,"HUS: onReceive: Verification code "+verificationCode);
                        OtpVerificationActivity.getInstace().updateUI(verificationCode);
                    }
                }else{
                    Log.e(TAG,"HUS: onReceive: pdusObj is null");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,"HUS: onReceive: exception "+e.getMessage());
        }
    }

    /*
    * Method to get verification code from the sms
    * */
    private String getVerificationCode(String message){
        int index = message.indexOf(Constants.CONFIG_SMS_DELIMITER);

        if (index != -1){
            int start = index + 2;
            int length = 4;
            return  message.substring(start,start+length);

        }
        return null;
    }
}