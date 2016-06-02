package com.hackerkernel.blooddonar.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.adapter.DealsAdapter;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.constant.EndPoints;
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.parser.JsonParser;
import com.hackerkernel.blooddonar.pojo.DealsPjo;
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


public class DealsFragment extends Fragment {
    @Bind(R.id.deals_recycleView) RecyclerView recyclerView;

    private MySharedPreferences sp;
    private RequestQueue requestQue;

    public DealsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_deals, container, false);

        ButterKnife.bind(this, view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        sp = new MySharedPreferences();
        // Inflate the layout for this fragment

        LinearLayoutManager maneger = new LinearLayoutManager(getActivity());
        // recyclerView.setLayoutManager(maneger);
        DealsAdapter adapter = new DealsAdapter(getActivity());
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setAdapter(adapter);
        requestQue = MyVolley.getInstance().getRequestQueue();

        checkNetworkAvailable();


        return view;

    }

    private void checkNetworkAvailable() {
        if (Util.isNetworkAvailable()){
            getDataInBackground();
        }
    }

    private void getDataInBackground() {
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_DEALS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseDeals(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY,Util.generateApiKey(sp.getUserMobile()));
                params.put(Constants.COM_MOBILE,sp.getUserMobile());
                return params;
            }
        };
        requestQue.add(request);
    }
    public void parseDeals(String response){
        try {
            JSONObject obj = new JSONObject(response);
            boolean returned = obj.getBoolean(Constants.COM_RETURN);
            if (returned){
            int count = obj.getInt(Constants.COM_COUNT);
                if (count <=0 ){
                    //TODO ADD SNACKBAR

                }
                else {
                    JSONArray data = obj.getJSONArray(Constants.COM_DATA);
                    List<DealsPjo> list = JsonParser.parseDeals(data);
                    setupRecycleView(list);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }
    public void setupRecycleView(List<DealsPjo> list){
        DealsAdapter adapter = new DealsAdapter(getActivity());
        adapter.setmList(list);
        recyclerView.setAdapter(adapter);
    }


}
