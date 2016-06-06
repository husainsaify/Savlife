package com.hackerkernel.blooddonar.infrastructure;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.hackerkernel.blooddonar.appintro.MyAppIntro;
import com.hackerkernel.blooddonar.storage.MySharedPreferences;
import com.hackerkernel.blooddonar.util.Util;

/**
 * Class to check if user logged in send him to home activity
 * no need to display login or MainActivity
 */
public abstract class BaseActivity extends AppCompatActivity {
    private MySharedPreferences sp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init sp
        sp = MySharedPreferences.getInstance(this);

        //check app intro
        if (!sp.getBooleanKey(MySharedPreferences.BOL_KEY_APP_INTRO)){
            //show app intro
            startActivity(new Intent(this, MyAppIntro.class));
            //save in sp that app intro was shown
            sp.setBooleanKey(MySharedPreferences.BOL_KEY_APP_INTRO);
        }

        if (sp.getLoginStatus()){
            Util.goToHomeActivity(this);
        }
    }
}
