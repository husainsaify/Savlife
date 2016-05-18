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
    private String KEY_FULL_NAME = "fullname",
            KEY_USER_MOBILE = "mobile",
            KEY_USER_AGE = "age",
            KEY_USER_GENDER = "gender",
            KEY_USER_BLOOD_GROUP = "blood_group";

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
        return mSharedPreference.getString(KEY_FULL_NAME, null);
    }

    //USER age
    public void setUserAge(String age){
        mSharedPreference.edit().putString(KEY_USER_AGE, age).apply();
    }

    public String getUserAge(){
        return mSharedPreference.getString(KEY_USER_AGE, null);
    }

    //USER gender
    public void setUserGender(String gender){
        mSharedPreference.edit().putString(KEY_USER_GENDER, gender).apply();
    }

    public String getUserGender(){
        return mSharedPreference.getString(KEY_USER_GENDER, null);
    }

    //USER state
    public void setUserBloodGroup(String state){
        mSharedPreference.edit().putString(KEY_USER_BLOOD_GROUP, state).apply();
    }

    public String getUserBloodGroup(){
        return mSharedPreference.getString(KEY_USER_BLOOD_GROUP, null);
    }

    //Method to check user is logged in or not
    public boolean getLoginStatus() {
        //logged in
        return mSharedPreference.getString(KEY_USER_MOBILE, KEY_DEFAULT) != null;
    }

}
