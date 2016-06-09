package com.hackerkernel.blooddonar.appintro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.activity.MainActivity;

/**
 * Created by husain on 6/6/2016.
 */
public class MyAppIntro extends AppIntro {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Blood Donor","Find blood donors easily around the globe", R.drawable.appintro1, Color.parseColor("#242426")));
        addSlide(AppIntroFragment.newInstance("Health Feeds","Get recent health related Post & Image",R.drawable.appintro2,Color.parseColor("#7badc6")));
        addSlide(AppIntroFragment.newInstance("Hot Deals","Get exclusive deal on Pathology labs & Hospitals",R.drawable.appintro3,Color.parseColor("#B35053")));

        showStatusBar(false);

        // Hide Skip/Done button
        showSkipButton(true);
        setDoneText("Login");

        setDepthAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

}
