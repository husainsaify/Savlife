package com.hackerkernel.blooddonar.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hackerkernel.blooddonar.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OtpVerificationActivity extends AppCompatActivity {
    private static final String TAG = OtpVerificationActivity.class.getSimpleName();
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.otp_progressbar) ProgressBar mOtpProgressbar;
    @Bind(R.id.resend_otp_heading) TextView mResendOtpHeading;
    @Bind(R.id.resend_otp_btn) Button mResendOtpBtn;
    @Bind(R.id.otp_progress_percentage) TextView mOtpProgressPercentage;
    @Bind(R.id.verify_EditText) EditText mOtpEditext;
    @Bind(R.id.verify_Button) Button mOtpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        ButterKnife.bind(this);

        //init toolbar
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.verify_otp);

        initOtpWaitingProgressbar();
    }

    /*
    * Method to show progressbar for 2 Minutes
    * */
    private void initOtpWaitingProgressbar() {
        new CountDownTimer(120000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                int percent = (int) (millisUntilFinished / 1200);
                mOtpProgressbar.setProgress(percent);
                mOtpProgressPercentage.setText(percent+"% left");
            }

            @Override
            public void onFinish() {
                mOtpProgressbar.setVisibility(View.GONE);
                mOtpProgressPercentage.setVisibility(View.GONE);

                //show resend otp
                mResendOtpBtn.setVisibility(View.VISIBLE);
                mResendOtpHeading.setVisibility(View.VISIBLE);
            }
        }.start();
    }
}
