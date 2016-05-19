package com.hackerkernel.blooddonar.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.adapter.ViewPagerAdapter;
import com.hackerkernel.blooddonar.fragment.LoginFragment;
import com.hackerkernel.blooddonar.fragment.RegisterFragment;
import com.hackerkernel.blooddonar.infrastructure.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    @Bind(R.id.mainViewpager) ViewPager mViewPager;
    @Bind(R.id.mainTabs) TabLayout mTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupViewpager();
        mTabs.setupWithViewPager(mViewPager);
    }

    private void setupViewpager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LoginFragment(),"LOGIN");
        adapter.addFragment(new RegisterFragment(),"REGISTER");
        mViewPager.setAdapter(adapter);
    }
}
