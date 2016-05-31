package com.hackerkernel.blooddonar.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.constant.EndPoints;
import com.hackerkernel.blooddonar.infrastructure.BaseAuthActivity;
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.parser.JsonParser;
import com.hackerkernel.blooddonar.pojo.DealsPojo;
import com.hackerkernel.blooddonar.storage.MySharedPreferences;
import com.hackerkernel.blooddonar.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DealsDetailActivity extends BaseAuthActivity {
    private static final String TAG = DealsDetailActivity.class.getSimpleName();
    private String mDealsId;
    private MySharedPreferences sp;
    private RequestQueue mRequestQueue;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.detail_hospital_name) TextView mHospitalName;
    @Bind(R.id.detail_hospital_image) ImageView mHospitalImage;
    @Bind(R.id.detail_off) TextView mPercentOff;
    @Bind(R.id.detail_description) TextView mDescription;
    @Bind(R.id.detail_timing) TextView timings;
    @Bind(R.id.detail_original_price) TextView mOriginalPrice;
    @Bind(R.id.detail_special_price) TextView mOfferPrice;
    @Bind(R.id.layout_for_snackbar) View mLayoutForSnackbar;
    @Bind(R.id.progressBar) ProgressBar mProgressbar;
    @Bind(R.id.detail_scroll) ScrollView mDetailScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals_detail);
        ButterKnife.bind(this);

        if (getIntent().hasExtra(Constants.COM_ID)) {
            mDealsId = getIntent().getStringExtra(Constants.COM_ID);
        }else{
            Toast.makeText(getApplicationContext(),"Unable to open deal. Try Again Later",Toast.LENGTH_LONG).show();
            finish();
        }

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Deal Detail");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(this);

        checkInternetAndFetchDeal();
    }

    private void checkInternetAndFetchDeal() {
        if (Util.isNetworkAvailable()) {
            fetchDealsInBackground();
        }else {
            Util.noInternetSnackBar(this,mLayoutForSnackbar);
        }
    }

    private void fetchDealsInBackground() {
        showProgressAndHideLayout(true);
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_DEALS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showProgressAndHideLayout(false);
                parseDealsData(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgressAndHideLayout(false);
                error.printStackTrace();
                Log.d(TAG,"HUS: fetchDealsInBackground: "+error.getMessage());
                String errorString = MyVolley.handleVolleyError(error);
                if (errorString != null){
                    Util.showRedSnackbar(mLayoutForSnackbar,errorString);
                }
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
            boolean returned = obj.getBoolean(Constants.COM_RETURN);
            String message = obj.getString(Constants.COM_MESSAGE);
            if (returned){
                JSONArray data = obj.getJSONArray(Constants.COM_DATA);
                DealsPojo pojo = JsonParser.DetailDealsParser(data);
                setupView(pojo);
            }else{
                Util.showRedSnackbar(mLayoutForSnackbar,message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Util.showParsingErrorAlert(DealsDetailActivity.this);
        }
    }

    private void setupView(DealsPojo pojo) {
        mHospitalName.setText(pojo.getLabName());
        mPercentOff.setText(pojo.getOff());
        mPercentOff.append("% off");
        mDescription.setText(pojo.getDescription());
        mOriginalPrice.setText(pojo.getOrignal_prize());
        mOriginalPrice.append("Rs");
        mOfferPrice.setText(pojo.getSpecial_prize());
        mOfferPrice.append("Rs");

        //load hospital image
        if (!pojo.getImageUrl().isEmpty()){
            String image = EndPoints.IMAGE_BASE_URL + pojo.getImageUrl();
            Glide.with(this)
                    .load(image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mHospitalImage);
        }

    }

    private void showProgressAndHideLayout(boolean state) {
        if (state){
            //show progressbar and hide layout
            mProgressbar.setVisibility(View.VISIBLE);
            mDetailScrollView.setVisibility(View.GONE);
        }else {
            //hide progressbar and show layout
            mProgressbar.setVisibility(View.GONE);
            mDetailScrollView.setVisibility(View.VISIBLE);
        }
    }


}
