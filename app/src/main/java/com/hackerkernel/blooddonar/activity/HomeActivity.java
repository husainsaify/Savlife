package com.hackerkernel.blooddonar.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.adapter.ViewPagerAdapter;
import com.hackerkernel.blooddonar.fragment.BestDonours;
import com.hackerkernel.blooddonar.fragment.Deals;
import com.hackerkernel.blooddonar.fragment.ReviewUs;

import butterknife.Bind;

public class HomeActivity extends AppCompatActivity {
        @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tablayout)
    TabLayout tabLayout;
    @Bind(R.id.homeviewpager)
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
    public void setupviewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BestDonours(),"Best Donour");
        adapter.addFragment(new ReviewUs(),"Review US");
        adapter.addFragment(new Deals(),"Deals");
        viewPager.setAdapter(adapter);
    }
}
