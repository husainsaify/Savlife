package com.hackerkernel.blooddonar.activity;

import android.content.Intent;
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
import android.view.View;
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

    private ActionBarDrawerToggle mActionBarDrawerToggle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("SavLife");



        initSideMenu();

        setupviewPager();
        mTabLayout.setupWithViewPager(mViewPager);

        GetUserLocation getUserLocation = new GetUserLocation(getApplicationContext());
        getUserLocation.getLocation();


    }

    public void openSearchActivity(View view){
        startActivity(new Intent(this, SearchActivity.class));
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



}
