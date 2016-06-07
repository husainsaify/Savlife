package com.hackerkernel.blooddonar.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.adapter.ViewPagerAdapter;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.constant.EndPoints;
import com.hackerkernel.blooddonar.fragment.UserDetailDescriptionFragment;
import com.hackerkernel.blooddonar.fragment.UserDonationHistoryFragment;
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
    private static final String TAG = DonorDetailActivity.class.getSimpleName();
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.tablayout) TabLayout tabLayout;
    @Bind(R.id.homeviewpager) ViewPager viewPager;
    @Bind(R.id.donor_image) ImageView mDonorImageView;
    @Bind(R.id.donor_name) TextView mDonorName;
    @Bind(R.id.layout_for_snackbar) View mLayoutForSnackbar;

    private String mDonorId;
    private ProgressDialog pd;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_detail);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Donor Details");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init progressbar
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.please_Watit));
        pd.setCancelable(false);

        //init request queue
        mRequestQueue = MyVolley.getInstance().getRequestQueue();

        //check we got donor id internet or not
        if (getIntent().hasExtra(Constants.COM_ID)){
            mDonorId = getIntent().getExtras().getString(Constants.COM_ID);
        }else{
            Toast.makeText(getApplicationContext(),"Unable to open donor page. Try Again Later",Toast.LENGTH_LONG).show();
            finish();
        }

        checkNetworkIsAvailable();
    }

    private void checkNetworkIsAvailable(){
        if (Util.isNetworkAvailable()){
            fetchDetailDonorInBackground();
        }else {
            Util.noInternetSnackBar(this,mLayoutForSnackbar);
        }
    }

    private void fetchDetailDonorInBackground() {
        pd.show();//show pb
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_DONOR_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss(); //hide progressbar
                parseDetailDonorData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Log.d(TAG,"HUS:"+error.getMessage());
                error.printStackTrace();
                String errorString = MyVolley.handleVolleyError(error);
                if (errorString != null){
                    Util.showRedSnackbar(mLayoutForSnackbar,errorString);
                }
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
                setupView(pojo);
            }
            else{
                Util.showRedSnackbar(mLayoutForSnackbar,message);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    * Method to setup view
    * */
    private void setupView(DonorPojo pojo) {
        String fullname = Util.makeFirstLetterUpercase(pojo.getFullName());
        mDonorName.setText(fullname);

        //set title with the name of donor
        if (getSupportActionBar() != null){

            getSupportActionBar().setTitle(fullname);
        }

        //download image
        if (pojo.getImageUrl() != null){
            String userImage = EndPoints.IMAGE_BASE_URL + pojo.getImageUrl();
            Glide.with(this)
                    .load(userImage)
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mDonorImageView);
        }

        Bundle bundle = new Bundle();
        bundle.putString(Constants.COM_FULLNAME,fullname);
        bundle.putString(Constants.COM_AGE,pojo.getAge());
        bundle.putString(Constants.COM_BLOOD,pojo.getBloodGroup());
        bundle.putString(Constants.COM_GENDER,pojo.getGender());
        bundle.putString(Constants.COM_ID,pojo.getId());

        //setup tabs
        UserDetailDescriptionFragment userDetailDescriptionFragment = new UserDetailDescriptionFragment();
        userDetailDescriptionFragment.setArguments(bundle);

        //user donation history
        Bundle bundle1 = new Bundle();
        bundle1.putString(Constants.COM_MOBILE,pojo.getMobile());

        UserDonationHistoryFragment userDonationHistoryFragment = new UserDonationHistoryFragment();
        userDonationHistoryFragment.setArguments(bundle1);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(userDetailDescriptionFragment,"Description");
        adapter.addFragment(userDonationHistoryFragment,"Donation History");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
