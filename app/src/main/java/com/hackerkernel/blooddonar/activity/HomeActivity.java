package com.hackerkernel.blooddonar.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.adapter.ViewPagerAdapter;
import com.hackerkernel.blooddonar.appintro.MyAppIntro;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.dialog.DatePicker;
import com.hackerkernel.blooddonar.fragment.BestDonorFragment;
import com.hackerkernel.blooddonar.fragment.DealsFragment;
import com.hackerkernel.blooddonar.fragment.FeedFragment;
import com.hackerkernel.blooddonar.infrastructure.BaseAuthActivity;
import com.hackerkernel.blooddonar.network.GetUserLocation;
import com.hackerkernel.blooddonar.service.GcmRegistrationIntentService;
import com.hackerkernel.blooddonar.util.Util;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends BaseAuthActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.tablayout) TabLayout mTabLayout;
    @Bind(R.id.homeviewpager) ViewPager mViewPager;
    @Bind(R.id.footer_textview) TextView mFooterTextview;

    //side menu
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.home_navigation_view) NavigationView mNaigationView;

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

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

        //set footer text
        mFooterTextview.append("Made with ");
        Spanned redHeart = Html.fromHtml("<font color='red'>&#10084;</font>");
        mFooterTextview.append(redHeart);
        mFooterTextview.append(" By HackerKernel.com");
        mFooterTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://hackerkernel.com/refer.php?id=savlife"));
                startActivity(browserIntent);
            }
        });

        /**
        * GCM
                * */
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.REGISTRATION_COMPLETE)){
                    Log.d(TAG,"HUS: device ready");
                }else if (intent.getAction().equals(Constants.SENT_TOKEN_TO_SERVER)){
                    Log.d(TAG,"HUS: Token send to server");
                }else if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)){
                    Log.d(TAG,"HUS: Push notification received");
                }
            }
        };

        if (checkPlayServices()){
            registerGCM();
            subscribeToGlobalNotification();
        }
    }

    private void registerGCM() {
        Intent intent = new Intent(this, GcmRegistrationIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
    }

    public void subscribeToGlobalNotification(){
        Intent intent = new Intent(this, GcmRegistrationIntentService.class);
        intent.putExtra("key", "subscribe");
        intent.putExtra("topic", "global");
        startService(intent);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
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
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mNaigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_home:
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.menu_profile:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(HomeActivity.this,ProfileInfoActivity.class));
                        break;
                    case R.id.menu_add_last_donation:
                        mDrawerLayout.closeDrawers();
                        DatePicker dialog = new DatePicker();
                        dialog.show(getSupportFragmentManager(),"DateDIA");
                        break;
                    case R.id.menu_like_us_facebook:
                        mDrawerLayout.closeDrawers();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/blooddonorbhopal/?fref=ts"));
                        startActivity(browserIntent);
                        break;
                    case R.id.menu_about:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(HomeActivity.this,AboutUsActivity.class));
                        break;
                    case R.id.menu_join_us:
                        mDrawerLayout.closeDrawers();
                        Util.dialNumber(HomeActivity.this,"09752071654");
                        break;
                }
                return true;
            }
        });
    }

    public void setupviewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BestDonorFragment(), "Best Donor");
        adapter.addFragment(new FeedFragment(), "Health Feed");
        adapter.addFragment(new DealsFragment(), "Deals");
        mViewPager.setAdapter(adapter);
    }



}
