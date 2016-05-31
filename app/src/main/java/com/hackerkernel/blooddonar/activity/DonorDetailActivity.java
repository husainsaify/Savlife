package com.hackerkernel.blooddonar.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.adapter.ViewPagerAdapter;
import com.hackerkernel.blooddonar.fragment.DetailDescriptionFragment;
import com.hackerkernel.blooddonar.fragment.LastDonatedFragment;
import com.hackerkernel.blooddonar.infrastructure.BaseAuthActivity;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DonorDetailActivity extends BaseAuthActivity {
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.tablayout) TabLayout tabLayout;
    @Bind(R.id.homeviewpager) ViewPager viewPager;


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

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }
    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DetailDescriptionFragment(),"Description");
        adapter.addFragment(new LastDonatedFragment(),"Donation History");
        viewPager.setAdapter(adapter);
    }

}
