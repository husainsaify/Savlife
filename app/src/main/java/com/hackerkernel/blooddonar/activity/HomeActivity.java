package com.hackerkernel.blooddonar.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.adapter.ViewPagerAdapter;
import com.hackerkernel.blooddonar.appintro.MyAppIntro;
import com.hackerkernel.blooddonar.dialog.DatePicker;
import com.hackerkernel.blooddonar.fragment.BestDonorFragment;
import com.hackerkernel.blooddonar.fragment.DealsFragment;
import com.hackerkernel.blooddonar.fragment.FeedFragment;
import com.hackerkernel.blooddonar.infrastructure.BaseAuthActivity;
import com.hackerkernel.blooddonar.network.GetUserLocation;
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
