package com.hackerkernel.blooddonar.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.adapter.DonorAdapter;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.constant.EndPoints;
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.parser.JsonParser;
import com.hackerkernel.blooddonar.pojo.DonorListPojo;
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


public class BestDonorFragment extends Fragment {
    private static final String TAG = BestDonorFragment.class.getSimpleName();
    @Bind(R.id.recycleView) RecyclerView recyclerViewe;
    @Bind(R.id.layoutForSnackbar) View mLayoutForSnackbar;
    private MySharedPreferences sp;
    private RequestQueue mRequestQueue;

    public BestDonorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init SP
        sp = MySharedPreferences.getInstance(getActivity());

        //init request queue
        mRequestQueue = MyVolley.getInstance().getRequestQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_best_donours, container, false);
        ButterKnife.bind(this,view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewe.setLayoutManager(layoutManager);

        /*
        * Method to check internet & call method to get donor in background
        * */
        checkInternetAndGetBestDonor();

        return view;
    }

    /*
    * Method to check internet and get best donor
    * */
    private void checkInternetAndGetBestDonor() {
        if (Util.isNetworkAvailable()){
            getBestDonorInBackground();
        }else {
            //TODO:: no internet , show snackbar, and show need help contacts
        }
    }

    public void getBestDonorInBackground(){
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_BEST_DONOR, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseBestDonorResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG,"MUR::getBestDonorInBackground "+error.getMessage());
                String errorString = MyVolley.handleVolleyError(error);
                if (errorString != null){
                    Util.showRedSnackbar(mLayoutForSnackbar,errorString);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY, Util.generateApiKey(sp.getUserMobile()));
                params.put(Constants.COM_MOBILE,sp.getUserMobile());
                params.put(Constants.LOC_CITY,sp.getUserCity());
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    private void parseBestDonorResponse(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);
            boolean returned = jsonObj.getBoolean(Constants.COM_RETURN);
            String message = jsonObj.getString(Constants.COM_MESSAGE);
            if (returned){
                int count = jsonObj.getInt("count");
                if (count <= 0){
                    Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
                }else {
                    JSONArray dataArray = jsonObj.getJSONArray("data");

                    List<DonorListPojo> list = JsonParser.DonorParser(dataArray);
                    setupDonorRecyclerView(list);
                }
            }else {
                Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Util.showParsingErrorAlert(getActivity());
        }

    }

    private void setupDonorRecyclerView(List<DonorListPojo> list) {
        DonorAdapter adapter = new DonorAdapter(getActivity());
        adapter.setList(list);
        recyclerViewe.setAdapter(adapter);
    }
}
