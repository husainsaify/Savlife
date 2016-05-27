package com.hackerkernel.blooddonar.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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

public class DetailDealsActivity extends Activity {
    private String mDealsId;
    private MySharedPreferences sp;
    private RequestQueue requestQue;
    @Bind(R.id.detail_hospital_name)
    TextView hospitalName;
    @Bind(R.id.detail_offer) TextView percentOFF;
    @Bind(R.id.detail_hospital_description) TextView description;
    @Bind(R.id.detail_hospital_timing) TextView timmings;
    @Bind(R.id.detail_cut_text) TextView orignalPrize;
    @Bind(R.id.detail_prize_text) TextView offerPrize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);
        ButterKnife.bind(this);
        if (getIntent().hasExtra(Constants.COM_ID)) {
            mDealsId = getIntent().getStringExtra(Constants.COM_ID);
        }else{
            //TODO:: close activity
        }

        requestQue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(this);

        checkInternetAndFetchDealData();
    }

    private void checkInternetAndFetchDealData() {
        if (Util.isNetworkAvailable()) {
            fetchDataInBackGround();
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
        requestQue.add(request);
    }

    private void parseDealsData(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            JSONArray data = obj.getJSONArray(Constants.COM_DATA);
            DetailDealsPojo pojo = JsonParser.ParseDetailDeals(data);
            hospitalName.setText(pojo.getLabName());
            percentOFF.setText(pojo.getOff());
            description.setText(pojo.getDescription());
            orignalPrize.setText(pojo.getOrignal_prize());
            offerPrize.setText(pojo.getSpecial_prize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
