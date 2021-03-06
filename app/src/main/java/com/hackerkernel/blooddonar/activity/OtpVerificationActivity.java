package com.hackerkernel.blooddonar.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.constant.EndPoints;
import com.hackerkernel.blooddonar.infrastructure.BaseActivity;
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.parser.JsonParser;
import com.hackerkernel.blooddonar.pojo.SimplePojo;
import com.hackerkernel.blooddonar.storage.MySharedPreferences;
import com.hackerkernel.blooddonar.util.Util;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OtpVerificationActivity extends BaseActivity {
    private static final String TAG = OtpVerificationActivity.class.getSimpleName();
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.otp_progressbar) ProgressBar mOtpProgressbar;
    @Bind(R.id.resend_otp_heading) TextView mResendOtpHeading;
    @Bind(R.id.resend_otp_btn) Button mResendOtpBtn;
    @Bind(R.id.otp_progress_percentage) TextView mOtpProgressPercentage;
    @Bind(R.id.verify_EditText) EditText mOtpEditext;
    @Bind(R.id.verify_Button) Button mOtpButton;
    @Bind(R.id.nuberTextview) TextView mNumberTextview;

    private ProgressDialog pd;
    private RequestQueue mRequestQueue;
    private String mMobile;
    private MySharedPreferences sp;
    private static OtpVerificationActivity mOtpVerificationActivityInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        ButterKnife.bind(this);

        //init mToolbar
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.verify_otp);

        mOtpVerificationActivityInstance = this;

        //init pd
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.please_Watit));
        pd.setCancelable(true);

        //init request queue
        mRequestQueue = MyVolley.getInstance().getRequestQueue();

        //init SP
        sp = MySharedPreferences.getInstance(getApplicationContext());

        //change color of progressbar
        mOtpProgressbar.getProgressDrawable().setColorFilter(
                ContextCompat.getColor(this,R.color.primary), android.graphics.PorterDuff.Mode.SRC_IN);

        //check user has mobile intent
        if (getIntent().hasExtra(Constants.COM_MOBILE)){
            mMobile = getIntent().getExtras().getString(Constants.COM_MOBILE);
            mNumberTextview.append(" "+mMobile);
        }else {
            Toast.makeText(getApplicationContext(),"Unable to open OTP Verification. Try again",Toast.LENGTH_LONG).show();
            finish();
        }

        //When verify button is clicked
        mOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternetVerifyOtp();
            }
        });

        //when resend otp button is pressed
        mResendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternetAndResendOtp();
            }
        });

        initOtpWaitingProgressbar();
    }

    /*
    * Method to check internet and resend OTP
    * */
    private void checkInternetAndResendOtp() {
        if (Util.isNetworkAvailable()){
            resendOtpInBackground();
        }else {
            Util.showSimpleDialog(this,"OOPS!!","No Internet Connection");
        }
    }

    /*
    * Method to resend otp in background
    * */
    private void resendOtpInBackground() {
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                parseResendOtpResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Log.e(TAG,"HUS: doLoginInBackground: "+error.getMessage());
                error.printStackTrace();
                //handle Volley error
                String errorString = MyVolley.handleVolleyError(error);
                if (errorString != null){
                    //show alert dialog
                    Util.showSimpleDialog(OtpVerificationActivity.this,"OOPS!!",errorString);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY,Util.generateApiKey(mMobile));
                params.put(Constants.COM_MOBILE,mMobile);
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    /*
    * Method to parse resend OTP response
    * */
    private void parseResendOtpResponse(String response) {
        try {
            SimplePojo current = JsonParser.SimpleParser(response);
            if (current.isReturned()){
                /*
                * When otp is send successfully hide Resend OTP button and show progressbar again
                *
                * */
                //hide resend button
                mResendOtpBtn.setVisibility(View.GONE);
                mResendOtpHeading.setVisibility(View.GONE);
                //show progressbar
                mOtpProgressbar.setVisibility(View.VISIBLE);
                mOtpProgressPercentage.setVisibility(View.VISIBLE);
                initOtpWaitingProgressbar();

            }else {
                Util.showSimpleDialog(OtpVerificationActivity.this,"OOPS!!",current.getMessage());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"HUS: parseRegisterResponse: "+e.getMessage());
            Util.showParsingErrorAlert(OtpVerificationActivity.this);
        }
    }

    /*
    * Method to make a Instace of OtpVerification Activity
    * For IncommingOtp Broadcast Reciever so what we can set OTP
    * to the editText
    * */
    public static OtpVerificationActivity  getInstace(){
        return mOtpVerificationActivityInstance;
    }

    public void updateUI(final String otp) {
        OtpVerificationActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                mOtpEditext.setText(otp);
                checkInternetVerifyOtp();
            }
        });
    }

    /*
    * Method to validate OTP code & request api to validate OTP
    * */
    private void checkInternetVerifyOtp() {
        if (Util.isNetworkAvailable()){
            String otp = mOtpEditext.getText().toString().trim();

            if (otp.length() != 4){
                Toast.makeText(getApplicationContext(),"Invalid OTP code",Toast.LENGTH_LONG).show();
                return;
            }

            verifyOtpInBackground(otp);
        }else {
            Toast.makeText(getApplicationContext(),R.string.no_internet_available,Toast.LENGTH_LONG).show();
        }
    }

    /*
    * Method to verify OTP code in background
    * */
    private void verifyOtpInBackground(final String otp) {
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.VERIFY_OTP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                parseVerifyOtpResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Log.e(TAG,"HUS: verifyOtpInBackground: "+error.getMessage());
                error.printStackTrace();
                //handle Volley error
                String errorString = MyVolley.handleVolleyError(error);
                if (errorString != null){
                    Toast.makeText(getApplicationContext(),errorString,Toast.LENGTH_LONG).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put(Constants.COM_APIKEY,Util.generateApiKey(mMobile));
                param.put(Constants.COM_MOBILE,mMobile);
                param.put(Constants.COM_OTP,otp);
                return param;
            }
        };
        mRequestQueue.add(request);
    }

    /*
    * Method to parse Verify OTP response
    * */
    private void parseVerifyOtpResponse(String response) {
        try {
            SimplePojo pojo = JsonParser.SimpleParser(response);
            /*
            * If result is success
            * Fetch users details & store them in SP
            * */
            if (pojo.isReturned()){
                //store user details in sp
                JsonParser.VerifyOtpParser(response,sp);

                //send user to HomeActivity
                Util.goToHomeActivity(this);
            }else { // show error message
                Toast.makeText(getApplicationContext(),pojo.getMessage(),Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"HUS: parseRegisterResponse: "+e.getMessage());
            Util.showParsingErrorAlert(this);
        }
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
