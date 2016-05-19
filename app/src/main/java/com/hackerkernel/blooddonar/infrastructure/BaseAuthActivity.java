package com.hackerkernel.blooddonar.infrastructure;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.hackerkernel.blooddonar.activity.MainActivity;
import com.hackerkernel.blooddonar.storage.MySharedPreferences;
import com.hackerkernel.blooddonar.util.Util;

/**
 * Class to check login status
 * if user is not logged in send him to login activity
 */
public abstract class BaseAuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check internet connection if not avalable send user to NO internet activity
        if (!Util.isNetworkAvailable()){
            Util.goToNoInternetActivity(this);
        }

        //if user not login send him to Main activity
        if (!MySharedPreferences.getInstance(this).getLoginStatus()){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
