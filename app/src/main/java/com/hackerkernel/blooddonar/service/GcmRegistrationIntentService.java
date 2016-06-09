package com.hackerkernel.blooddonar.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.constant.EndPoints;
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.parser.JsonParser;
import com.hackerkernel.blooddonar.pojo.SimplePojo;
import com.hackerkernel.blooddonar.storage.MySharedPreferences;
import com.hackerkernel.blooddonar.util.Util;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to obtain GCM reg token
 */
public class GcmRegistrationIntentService extends IntentService {

    private static final String TAG = GcmRegistrationIntentService.class.getSimpleName();
    private RequestQueue mRequestQueue;
    MySharedPreferences sp;

    public GcmRegistrationIntentService() {
        super(TAG);
        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(this);
    }

    public static final String KEY = "key";
    public static final String TOPIC = "topic";
    public static final String SUBSCRIBE = "subscribe";
    public static final String UNSUBSCRIBE = "unsubscribe";

    @Override
    protected void onHandleIntent(Intent intent) {
        String key = intent.getStringExtra(KEY);
        switch (key){
            case SUBSCRIBE:
                String topic = intent.getStringExtra(TOPIC);
                subscribeToTopic(topic);
                break;
            default:
                //if key is not specified register with GCM
                registerGCM();
                break;
        }
    }

    /*
    * Registering with GCM and obtaining the GCM registration ID
    * */
    private void registerGCM() {
        String token = null;
        try{
            InstanceID instanceID = InstanceID.getInstance(this);
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE,null);

            Log.e(TAG,"HUS: GCM reg token "+token);

            //send registration token to server
            sendTokenToServer(token);

            sp.setBooleanKey(MySharedPreferences.KEY_SENT_GCM_TOKEN_TO_SERVER);

        }catch (Exception e){
            Log.e(TAG, "HUS: Failed to complete token refresh", e);
            sp.setBooleanKey(MySharedPreferences.KEY_SENT_GCM_TOKEN_TO_SERVER,false);
        }
        //notify UI that registration has completed, so that progress indicator can be hidden
        Intent registrationCompleted = new Intent(Constants.REGISTRATION_COMPLETE);
        registrationCompleted.putExtra("token",token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationCompleted);
    }

    /*
    * Method to save GCM to Server
    * */
    private void sendTokenToServer(final String token) {
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.UPDATE_GCM_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"HUS: sendTokenToServer: "+response);
                try {
                    SimplePojo current = JsonParser.SimpleParser(response);
                    if (current.isReturned()){
                        Intent registrationComplete = new Intent(Constants.SENT_TOKEN_TO_SERVER);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(registrationComplete);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG,"HUS: sendTokenToServer: Unable to parse json response "+e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,"HUS: sendTokenToServer: "+error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Constants.COM_MOBILE,sp.getUserMobile());
                params.put(Constants.COM_APIKEY, Util.generateApiKey(sp.getUserMobile()));
                params.put(Constants.COM_GCM_REG_ID,token);
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    /*
    * subscribe to topic
    * */
    public void subscribeToTopic(String topic){
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        InstanceID instanceID = InstanceID.getInstance(this);
        String token = null;
        try{
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE,null);
            if (token != null){
                pubSub.subscribe(token,"/topics/"+topic,null);
                Log.e(TAG,"HUS: Subscribed to topic: " + topic);
            }else{
                Log.e(TAG,"HUS: GCM registration id is null");
            }
        }catch (Exception e){
            Log.e(TAG, "HUS: Topic subscribe error. Topic: " + topic + ", error: " + e.getMessage());
        }
    }

    public void unsubscribeFromTopic(String topic) {
        GcmPubSub pubSub = GcmPubSub.getInstance(getApplicationContext());
        InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
        String token = null;
        try {
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            if (token != null) {
                pubSub.unsubscribe(token, topic);
                Log.e(TAG, "Unsubscribed from topic: " + topic);
            } else {
                Log.e(TAG, "error: gcm registration id is null");
            }
        } catch (IOException e) {
            Log.e(TAG, "Topic unsubscribe error. Topic: " + topic + ", error: " + e.getMessage());
        }
    }
}
