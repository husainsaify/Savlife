package com.hackerkernel.blooddonar.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hackerkernel.blooddonar.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OtpVerificationActivity extends AppCompatActivity {
    private static final String TAG = OtpVerificationActivity.class.getSimpleName();
    @Bind(R.id.otp_progressbar) ProgressBar mOtpProgressbar;
    @Bind(R.id.resend_otp_heading) TextView mResendOtpHeading;
    @Bind(R.id.resend_otp_btn) TextView mResendOtpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        ButterKnife.bind(this);

        new CountDownTimer(120000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                int percent = (int) (millisUntilFinished / 1200);
                mOtpProgressbar.setProgress(percent);
            }

            @Override
            public void onFinish() {
                mOtpProgressbar.setVisibility(View.GONE);

                //show resend otp
                mResendOtpBtn.setVisibility(View.VISIBLE);
                mResendOtpHeading.setVisibility(View.VISIBLE);
            }
        }.start();
    }
}
