package com.hackerkernel.blooddonar.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.adapter.DonorListAdapter;
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

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = SearchActivity.class.getSimpleName();
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.blood_spinner) Spinner mBloodSpinner;
    @Bind(R.id.location_spinner) Spinner mLocationSpinner;
    @Bind(R.id.search_btn) ImageButton mSearchBtn;
    @Bind(R.id.search_recycleView) RecyclerView mRecyclerView;
    @Bind(R.id.search_placeholder) TextView mPlaceholder;

    private ProgressDialog progressDialog;
    private String mBloodGroup, mLocation;
    private MySharedPreferences sp;
    private RequestQueue mRequestQue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Search");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRequestQue = MyVolley.getInstance().getRequestQueue();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_Watit));
        progressDialog.setCancelable(true);

        sp = MySharedPreferences.getInstance(this);
        LinearLayoutManager linearLayoutManeger = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(linearLayoutManeger);

        mBloodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               mBloodGroup = mBloodSpinner.getItemAtPosition(position).toString().toLowerCase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mLocation = mLocationSpinner.getItemAtPosition(position).toString().toLowerCase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternetAndDoSearch();
            }
        });

        checkInternetAndGetCityList();

    }

    /*
    * Method to check internet and get city list
    * */
    private void checkInternetAndGetCityList() {
        if (Util.isNetworkAvailable()){
            getCityInListInBackground();
        }else {
            Toast.makeText(getApplicationContext(),"Unable to download city list. Check your internet connection",Toast.LENGTH_LONG).show();
        }
    }

    /*
    * Method to get city list in background
    * */
    private void getCityInListInBackground() {
        progressDialog.show(); //show pd
        StringRequest request = new StringRequest(Request.Method.GET, EndPoints.GET_CITY_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                parseCityListResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                error.printStackTrace();
                Log.e(TAG,"HUS: getCityInListInBackground: "+error.getMessage());
                String errorString = MyVolley.handleVolleyError(error);
                if (errorString != null){
                    Util.showSimpleDialog(SearchActivity.this,"OOPS!!",errorString);
                }
            }
        });
        mRequestQue.add(request);
    }

    /*
    * Method to parse city list response
    * */
    private void parseCityListResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean returned = jsonObject.getBoolean(Constants.COM_RETURN);
            String message = jsonObject.getString(Constants.COM_MESSAGE);
            //check returned
            if (returned){
                //fetch city list
                JSONArray data = jsonObject.getJSONArray(Constants.COM_DATA);
                List<String> citylist = JsonParser.CityListParser(data);
                setupCityList(citylist);
            }else {
                Util.showSimpleDialog(this,"OOPS!! Unable to get city list",message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"HUS: parseCityListResponse: "+e.getMessage());
            Util.showParsingErrorAlert(this);
        }
    }

    /*
    * Method to setup city list
    * */
    private void setupCityList(List<String> citylist) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,citylist);
        mLocationSpinner.setAdapter(adapter);
    }

    public void checkInternetAndDoSearch(){
        if (Util.isNetworkAvailable()){
            doSearchInBackground();
        }else {
            Toast.makeText(getApplicationContext(),R.string.no_internet_connection,Toast.LENGTH_LONG).show();
        }
    }

    private void doSearchInBackground() {
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,
                EndPoints.SEARCH_DONOR, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                parseBestDonorResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                error.printStackTrace();
                Log.d("TAG","HUS: doSearchInBackground: "+error.getMessage());
                String stringError = MyVolley.handleVolleyError(error);
                if (stringError != null){
                    mRecyclerView.setVisibility(View.GONE);
                    mPlaceholder.setVisibility(View.VISIBLE);
                    mPlaceholder.setText(stringError);
                }
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY, Util.generateApiKey(sp.getUserMobile()));
                params.put(Constants.COM_MOBILE,sp.getUserMobile());
                params.put(Constants.COM_BLOOD,mBloodGroup);
                params.put(Constants.LOC_CITY,mLocation);
                return params;
            }
        };

        mRequestQue.add(request);

    }

    private void parseBestDonorResponse(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);
            boolean returned = jsonObj.getBoolean(Constants.COM_RETURN);
            String message = jsonObj.getString(Constants.COM_MESSAGE);
            if (returned){
                int count = jsonObj.getInt(Constants.COM_COUNT);
                //when no donor found for this place
                if (count <= 0){
                    mRecyclerView.setVisibility(View.GONE);
                    mPlaceholder.setVisibility(View.VISIBLE);
                    mPlaceholder.setText(message);
                }else {
                    //donor found
                    mPlaceholder.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);

                    JSONArray dataArray = jsonObj.getJSONArray(Constants.COM_DATA);
                    List<DonorListPojo> list = JsonParser.DonorListParser(dataArray);
                    setupDonorRecyclerView(list);
                }
            }else {
                //some auth error
                mRecyclerView.setVisibility(View.GONE);
                mPlaceholder.setVisibility(View.VISIBLE);
                mPlaceholder.setText(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Util.showParsingErrorAlert(getApplicationContext());
        }

    }

    private void setupDonorRecyclerView(List<DonorListPojo> list) {
        DonorListAdapter adapter = new DonorListAdapter(this);
        adapter.setList(list);
        mRecyclerView.setAdapter(adapter);
    }
}
