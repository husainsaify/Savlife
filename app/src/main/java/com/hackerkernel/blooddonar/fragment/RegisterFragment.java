package com.hackerkernel.blooddonar.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import com.hackerkernel.blooddonar.util.Util;

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
            String fullname = mFullname.getText().toString().trim();
            String mobile = mMobile.getText().toString().trim();
            int genderId = mGenderGroup.getCheckedRadioButtonId();
            String age = mAge.getText().toString().trim();
            String bloodGroup = (String) mBloodGroup.getSelectedItem();
            String gender;
            if (genderId == R.id.reg_gender_male){
                gender = "Male";
            }else {
                gender = "Female";
            }

            if (fullname.isEmpty() || mobile.isEmpty() || age.isEmpty() || bloodGroup.isEmpty() || gender.isEmpty()){
                Util.showRedSnackbar(mLayoutForSnackbar,"Fill in all the fields");
                return;
            }

            if (fullname.length() <= 3){
                Util.showRedSnackbar(mLayoutForSnackbar,"Fullname should be more then 3 character");
                return;
            }

            if (mobile.length() != 10){
                Util.showRedSnackbar(mLayoutForSnackbar,"Invalid mobile number");
                return;
            }

            if (Integer.parseInt(age) < 18){
                Util.showRedSnackbar(mLayoutForSnackbar,"You must be 18 to register for "+getString(R.string.app_name));
                return;
            }

            doRegisterInBackground(fullname,mobile,age,bloodGroup,gender);

        }else {
            Toast.makeText(getActivity(), R.string.no_internet_available,Toast.LENGTH_LONG).show();
        }
    }

    /*
    * Method to do register in background
    * */
    private void doRegisterInBackground(final String fullname, final String mobile, final String age, final String bloodGroup, final String gender) {
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();
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
                params.put(Constants.COM_APIKEY,Constants.APIKEY);
                params.put(Constants.COM_FULLNAME,fullname);
                params.put(Constants.COM_MOBILE,mobile);
                params.put(Constants.COM_AGE,age);
                params.put(Constants.COM_BLOOD,bloodGroup);
                params.put(Constants.COM_GENDER,gender);
                return params;
            }
        };

        mRequestQueue.add(request);
    }

}
