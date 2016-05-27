package com.hackerkernel.blooddonar.activity;

import android.app.Activity;
import android.os.Bundle;

import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.constant.Constants;

public class DetailDealsActivity extends Activity {
    private String dealsID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);
        if (getIntent().hasExtra(Constants.COM_ID)){
          dealsID = getIntent().getStringExtra(Constants.COM_ID);


        }
        else {

        }
    }
}
