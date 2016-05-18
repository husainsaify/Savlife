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
import android.widget.ScrollView;
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
 * Fragment to perform login
 */
public class LoginFragment extends Fragment {
    private static final String TAG = LoginFragment.class.getSimpleName();
    @Bind(R.id.login_layout_for_snackbar) ScrollView mLayoutForSnackbar;
    @Bind(R.id.login_mobile) EditText mLoginMobile;
    @Bind(R.id.login_btn) Button mLoginBtn;

    private RequestQueue mRequestQueue;
    private ProgressDialog pd;

    private String mUserMobile;

    public LoginFragment() {
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this,view);

        //when login button is clicked
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserMobile = mLoginMobile.getText().toString().trim();
                checkInternetAndDoLogin();
            }
        });

        return view;
    }

    /*
    * Method to check internet and perform check & do Login
    * */
    private void checkInternetAndDoLogin() {
        if (Util.isNetworkAvailable()){

            //validate mobile number
            if (mUserMobile.length() != 10){
                Util.showRedSnackbar(mLayoutForSnackbar,"Invalid mobile number");
                return;
            }

            doLoginInBackground();

        }else {
            Toast.makeText(getActivity(), R.string.no_internet_available,Toast.LENGTH_LONG).show();
            //go to no internet activity
            Util.goToNoInternetActivity(getActivity());
        }
    }

    /*
    * Method to perform login in background
    * */
    private void doLoginInBackground() {
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                parseLoginResponse(response);
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
                    Util.showRedSnackbar(mLayoutForSnackbar,errorString);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY,Constants.APIKEY);
                params.put(Constants.COM_MOBILE,mUserMobile);
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    /*
    * Method to parse login response
    * */
    private void parseLoginResponse(String response) {
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
