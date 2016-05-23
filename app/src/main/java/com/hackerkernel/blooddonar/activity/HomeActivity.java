package com.hackerkernel.blooddonar.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.adapter.ViewPagerAdapter;
import com.hackerkernel.blooddonar.fragment.BestDonorFragment;
import com.hackerkernel.blooddonar.fragment.DealsFragment;
import com.hackerkernel.blooddonar.fragment.ReviewUsFragment;
import com.hackerkernel.blooddonar.infrastructure.BaseAuthActivity;
import com.hackerkernel.blooddonar.storage.MySharedPreferences;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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
    private MySharedPreferences sp;

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

        initSideMenu();

        setupviewPager();
        mTabLayout.setupWithViewPager(mViewPager);

        getCityName();


    }

    /*
    * Method to get city name and store it in sharedPrefernce
    * */
    private void getCityName() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            Criteria criteria = new Criteria();

            locationManager.getBestProvider(criteria, true);

            //nce  you  know  the  name  of  the  LocationProvider,  you  can  call getLastKnownPosition() to  find  out  where  you  were  recently.
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

            if (location == null){
                Toast.makeText(getApplicationContext(),"Location Permission denied by user",Toast.LENGTH_LONG).show();
                return;
            }
            List<Address> addresses;

            try {
                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    String cityname = addresses.get(0).getLocality();
                    //insert city to SharedPrefernece
                    sp.setUserLocation(cityname);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(getApplicationContext(),"Location Permission denied by user",Toast.LENGTH_LONG).show();
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
}
