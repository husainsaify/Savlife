package com.hackerkernel.blooddonar.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.util.Util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NoInternetActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.contact_us_number1) TextView mNumber1;
    @Bind(R.id.contact_us_number2) TextView mNumber2;
    @Bind(R.id.progressBar) ProgressBar mProgressbar;
    @Bind(R.id.button_tryAgain) Button mTryAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        ButterKnife.bind(this);
        mNumber1.setOnClickListener(this);
        mNumber2.setOnClickListener(this);
        mTryAgain.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_tryAgain:
                CheckInternetConnection connection = new CheckInternetConnection();
                connection.execute();
                break;
            case R.id.contact_us_number1:
                Util.dialNumber(NoInternetActivity.this,"9752071654");
                break;
            case R.id.contact_us_number2:
                Util.dialNumber(NoInternetActivity.this,"8871334161");
                break;
        }
    }

    public class CheckInternetConnection extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show progressbar
            mProgressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (Util.isNetworkAvailable()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setRequestProperty("User-Agent", "Test");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(1500);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mProgressbar.setVisibility(View.GONE);
            if (result){
                //active internet
                Util.goToHomeActivity(NoInternetActivity.this);
            }else {
                //Inactive internet
                Toast.makeText(getApplicationContext(),"Inactive or weak Internet connection",Toast.LENGTH_LONG).show();
            }
        }
    }
}
