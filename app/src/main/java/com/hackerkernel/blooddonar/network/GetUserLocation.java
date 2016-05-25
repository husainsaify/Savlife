package com.hackerkernel.blooddonar.network;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.constant.EndPoints;
import com.hackerkernel.blooddonar.storage.MySharedPreferences;
import com.hackerkernel.blooddonar.util.Util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * CLass to get location of device and update it in api
 */
public class GetUserLocation {
    private static final String TAG = GetUserLocation.class.getSimpleName();
    private Context mContext;
    private MySharedPreferences sp;
    private RequestQueue mRequestQueue;

    public GetUserLocation(Context context){
        this.mContext = context;
        this.sp = MySharedPreferences.getInstance(context);
        this.mRequestQueue = MyVolley.getInstance().getRequestQueue();
    }
    public void getLocation(){
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        locationManager.getBestProvider(criteria, true);

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(mContext,"location denied by user",Toast.LENGTH_LONG).show();
            //TODO:: show a dialog box saying location permission denied by user and take approprate steps
            return;
        }

        //nce  you  know  the  name  of  the  LocationProvider,  you  can  call getLastKnownPosition() to  find  out  where  you  were  recently.
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
        Geocoder gcd = new Geocoder(mContext, Locale.getDefault());

        if (location == null){
            Toast.makeText(mContext,"Location Permission denied by user",Toast.LENGTH_LONG).show();
            return;
        }
        List<Address> addresses;

        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0) {
                String cityname = addresses.get(0).getLocality();
                Log.d("HUS","HUS:  dd "+cityname+"/"+location.getLatitude()+"/"+location.getLongitude());

                //insert city to Shared Preference
                sp.setUserCity(cityname);
                sp.setUserLatitude(location.getLatitude()+"");
                sp.setUserLongitude(location.getLongitude()+"");

                Log.d(TAG,"HUS: sp : "+sp.getUserCity()+"/"+sp.getUserLatitude()+"/"+sp.getUserLongitude());

                //method to update user location in api
                updateUserLocationInBackground(cityname,location.getLatitude(),location.getLongitude());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /*
    * Method to update user location in API
    * */
    private void updateUserLocationInBackground(final String cityname, final double latitude, final double longitude) {
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.UPDATE_USER_LOCATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"HUS: updateUserLocationInBackground: "+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG,"HUS: updateUserLocationInBackground: "+error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY, Util.generateApiKey(sp.getUserMobile()));
                params.put(Constants.COM_MOBILE,sp.getUserMobile());
                params.put(Constants.LOC_CITY,cityname);
                params.put(Constants.LOC_LATITUDE,latitude+"");
                params.put(Constants.LOC_LONGITUDE,longitude+"");
                return params;
            }
        };
        mRequestQueue.add(request);
    }
}
