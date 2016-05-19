package com.hackerkernel.blooddonar.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.util.Util;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NoInternetActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.contact_us_number1) TextView mNumber1;
    @Bind(R.id.contact_us_number2) TextView mNumber2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        ButterKnife.bind(this);
        mNumber1.setOnClickListener(this);
        mNumber2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.contact_us_number1:
                Util.dialNumber(NoInternetActivity.this,"9752071654");
                break;
            case R.id.contact_us_number2:
                Util.dialNumber(NoInternetActivity.this,"8871334161");
                break;
        }
    }
}
