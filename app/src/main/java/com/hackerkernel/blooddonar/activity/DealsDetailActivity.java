package com.hackerkernel.blooddonar.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
import com.hackerkernel.blooddonar.parser.JsonParser;
import com.hackerkernel.blooddonar.pojo.DetailDealsPojo;
import com.hackerkernel.blooddonar.storage.MySharedPreferences;
import com.hackerkernel.blooddonar.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DealsDetailActivity extends Activity {
    private String mDealsId;
    private MySharedPreferences sp;
    private RequestQueue mRequestQueue;

    @Bind(R.id.detail_hospital_name) TextView mHospitalName;
    @Bind(R.id.detail_off) TextView mPercentOff;
    @Bind(R.id.detail_description) TextView mDescription;
    @Bind(R.id.detail_timing) TextView timings;
    @Bind(R.id.original_price) TextView mOrignalPrice;
    @Bind(R.id.detail_special_price) TextView mOfferPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);
        ButterKnife.bind(this);

        if (getIntent().hasExtra(Constants.COM_ID)) {
            mDealsId = getIntent().getStringExtra(Constants.COM_ID);
        }else{
            Toast.makeText(getApplicationContext(),"Unable to open deal. Try Again Later",Toast.LENGTH_LONG).show();
            finish();
        }

        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(this);

        checkInternetAndFetchDealData();
    }

    private void checkInternetAndFetchDealData() {
        if (Util.isNetworkAvailable()) {
            fetchDataInBackGround();
        }else {

        }
    }

    private void fetchDataInBackGround() {
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_DEALS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG","MUR::"+response);
                parseDealsData(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY,Util.generateApiKey(sp.getUserMobile()));
                params.put(Constants.COM_MOBILE,sp.getUserMobile());
                params.put(Constants.COM_ID, mDealsId);
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseDealsData(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            JSONArray data = obj.getJSONArray(Constants.COM_DATA);
            DetailDealsPojo pojo = JsonParser.ParseDetailDeals(data);
            mHospitalName.setText(pojo.getLabName());
            mPercentOff.setText(pojo.getOff());
            mDescription.setText(pojo.getDescription());
            mOrignalPrice.setText(pojo.getOrignal_prize());
            mOfferPrice.setText(pojo.getSpecial_prize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
