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

    @Bind(R.id.detail_donor_name) TextView mName;
    @Bind(R.id.detail_donor_age) TextView mAge;
    @Bind(R.id.detail_donor_blood) TextView mBlood;
    @Bind(R.id.detail_donor_gender) TextView mGender;
    @Bind(R.id.detail_donor_image) ImageView mImage;
    @Bind(R.id.detail_last_donated) TextView mLastDonated;
    @Bind(R.id.detail_donor_id) TextView idDonor;
    @Bind(R.id.detail_contact_btn) Button mContactBtn;
    @Bind(R.id.progressBar) ProgressBar mProgressbar;
    @Bind(R.id.scroll_view) ScrollView mScrollView;
    private RequestQueue mRequestQueue;
    private String mDonorId;


    public UserDetailDescriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_detail_description, container, false);
        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        ButterKnife.bind(this,view);


        //check we got donor id internet or not
        if (getActivity().getIntent().hasExtra(Constants.COM_ID)){
            mDonorId = getActivity().getIntent().getExtras().getString(Constants.COM_ID);
        }else{
            Toast.makeText(getActivity(),"Unable to open donor page. Try Again Later",Toast.LENGTH_LONG).show();

        }
        checkNetworkIsAvailable();

        return view;
    }
    private void checkNetworkIsAvailable(){
        if (Util.isNetworkAvailable()){
            fetchDetailDonorInBackground();
        }else {
            Util.noInternetSnackBar(getActivity(),mScrollView);
        }
    }

    private void fetchDetailDonorInBackground() {
        //show pb
        showProgressAndHideLayout(true);
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_DONOR_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showProgressAndHideLayout(false); //hide pb
                parseDetailDonorData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgressAndHideLayout(false);
                Log.d(TAG,"HUS:"+error.getMessage());
                error.printStackTrace();
                String errorString = MyVolley.handleVolleyError(error);
                if (errorString != null){
                    Util.showRedSnackbar(mScrollView,errorString);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY,Util.generateApiKey(mDonorId));
                params.put(Constants.COM_ID,mDonorId);
                return params;
            }
        };
        mRequestQueue.add(request);

    }
    private void parseDetailDonorData(String response){

        try {
            JSONObject obj = new JSONObject(response);
            boolean returned = obj.getBoolean(Constants.COM_RETURN);
            String message = obj.getString(Constants.COM_MESSAGE);
            if (returned){
                JSONArray data =  obj.getJSONArray(Constants.COM_DATA);
                DonorPojo pojo = JsonParser.DetailDonorParser(data);
                setupView(pojo);
            }
            else{
                Util.showRedSnackbar(mScrollView,message);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    * Method to setup view
    * */
    private void setupView(DonorPojo pojo) {
        mName.setText(pojo.getFullName());
        mAge.setText("Age: "+pojo.getAge());
        mBlood.setText(pojo.getBloodGroup());
        mGender.setText("Gender: "+pojo.getGender());
        idDonor.setText("Id: "+pojo.getId());
        mLastDonated.setText(pojo.getLastDonated());
        //download image
        String userImage = EndPoints.IMAGE_BASE_URL + pojo.getImageUrl();
        Glide.with(this)
                .load(userImage)
                .placeholder(R.drawable.placeholder_300_300)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mImage);
    }

    private void showProgressAndHideLayout(boolean state) {
        if (state){
            //show progressbar and hide layout
            mProgressbar.setVisibility(View.VISIBLE);
            mScrollView.setVisibility(View.GONE);
            mContactBtn.setVisibility(View.GONE);
        }else {
            //hide progressbar and show layout
            mProgressbar.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
            mContactBtn.setVisibility(View.VISIBLE);
        }
    }

}
