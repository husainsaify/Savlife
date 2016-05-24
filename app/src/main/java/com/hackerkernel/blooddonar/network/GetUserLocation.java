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

import com.hackerkernel.blooddonar.storage.MySharedPreferences;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by husain on 5/24/2016.
 */
public class GetUserLocation {
    private Context mContext;
    public GetUserLocation(Context mContext){
        this.mContext = mContext;
    }
    public void getLocation(){
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();

            locationManager.getBestProvider(criteria, true);

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
                    Log.d("HUS","HUS:  dd "+location.getLatitude()+"/"+location.getLongitude());
                    //insert city to Shared Preference
                    MySharedPreferences sp = MySharedPreferences.getInstance(mContext);
                    sp.setUserLocation(cityname);
                    sp.setUserLatitude(location.getLatitude()+"");
                    sp.setUserLongitude(location.getLongitude()+"");

                    Log.d("HUS","HUS: "+sp.getUserLocation()+"/"+sp.getUserLatitude()+"/"+sp.getUserLongitude());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(mContext,"Location Permission denied by user",Toast.LENGTH_LONG).show();
        }


    }
}
