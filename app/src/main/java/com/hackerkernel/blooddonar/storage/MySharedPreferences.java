package com.hackerkernel.blooddonar.storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Singleton class for sharedPreferences
 */
public class MySharedPreferences {
    //instance field
    private static SharedPreferences mSharedPreference;
    private static MySharedPreferences mInstance = null;
    private static Context mContext;


    //Shared Preference key
    private String KEY_PREFERENCE_NAME = "SavLife";

    //private keyS
    private String KEY_DEFAULT = null;

    //user details keys
    private String KEY_USER_ID = "id",
            KEY_FULL_NAME = "fullname",
            KEY_USER_MOBILE = "mobile",
            KEY_USER_AGE = "age",
            KEY_USER_GENDER = "gender",
            KEY_USER_BLOOD_GROUP = "blood_group",
            KEY_USER_CREATED_AT = "created_at",
            KEY_USER_CITY = "location",
            KEY_USER_LATITUDE = "latitude",
            KEY_USER_LONGITUDE ="longitude";


    private MySharedPreferences() {
        mSharedPreference = mContext.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static MySharedPreferences getInstance(Context context) {
        mContext = context;
        if (mInstance == null) {
            mInstance = new MySharedPreferences();
        }
        return mInstance;
    }

    //Method to set boolean for (AppIntro)
    public void setBooleanKey(String keyname) {
        mSharedPreference.edit().putBoolean(keyname, true).apply();
    }

    /*
    * Method to get boolan key
    * true = means set
    * false = not set (show app intro)
    * */
    public boolean getBooleanKey(String keyname) {
        return mSharedPreference.getBoolean(keyname, false);
    }


    //Method to store user Mobile number
    public void setUserMobile(String mobile) {
        mSharedPreference.edit().putString(KEY_USER_MOBILE, mobile).apply();
    }

    //Method to get User mobile number
    public String getUserMobile() {
        return mSharedPreference.getString(KEY_USER_MOBILE, KEY_DEFAULT);
    }


    //USER FULLNAME
    public void setUserFullname(String name){
        mSharedPreference.edit().putString(KEY_FULL_NAME, name).apply();
    }

    public String getUserFullname(){
        return mSharedPreference.getString(KEY_FULL_NAME, KEY_DEFAULT);
    }

    //USER age
    public void setUserAge(String age){
        mSharedPreference.edit().putString(KEY_USER_AGE, age).apply();
    }

    public String getUserAge(){
        return mSharedPreference.getString(KEY_USER_AGE, KEY_DEFAULT);
    }

    //USER gender
    public void setUserGender(String gender){
        mSharedPreference.edit().putString(KEY_USER_GENDER, gender).apply();
    }

    public String getUserGender(){
        return mSharedPreference.getString(KEY_USER_GENDER, KEY_DEFAULT);
    }

    //USER blood group
    public void setUserBloodGroup(String state){
        mSharedPreference.edit().putString(KEY_USER_BLOOD_GROUP, state).apply();
    }

    public String getUserBloodGroup(){
        return mSharedPreference.getString(KEY_USER_BLOOD_GROUP, KEY_DEFAULT);
    }

    //USER id
    public void setUserId(String id){
        mSharedPreference.edit().putString(KEY_USER_ID, id).apply();
    }

    public String getUserId(){
        return mSharedPreference.getString(KEY_USER_ID, KEY_DEFAULT);
    }

    //USER Created At
    public void setUserCreatedAt(String createdAt){
        mSharedPreference.edit().putString(KEY_USER_CREATED_AT, createdAt).apply();
    }

    public String getUserCreatedAt(){
        return mSharedPreference.getString(KEY_USER_CREATED_AT, KEY_DEFAULT);
    }

    //Method to check user is logged in or not
    public boolean getLoginStatus() {
        //logged in
        return mSharedPreference.getString(KEY_USER_MOBILE, KEY_DEFAULT) != null;
    }

    //USER locatio
    public void setUserCity(String location){
        mSharedPreference.edit().putString(KEY_USER_CITY, location).apply();
    }

    public String getUserCity(){
        return mSharedPreference.getString(KEY_USER_CITY, KEY_DEFAULT);
    }

    public void setUserLatitude(String latitude){
        mSharedPreference.edit().putString(KEY_USER_LATITUDE, latitude).apply();
    }

    public String getUserLatitude(){
        return mSharedPreference.getString(KEY_USER_LATITUDE, KEY_DEFAULT);
    }
    public void setUserLongitude(String longitude){
        mSharedPreference.edit().putString(KEY_USER_LONGITUDE,longitude).apply();
    }
    public String getUserLongitude(){
        return mSharedPreference.getString(KEY_USER_LONGITUDE,KEY_DEFAULT);

    }



}
