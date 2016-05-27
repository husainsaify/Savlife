package com.hackerkernel.blooddonar.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.hackerkernel.blooddonar.infrastructure.BaseAuthActivity;
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.parser.JsonParser;
import com.hackerkernel.blooddonar.pojo.DonorPojo;
import com.hackerkernel.blooddonar.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DonorDetailActivity extends BaseAuthActivity {
    @Bind(R.id.donor_detail_layout) View mLayoutForSnackbar;
    @Bind(R.id.detail_donor_name) TextView mName;
    @Bind(R.id.detail_donor_age) TextView mAge;
    @Bind(R.id.detail_donor_blood) TextView mBlood;
    @Bind(R.id.detail_donor_gender) TextView mGender;
    @Bind(R.id.detail_donor_image) ImageView mImage;
    @Bind(R.id.detail_last_donated) TextView mLastDonated;
    @Bind(R.id.detail_donor_id) TextView idDonor;

    private RequestQueue mRequestQueue;
    private String mDonorId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_detail);
        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        ButterKnife.bind(this);

        //check we got donor id intenet or not
        if (getIntent().hasExtra(Constants.COM_ID)){
            mDonorId = getIntent().getExtras().getString(Constants.COM_ID);
        }else{
            Toast.makeText(getApplicationContext(),"Unable to open donor page. Try Again Later",Toast.LENGTH_LONG).show();
            finish();
        }
        checkNetworkIsAvailble();

    }
    private void checkNetworkIsAvailble(){
        if (Util.isNetworkAvailable()){
            fetchDetailDonorInBackground();
        }
    }

    private void fetchDetailDonorInBackground() {
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_DONOR_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            parseDetailDonorData(response);
            Log.d("TAG","MUR::"+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG","MUR:"+error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY,Util.generateApiKey(mDonorId));
                params.put(Constants.COM_ID,mDonorId);
                return params;
            }
        };
        mRequestQueue.add(request);

    }
    private void parseDetailDonorData(String response){

        try {
            JSONObject obj = new JSONObject(response);
            boolean returned = obj.getBoolean(Constants.COM_RETURN);
            String message = obj.getString(Constants.COM_MESSAGE);
            if (returned){
                JSONArray data =  obj.getJSONArray(Constants.COM_DATA);
                DonorPojo pojo = JsonParser.DetailDonorParser(data);
                mName.setText(pojo.getFullName());
                mAge.setText("Age: "+pojo.getAge());
                mBlood.setText(pojo.getBloodGroup());
                mGender.setText("Gender: "+pojo.getGender());
                idDonor.setText("Id: "+pojo.getId());
                mLastDonated.setText(pojo.getLastDonated());

            }
            else{
                Util.showRedSnackbar(mLayoutForSnackbar,message);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
