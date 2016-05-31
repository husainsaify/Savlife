package com.hackerkernel.blooddonar.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.constant.EndPoints;
import com.hackerkernel.blooddonar.infrastructure.BaseAuthActivity;
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.parser.JsonParser;
import com.hackerkernel.blooddonar.pojo.ProfileDetailPojo;
import com.hackerkernel.blooddonar.storage.MySharedPreferences;
import com.hackerkernel.blooddonar.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileInfoActivity extends BaseAuthActivity {
    private static final String TAG = ProfileInfoActivity.class.getSimpleName();
    @Bind(R.id.profile_layout_for_snackbar) View mLayoutForSnackbar;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.profile_imageView) ImageView mProfileImage;
    @Bind(R.id.profile_name) TextView mProfileName;
    @Bind(R.id.profile_blood_grup) TextView mProfileBlood;
    @Bind(R.id.profile_Num) TextView mProfileNum;
    @Bind(R.id.profile_last_donated) TextView mProfileLastDonated;
    @Bind(R.id.profile_gender) TextView mProfileGender;
    @Bind(R.id.profile_age) TextView mProfileAge;
    @Bind(R.id.profile_id) TextView mProfileID;
    @Bind(R.id.profile_city) TextView mProfileCity;

    private MySharedPreferences sp;
    private RequestQueue mRequestQue;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);
        ButterKnife.bind(this);

        //init toolbar
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.my_profile);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = MySharedPreferences.getInstance(getApplicationContext());
        mRequestQue = MyVolley.getInstance().getRequestQueue();

        //init pd
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.please_Watit));
        pd.setCancelable(true);

        ifNetworkIsAvailable();
    }

    private void ifNetworkIsAvailable() {
        if (Util.isNetworkAvailable()) {
            parseProfileInfoInBackground();
        } else {
            Util.noInternetSnackBar(this, mLayoutForSnackbar);
        }
    }

    private void parseProfileInfoInBackground() {
        pd.show(); //show pb
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_DONOR_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss(); //hide pb
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean returned = obj.getBoolean(Constants.COM_RETURN);
                    String message = obj.getString(Constants.COM_MESSAGE);
                    if (returned){
                        JSONArray data = obj.getJSONArray(Constants.COM_DATA);
                        ProfileDetailPojo pojo = JsonParser.ParseUserProfile(data);
                        mProfileName.setText(pojo.getProfileNAme());
                        mProfileBlood.setText(pojo.getProfileBlood());
                        mProfileNum.setText(pojo.getProfileNum());
                        mProfileLastDonated.setText(pojo.getProfilelastDonated());
                        mProfileAge.setText(pojo.getProfileAge());
                        mProfileCity.setText(pojo.getProfileCity());
                        mProfileGender.setText(pojo.getProfileGender());
                        mProfileID.setText(pojo.getProfileId());
                        String imageURl = EndPoints.IMAGE_BASE_URL+ pojo.getProfileURL();
                        Glide.with(getApplicationContext())
                                .load(imageURl)
                                .into(mProfileImage);
                    }else {
                        Util.showRedSnackbar(mLayoutForSnackbar,message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG,"HUS: parseProfileInfoInBackground: "+e.getMessage());
                    Util.showParsingErrorAlert(ProfileInfoActivity.this);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                error.printStackTrace();
                Log.d(TAG,"HUS: parseProfileInfoInBackground: "+error.getMessage());
                String errorString = MyVolley.handleVolleyError(error);
                if (errorString != null){
                    Util.showRedSnackbar(mLayoutForSnackbar,errorString);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY, Util.generateApiKey(sp.getUserMobile()));
                params.put(Constants.COM_MOBILE, sp.getUserMobile());
                return params;
            }
        };
        mRequestQue.add(request);
    }
}
