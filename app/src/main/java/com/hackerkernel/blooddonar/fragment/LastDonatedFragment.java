package com.hackerkernel.blooddonar.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
    @Bind(R.id.listView_lastDonated)
    ListView listView;
    @Bind(R.id.scroll_view_lastDonated)
    ScrollView scrollView;
    MySharedPreferences sp;
    private String iD;
    private RequestQueue mRequestQue;



    public LastDonatedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_last_donated, container, false);
        sp = MySharedPreferences.getInstance(getActivity());
        mRequestQue = MyVolley.getInstance().getRequestQueue();
        iD = getActivity().getIntent().getStringExtra(Constants.COM_ID);
        ButterKnife.bind(this,view);
        checkNetworkstate(iD);



        return view;
    }
    private void checkNetworkstate(String id){
        if (Util.isNetworkAvailable()){
            parseDataInBackground(id);
        }
        else {
            Util.noInternetSnackBar(getActivity(),scrollView);
        }
    }

    private void parseDataInBackground(final String id) {
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.LAST_DONATED, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean returned = obj.getBoolean(Constants.COM_RETURN);
                    if (returned){

                        int count = obj.getInt(Constants.COM_COUNT);
                        if (count <=0){
                            Toast.makeText(getActivity(),"No Donour FOund",Toast.LENGTH_LONG).show();
                        }
                        else {
                            JSONArray data = obj.getJSONArray(Constants.COM_DATA);
                            List<String> mList = JsonParser.getLastDonated(data);
                            setupView(mList);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY,Util.generateApiKey(sp.getUserId()));
                params.put(Constants.COM_ID,id);
                return params;
            }
        };
        mRequestQue.add(request);
    }

    private void setupView(List<String> list) {
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,list);
        listView.setAdapter(arrayAdapter);
    }

}
