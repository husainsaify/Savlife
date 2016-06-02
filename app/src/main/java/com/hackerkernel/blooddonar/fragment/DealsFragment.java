package com.hackerkernel.blooddonar.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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


public class DealsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = DealsFragment.class.getSimpleName();
    @Bind(R.id.deals_recycleView) RecyclerView mRecyclerView;
    @Bind(R.id.deals_placeholder) TextView mPlaceholder;
    @Bind(R.id.deals_swipe_refresh) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.layout_for_snackbar) View mLayoutForSnackbar;

    private MySharedPreferences sp;
    private RequestQueue mRequestQueue;

    public DealsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = new MySharedPreferences();
        mRequestQueue = MyVolley.getInstance().getRequestQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deals, container, false);
        ButterKnife.bind(this, view);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        checkInternetAndFetchDeals();
    }

    private void checkInternetAndFetchDeals() {
        if (Util.isNetworkAvailable()){
            fetchDealsInBackground();
        }else {
            Util.noInternetSnackBar(getActivity(),mLayoutForSnackbar);
            mRecyclerView.setVisibility(View.GONE);
            mPlaceholder.setVisibility(View.VISIBLE);
            mPlaceholder.setText(getText(R.string.no_internet_connection));
        }
    }

    private void fetchDealsInBackground() {
        startRefreshing();
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_DEALS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                stopRefreshing();
                parseDealsResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                stopRefreshing();
                error.printStackTrace();
                Log.d(TAG,"HUS::fetchDealsInBackground "+error.getMessage());
                String errorString = MyVolley.handleVolleyError(error);
                if (errorString != null){
                    Util.showRedSnackbar(mLayoutForSnackbar,errorString);
                }
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
        mRequestQueue.add(request);
    }

    public void parseDealsResponse(String response){
        try {
            JSONObject obj = new JSONObject(response);
            boolean returned = obj.getBoolean(Constants.COM_RETURN);
            String message = obj.getString(Constants.COM_RETURN);
            if (returned){
            int count = obj.getInt(Constants.COM_COUNT);
                if (count <=0 ){
                    mRecyclerView.setVisibility(View.GONE);
                    mPlaceholder.setVisibility(View.VISIBLE);
                    mPlaceholder.setText(message);
                }
                else {
                    JSONArray data = obj.getJSONArray(Constants.COM_DATA);
                    List<DealsPjo> list = JsonParser.parseDeals(data);
                    setupRecyclerView(list);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"HUS: parseDealsResponse: "+e.getMessage());
            Util.showParsingErrorAlert(getActivity());
        }
    }

    public void setupRecyclerView(List<DealsPjo> list){
        DealsAdapter adapter = new DealsAdapter(getActivity());
        adapter.setmList(list);
        mRecyclerView.setVisibility(View.VISIBLE);
        mPlaceholder.setVisibility(View.GONE);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        checkInternetAndFetchDeals();
    }

    //method to stop swipeRefreshlayout refresh icon
    private void stopRefreshing() {
        if(mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void startRefreshing(){
        if(!mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
        }
    }
}
