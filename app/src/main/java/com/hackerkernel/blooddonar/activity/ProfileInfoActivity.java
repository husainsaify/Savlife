package com.hackerkernel.blooddonar.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.constant.EndPoints;
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.storage.MySharedPreferences;
import com.hackerkernel.blooddonar.util.Util;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileInfoActivity extends AppCompatActivity {

    private ImageView mProfileImage;

    private TextView mProfileName;
    private TextView mProfileBlood;
    private TextView mProfileNum;
    private TextView mProfileLastDonated;
    private TextView mRelative;
    private TextView mProfileGender;
    private TextView mProfileAge;
    private TextView mProfileID;
    private TextView mProfileCity;

    private MySharedPreferences sp;
    private RequestQueue mRequestQue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);
        mProfileImage = (ImageView) findViewById(R.id.profile_imageView);
        mProfileName = (TextView) findViewById(R.id.profile_name);
        mProfileBlood = (TextView) findViewById(R.id.profile_blood_grup);
        mProfileNum = (TextView) findViewById(R.id.profile_Num);
        mProfileLastDonated = (TextView) findViewById(R.id.profile_last_donated);
        mProfileAge = (TextView) findViewById(R.id.profile_age);
        mProfileCity = (TextView) findViewById(R.id.profile_city);
        mProfileGender = (TextView) findViewById(R.id.profile_gender);
        mProfileID = (TextView) findViewById(R.id.profile_id);
        sp = MySharedPreferences.getInstance(getApplicationContext());
        mRequestQue = MyVolley.getInstance().getRequestQueue();
        ifNetworkIsAvailable();

    }

    private void ifNetworkIsAvailable() {
        if (Util.isNetworkAvailable()) {
            parseProfileInfoInBackground();

        } else {
            Util.noInternetSnackBar(this, mRelative);
        }
    }

    private void parseProfileInfoInBackground() {
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_DONOR_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Tag", "MUR:" + response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY, Util.generateApiKey(sp.getUserMobile()));
                params.put(Constants.COM_MOBILE, sp.getUserMobile());
                return params;
            }
        };
        mRequestQue.add(request);
    }

}
