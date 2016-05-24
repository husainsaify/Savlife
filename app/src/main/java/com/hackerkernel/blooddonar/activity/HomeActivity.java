package com.hackerkernel.blooddonar.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.adapter.DonorAdapter;
import com.hackerkernel.blooddonar.adapter.ViewPagerAdapter;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.constant.EndPoints;
import com.hackerkernel.blooddonar.fragment.BestDonorFragment;
import com.hackerkernel.blooddonar.fragment.DealsFragment;
import com.hackerkernel.blooddonar.fragment.ReviewUsFragment;
import com.hackerkernel.blooddonar.infrastructure.BaseAuthActivity;
import com.hackerkernel.blooddonar.network.GetUserLocation;
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.parser.JsonParser;
import com.hackerkernel.blooddonar.pojo.DonorListPojo;
import com.hackerkernel.blooddonar.storage.MySharedPreferences;
import com.hackerkernel.blooddonar.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends BaseAuthActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.tablayout) TabLayout mTabLayout;
    @Bind(R.id.homeviewpager) ViewPager mViewPager;

    //side menu
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.home_navigation_view) NavigationView mNaigationView;
    @Bind(R.id.recycleView)
    RecyclerView recyclerViewe;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private MySharedPreferences sp;
    private RequestQueue mRequestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("SavLife");

        //init SP
        sp = MySharedPreferences.getInstance(this);

        //init request queue
        mRequestQueue = MyVolley.getInstance().getRequestQueue();

        initSideMenu();

        setupviewPager();
        mTabLayout.setupWithViewPager(mViewPager);

        GetUserLocation getUserLocation = new GetUserLocation(getApplicationContext());
        getUserLocation.getLocation();

        /*
        * Method to check internet & call method to get donor in background
        * */
        checkInternetAndGetBestDonor();
    }

    /*
    * Method to check internet and get best donor
    * */
    private void checkInternetAndGetBestDonor() {
        if (Util.isNetworkAvailable()){
            getBestDonorInBackground();
        }else {
            //TODO:: no internet , show snackbar, and show need help contacts
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    /*
    * Method to setup side menu
    * */
    private void initSideMenu() {
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    public void setupviewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BestDonorFragment(), "Best Donor");
        adapter.addFragment(new ReviewUsFragment(), "Review US");
        adapter.addFragment(new DealsFragment(), "Deals");
        mViewPager.setAdapter(adapter);
    }


    public void getBestDonorInBackground(){
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_BEST_DONOR, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseBestDonorResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG,"MUR::getBestDonorInBackground "+error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY, Util.generateApiKey(sp.getUserMobile()));
                params.put(Constants.COM_MOBILE,sp.getUserMobile());
                params.put(Constants.LOC_CITY,sp.getUserLocation());
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseBestDonorResponse(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);
            boolean returned = jsonObj.getBoolean(Constants.COM_RETURN);
            String message = jsonObj.getString(Constants.COM_MESSAGE);
            if (returned){
                int count = jsonObj.getInt("count");
                if (count <= 0){
                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                }else {
                    JSONArray dataArray = jsonObj.getJSONArray("data");

                    List<DonorListPojo> list = JsonParser.DonorParser(dataArray);

                    setupDonorRecyclerView(list);
                }
            }else {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Util.showParsingErrorAlert(getApplicationContext());
        }

    }

    private void setupDonorRecyclerView(List<DonorListPojo> list) {
        DonorAdapter adapter = new DonorAdapter(list);
        recyclerViewe.setAdapter(adapter);
    }
}
