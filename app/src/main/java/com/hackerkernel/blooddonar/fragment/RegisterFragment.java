package com.hackerkernel.blooddonar.fragment;


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
import android.widget.Spinner;
import android.widget.Toast;

import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.util.Util;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Fragment to register a new user
 */
public class RegisterFragment extends Fragment {
    private static final String TAG = RegisterFragment.class.getSimpleName();
    @Bind(R.id.reg_fullname) EditText mFullname;
    @Bind(R.id.reg_mobile) EditText mMobile;
    @Bind(R.id.reg_gender_group) RadioGroup mGenderGroup;
    @Bind(R.id.reg_age) EditText mAge;
    @Bind(R.id.reg_blood_group) Spinner mBloodGroup;
    @Bind(R.id.reg_button) Button mRegButton;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

            Log.d(TAG,"HUS: "+fullname+"/"+mobile+"/"+gender+"/"+age+"/"+bloodGroup);
        }else {
            Toast.makeText(getActivity(), R.string.no_internet_available,Toast.LENGTH_LONG).show();
        }
    }

}
