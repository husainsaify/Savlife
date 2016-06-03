package com.hackerkernel.blooddonar.dialog;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.*;
import android.widget.DatePicker;

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
import com.hackerkernel.blooddonar.pojo.SimplePojo;
import com.hackerkernel.blooddonar.storage.MySharedPreferences;
import com.hackerkernel.blooddonar.util.Util;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Murtaza on 5/31/2016.
 */
public class DateSetting implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = DateSetting.class.getSimpleName();
    private Context context;
    private ProgressDialog pd;
    private RequestQueue mRequestQueue;
    private MySharedPreferences sp;

    public DateSetting(Context context){
        this.context = context;
        this.pd = new ProgressDialog(context);
        this.pd.setMessage(context.getString(R.string.please_Watit));
        this.mRequestQueue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(context);
    }
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        int newMonth = monthOfYear+1;
        String selectedString = dayOfMonth+"/"+newMonth+"/"+year;
        checkInternetAndAddDate(selectedString);
    }

    /*
    * Method to check internet and add date
    * */
    private void checkInternetAndAddDate(String selectedString) {
        if (Util.isNetworkAvailable()){
            addDonationHistoryInBackground(selectedString);
        }else {
            Toast.makeText(context,context.getString(R.string.no_internet_connection),Toast.LENGTH_LONG).show();
        }
    }

    /*
    * Method to add donation history in background
    * */
    private void addDonationHistoryInBackground(final String selectedString) {
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.ADD_DONATION_HISTORY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    SimplePojo pojo = JsonParser.SimpleParser(response);
                    Toast.makeText(context,pojo.getMessage(),Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Util.showParsingErrorAlert(context);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                error.printStackTrace();
                Log.e(TAG,"HUS: addDonationHistoryInBackground: "+error.getMessage());
                String errorString = MyVolley.handleVolleyError(error);
                if (errorString != null){
                    Toast.makeText(context,errorString,Toast.LENGTH_LONG).show();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put(Constants.COM_APIKEY,Util.generateApiKey(sp.getUserId()));
                param.put(Constants.COM_ID,sp.getUserId());
                param.put(Constants.COM_DATE,selectedString);
                return param;
            }
        };
        mRequestQueue.add(request);
    }


}
