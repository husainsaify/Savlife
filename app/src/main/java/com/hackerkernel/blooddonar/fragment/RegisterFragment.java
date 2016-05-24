package com.hackerkernel.blooddonar.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
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
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.parser.JsonParser;
import com.hackerkernel.blooddonar.pojo.SimplePojo;
import com.hackerkernel.blooddonar.util.Util;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Fragment to register a new user
 */
public class RegisterFragment extends Fragment {
    private static final String TAG = RegisterFragment.class.getSimpleName();
    @Bind(R.id.reg_layout_for_snackbar) ScrollView mLayoutForSnackbar;
    @Bind(R.id.reg_fullname) EditText mFullname;
    @Bind(R.id.reg_mobile) EditText mMobile;
    @Bind(R.id.reg_gender_group) RadioGroup mGenderGroup;
    @Bind(R.id.reg_age) EditText mAge;
    @Bind(R.id.reg_blood_group) Spinner mBloodGroup;
    @Bind(R.id.reg_button) Button mRegButton;

    private String mUserFullname;
    private String mUserMobile;
    private String mUserGender;
    private String mUserAge;
    private String mUserBloodGroup;
    private String mUserLocation;

    private RequestQueue mRequestQueue;
    private ProgressDialog pd;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init volley
        mRequestQueue = MyVolley.getInstance().getRequestQueue();

        //init pd
        pd = new ProgressDialog(getActivity());
        pd.setMessage(getString(R.string.please_Watit));
        pd.setCancelable(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this,view);

        /*
        * When Register button is clicked
        * */
        mRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternetAndRegister();
            }
        });
        return view;
    }

    /*
    * Method to Check internet , validate and Perform Register
    * */
    private void checkInternetAndRegister() {
        if (Util.isNetworkAvailable()){
            mUserFullname = mFullname.getText().toString().trim();
            mUserMobile = mMobile.getText().toString().trim();
            int genderId = mGenderGroup.getCheckedRadioButtonId();
            mUserAge = mAge.getText().toString().trim();
            mUserBloodGroup = (String) mBloodGroup.getSelectedItem();
            mUserLocation = Util.getCityName(getActivity());
            if (genderId == R.id.reg_gender_male){
                mUserGender = "Male";
            }else {
                mUserGender = "Female";
            }

            if (mUserFullname.isEmpty() || mUserMobile.isEmpty() || mUserAge.isEmpty() || mUserBloodGroup.isEmpty() || mUserGender.isEmpty()){
                Util.showRedSnackbar(mLayoutForSnackbar,"Fill in all the fields");
                return;
            }

            if (mUserFullname.length() <= 3){
                Util.showRedSnackbar(mLayoutForSnackbar,"Fullname should be more then 3 character");
                return;
            }

            if (mUserMobile.length() != 10){
                Util.showRedSnackbar(mLayoutForSnackbar,"Invalid mobile number");
                return;
            }

            if (Integer.parseInt(mUserAge) < 18){
                Util.showRedSnackbar(mLayoutForSnackbar,"You must be 18 to register for "+getString(R.string.app_name));
                return;
            }
            if (mUserLocation == null){
                Util.showRedSnackbar(mLayoutForSnackbar,"Invalid user location. Allow us to collect location");
                return;
            }

            doRegisterInBackground();

        }else {
            Toast.makeText(getActivity(), R.string.no_internet_available,Toast.LENGTH_LONG).show();
            //go to no internet activity
            Util.goToNoInternetActivity(getActivity());
        }
    }

    /*
    * Method to do register in background
    * */
    private void doRegisterInBackground() {
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                parseRegisterResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Log.e(TAG,"HUS: doRegisterInBackground: "+error.getMessage());
                error.printStackTrace();
                //handle Volley error
                String errorString = MyVolley.handleVolleyError(error);
                if (errorString != null){
                    Util.showRedSnackbar(mLayoutForSnackbar,errorString);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY,Util.generateApiKey(mUserMobile));
                params.put(Constants.COM_FULLNAME,mUserFullname);
                params.put(Constants.COM_MOBILE,mUserMobile);
                params.put(Constants.COM_AGE,mUserAge);
                params.put(Constants.COM_BLOOD,mUserBloodGroup);
                params.put(Constants.COM_GENDER,mUserGender);
                params.put(Constants.COM_LOCATION,mUserLocation);
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    /*
    * Method to parse register response
    * */
    private void parseRegisterResponse(String response) {
        try {
            SimplePojo current = JsonParser.SimpleParser(response);
            if (current.isReturned()){
                //go to OTP verification activity
                Util.goToOtpVerificationActivity(getActivity(),mUserMobile);
            }else {
                Util.showRedSnackbar(mLayoutForSnackbar,current.getMessage());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"HUS: parseRegisterResponse: "+e.getMessage());
            Util.showParsingErrorAlert(getActivity());
        }
    }

}
