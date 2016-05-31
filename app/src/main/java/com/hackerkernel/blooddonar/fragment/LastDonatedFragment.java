package com.hackerkernel.blooddonar.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.constant.EndPoints;
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.parser.JsonParser;
import com.hackerkernel.blooddonar.storage.MySharedPreferences;
import com.hackerkernel.blooddonar.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class LastDonatedFragment extends Fragment {
    private static final String TAG = LastDonatedFragment.class.getSimpleName();
    @Bind(R.id.donation_history_listview) ListView mListview;
    @Bind(R.id.donation_history_placeholder) TextView mPlaceholder;
    @Bind(R.id.donation_history_progressbar) ProgressBar mProgressbar;
    private String mUserId;
    private RequestQueue mRequestQueue;



    public LastDonatedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        mUserId = getActivity().getIntent().getStringExtra(Constants.COM_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_last_donated, container, false);
        ButterKnife.bind(this,view);
        checkInternetAndFetchDonationHistory();
        return view;
    }

    private void checkInternetAndFetchDonationHistory(){
        if (Util.isNetworkAvailable()){
            fetchDonationHistoryInBackground();
        }
        else {
            //hide listview & pb & show placeholder
            mListview.setVisibility(View.GONE);
            mProgressbar.setVisibility(View.GONE);
            mPlaceholder.setVisibility(View.VISIBLE);
            mPlaceholder.setText(getString(R.string.no_internet_connection));
        }
    }

    private void fetchDonationHistoryInBackground() {
        showPbAndHideListview(true); //show pb
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.DONATED_HISTORY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showPbAndHideListview(false); //hide pb
                parseDonationHistoryResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showPbAndHideListview(false); //hide pb
                error.printStackTrace();
                Log.d(TAG,"HUS: fetchDonationHistoryInBackground: "+error.getMessage());
                String errorString = MyVolley.handleVolleyError(error);
                if (errorString != null){
                    mListview.setVisibility(View.GONE);
                    mProgressbar.setVisibility(View.GONE);
                    mPlaceholder.setVisibility(View.VISIBLE);
                    mPlaceholder.setText(errorString);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY,Util.generateApiKey(mUserId));
                params.put(Constants.COM_ID,mUserId);
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseDonationHistoryResponse(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            boolean returned = obj.getBoolean(Constants.COM_RETURN);
            String message = obj.getString(Constants.COM_MESSAGE);
            //return true
            if (returned){
                int count = obj.getInt(Constants.COM_COUNT);
                if (count > 0){
                    JSONArray data = obj.getJSONArray(Constants.COM_DATA);
                    List<String> mList = JsonParser.getLastDonated(data);
                    setupView(mList);
                }else {
                    //return false
                    mListview.setVisibility(View.GONE);
                    mProgressbar.setVisibility(View.GONE);
                    mPlaceholder.setVisibility(View.VISIBLE);
                    mPlaceholder.setText(message);
                }
            }else {
                //return false
                mListview.setVisibility(View.GONE);
                mProgressbar.setVisibility(View.GONE);
                mPlaceholder.setVisibility(View.VISIBLE);
                mPlaceholder.setText(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupView(List<String> list) {
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,list);
        mListview.setAdapter(arrayAdapter);
    }

    private void showPbAndHideListview(boolean state){
        if (state){
            mProgressbar.setVisibility(View.VISIBLE);
            mListview.setVisibility(View.GONE);
        }else {
            mProgressbar.setVisibility(View.GONE);
            mListview.setVisibility(View.VISIBLE);
        }
    }
}
