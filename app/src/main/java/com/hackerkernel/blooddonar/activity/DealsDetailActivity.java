package com.hackerkernel.blooddonar.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.hackerkernel.blooddonar.pojo.SimplePojo;
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
    private ProgressDialog pd;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.detail_hospital_name) TextView mHospitalName;
    @Bind(R.id.detail_hospital_image) ImageView mHospitalImage;
    @Bind(R.id.detail_off) TextView mPercentOff;
    @Bind(R.id.detail_description) TextView mDescription;
    @Bind(R.id.detail_original_price) TextView mOriginalPrice;
    @Bind(R.id.detail_special_price) TextView mOfferPrice;
    @Bind(R.id.layout_for_snackbar) View mLayoutForSnackbar;
    @Bind(R.id.progressBar) ProgressBar mProgressbar;
    @Bind(R.id.detail_scroll) ScrollView mDetailScrollView;
    @Bind(R.id.detail_deal_code) TextView mCodeArea;
    @Bind(R.id.detail_book_detail) Button mBookDeal;

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

        //init pd
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.please_Watit));
        pd.setCancelable(false);

        checkInternetAndFetchDeal();

        mBookDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternetAndBookDeal();
            }
        });

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
                //show and hide deal book button on the basic of data
                boolean bookedStatus = obj.getBoolean(Constants.DEAL_BOOKED_STATUS);
                if (bookedStatus){
                    //show code (deal is booked)
                    mBookDeal.setVisibility(View.GONE);
                    mCodeArea.setVisibility(View.VISIBLE);
                }else {
                    //hide code & show button deal is not booked
                    mBookDeal.setVisibility(View.VISIBLE);
                    mCodeArea.setVisibility(View.GONE);
                }
                //parse response and setup views
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
        //set name to the Toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(pojo.getLabName());
        }

        mHospitalName.setText(pojo.getLabName());
        mPercentOff.setText(pojo.getOff());
        mPercentOff.append("% off");
        mDescription.setText(pojo.getDescription().replace("\\n","\n"));
        mOriginalPrice.setText(pojo.getOrignal_prize());
        mOriginalPrice.append("Rs");
        mOfferPrice.setText(pojo.getSpecial_prize());
        mOfferPrice.append("Rs");
        mCodeArea.setText("code:: "+pojo.getCode());

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

    /*
    * DEALS BOOKING
    * */
    private void checkInternetAndBookDeal() {
        if (Util.isNetworkAvailable()){
            bookDealInBackground();
        }else {
            Util.noInternetSnackBar(this,mLayoutForSnackbar);
        }
    }

    /*
    * Method to make a request to the api to book deal
    * */
    private void bookDealInBackground() {
        pd.show(); //show pd
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.BOOK_DEAL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss(); //hide pd
                parseBookDealsResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss(); //hide pd
                error.printStackTrace();
                Log.d(TAG,"HUS: bookDealInBackground: "+error.getMessage());
                String errorString = MyVolley.handleVolleyError(error);
                if (errorString != null){
                    Util.showRedSnackbar(mLayoutForSnackbar,errorString);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY,Util.generateApiKey(sp.getUserMobile()));
                params.put(Constants.COM_MOBILE,sp.getUserMobile());
                params.put(Constants.COM_ID,mDealsId);
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    /*
    * Method to parse book deal response
    * */
    private void parseBookDealsResponse(String response) {
        try {
            SimplePojo pojo = JsonParser.SimpleParser(response);
            if (pojo.isReturned()){
                //success
                Util.showSimpleDialog(this,"HURRAY!!",pojo.getMessage());
                //refresh deal detail
                checkInternetAndFetchDeal();
            }else {
                //failure
                Util.showSimpleDialog(this,"OOPS!!",pojo.getMessage());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"HUS: parseBookDealsResponse: "+e.getMessage());
            Util.showParsingErrorAlert(this);
        }
    }
}
