package com.hackerkernel.blooddonar.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

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

public class SearchActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.blood_spinner) Spinner mBloodSpinner;
    @Bind(R.id.location_spinner) Spinner mLocationSpinner;
    @Bind(R.id.search_btn) ImageButton mSearchBtn;
    private String mBloodGroup, mLocation;
    private MySharedPreferences sp;
    private RequestQueue mRequestQue;
    @Bind(R.id.search_recycleView)
    RecyclerView mRecyclerView;
    @Bind(R.id.search_placeholder)
    TextView mPlaceholder;
    private ProgressDialog progressDialog;

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
    }

    public void checkInternetAndDoSearch(){
        if (Util.isNetworkAvailable()){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            doSearchInBackground();
        }
    }

    private void doSearchInBackground() {
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.SEARCH_DONOR, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG","MUR:"+response);
            parseBestDonorResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG","MUR: "+error.getMessage());
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
            hideDialog();
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
                    List<DonorListPojo> list = JsonParser.DonorParser(dataArray);
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
        DonorAdapter adapter = new DonorAdapter(getApplicationContext());
        adapter.setList(list);
        mRecyclerView.setAdapter(adapter);
    }
    private void hideDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
