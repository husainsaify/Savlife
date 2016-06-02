package com.hackerkernel.blooddonar.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.constant.EndPoints;
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.parser.JsonParser;
import com.hackerkernel.blooddonar.pojo.DonorPojo;
import com.hackerkernel.blooddonar.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailDescriptionFragment extends Fragment {
    private static final String TAG = UserDetailDescriptionFragment.class.getSimpleName();

    @Bind(R.id.detail_donor_age) TextView mAge;
    @Bind(R.id.detail_donor_blood) TextView mBlood;
    @Bind(R.id.detail_donor_gender) TextView mGender;
    @Bind(R.id.detail_donor_id) TextView idDonor;
    @Bind(R.id.detail_donor_name) TextView mFullname;
    @Bind(R.id.detail_contact_btn) Button mContactBtn;


    public UserDetailDescriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String age = getArguments().getString(Constants.COM_AGE);
        String blood = getArguments().getString(Constants.COM_BLOOD);
        String gender = getArguments().getString(Constants.COM_GENDER);
        String id = getArguments().getString(Constants.COM_ID);
        String fullname = getArguments().getString(Constants.COM_FULLNAME);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_detail_description, container, false);
        ButterKnife.bind(this,view);

        mFullname.setText(fullname);
        mAge.setText(age+" Years old");
        mBlood.setText(blood);
        mGender.setText(gender);
        idDonor.setText("Donor Id: "+id);

        mContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.dialNumber(getActivity(),"8871334161");
            }
        });

        return view;
    }

}
