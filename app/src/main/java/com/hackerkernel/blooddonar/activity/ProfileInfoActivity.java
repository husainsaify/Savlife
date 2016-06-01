package com.hackerkernel.blooddonar.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.constant.EndPoints;
import com.hackerkernel.blooddonar.infrastructure.BaseAuthActivity;
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.parser.JsonParser;
import com.hackerkernel.blooddonar.pojo.ProfileDetailPojo;
import com.hackerkernel.blooddonar.pojo.SimplePojo;
import com.hackerkernel.blooddonar.storage.MySharedPreferences;
import com.hackerkernel.blooddonar.util.ImageUtil;
import com.hackerkernel.blooddonar.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileInfoActivity extends BaseAuthActivity {
    private static final String TAG = ProfileInfoActivity.class.getSimpleName();
    private static final int SELECT_IMAGE_CODE = 100;

    @Bind(R.id.profile_layout_for_snackbar) View mLayoutForSnackbar;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.profile_imageView) ImageView mProfileImage;
    @Bind(R.id.profile_name) TextView mProfileName;
    @Bind(R.id.profile_blood_grup) TextView mProfileBlood;
    @Bind(R.id.profile_Num) TextView mProfileNum;
    @Bind(R.id.profile_last_donated) TextView mProfileLastDonated;
    @Bind(R.id.profile_gender) TextView mProfileGender;
    @Bind(R.id.profile_age) TextView mProfileAge;
    @Bind(R.id.profile_id) TextView mProfileID;
    @Bind(R.id.profile_city) TextView mProfileCity;

    private MySharedPreferences sp;
    private RequestQueue mRequestQue;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);
        ButterKnife.bind(this);

        //init toolbar
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.my_profile);

        sp = MySharedPreferences.getInstance(getApplicationContext());
        mRequestQue = MyVolley.getInstance().getRequestQueue();

        //init pd
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.please_Watit));
        pd.setCancelable(true);

        //when profile pic in clicked
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open gallery
                Intent openGallery = new Intent(Intent.ACTION_PICK);
                openGallery.setType("image/*");
                startActivityForResult(openGallery, SELECT_IMAGE_CODE);
            }
        });

        ifNetworkIsAvailable();
    }

    private void ifNetworkIsAvailable() {
        if (Util.isNetworkAvailable()) {
            parseProfileInfoInBackground();
        } else {
            Util.noInternetSnackBar(this, mLayoutForSnackbar);
        }
    }

    private void parseProfileInfoInBackground() {
        pd.show(); //show pb
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_DONOR_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss(); //hide pb
                parseProfileResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                error.printStackTrace();
                Log.d(TAG,"HUS: parseProfileInfoInBackground: "+error.getMessage());
                String errorString = MyVolley.handleVolleyError(error);
                if (errorString != null){
                    Util.showRedSnackbar(mLayoutForSnackbar,errorString);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY, Util.generateApiKey(sp.getUserMobile()));
                params.put(Constants.COM_MOBILE, sp.getUserMobile());
                return params;
            }
        };
        mRequestQue.add(request);
    }

    private void parseProfileResponse(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            boolean returned = obj.getBoolean(Constants.COM_RETURN);
            String message = obj.getString(Constants.COM_MESSAGE);
            if (returned){
                JSONArray data = obj.getJSONArray(Constants.COM_DATA);
                ProfileDetailPojo pojo = JsonParser.ParseUserProfile(data);
                mProfileName.setText(pojo.getProfileNAme());
                mProfileBlood.setText(pojo.getProfileBlood());
                mProfileNum.setText(pojo.getProfileNum());
                mProfileLastDonated.setText(pojo.getProfilelastDonated());
                mProfileAge.setText(pojo.getProfileAge());
                mProfileCity.setText(pojo.getProfileCity());
                mProfileGender.setText(pojo.getProfileGender());
                mProfileID.setText(pojo.getProfileId());
                String imageURl = EndPoints.IMAGE_BASE_URL+ pojo.getProfileURL();
                Glide.with(getApplicationContext())
                        .load(imageURl)
                        .into(mProfileImage);
            }else {
                Util.showRedSnackbar(mLayoutForSnackbar,message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"HUS: parseProfileInfoInBackground: "+e.getMessage());
            Util.showParsingErrorAlert(ProfileInfoActivity.this);
        }
    }

    //When profile picture is selected

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_CODE && resultCode == RESULT_OK && data != null){
            Uri imageUri = data.getData();
            loadAndUploadImage(imageUri);
        }
    }




    /*
    * This method check internet
    * Load the image to imageview
    * And upload the image to server
    * */
    private void loadAndUploadImage(Uri imageUri) {
        //if network avaialble
        if (Util.isNetworkAvailable()){
            //compress image & set in imageview
            String imagePath = ImageUtil.getFilePathFromUri(this, imageUri);
            final Bitmap lowResBitmap = ImageUtil.decodeBitmapFromFilePath(imagePath, 300, 300);
            mProfileImage.setImageBitmap(lowResBitmap);

            //compress image to base64
            String imageBase64 = ImageUtil.compressImageToBase64(lowResBitmap);

            //upload image to server
            UploadProfilePic uploadProfilePic = new UploadProfilePic();
            uploadProfilePic.execute(imageBase64);
        }else {
            //if no network available
            Util.noInternetSnackBar(this,mLayoutForSnackbar);
        }
    }

    /*
    * Asynctask to upload status image to server
    * */
    public class UploadProfilePic extends AsyncTask<String,Integer,String> {
        public UploadProfilePic(){
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(EndPoints.UPLOAD_PROFILE_PIC);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os,"UTF-8"));

                //set post data
                HashMap<String,String> postParam = new HashMap<>();
                postParam.put(Constants.COM_APIKEY,Util.generateApiKey(sp.getUserMobile()));
                postParam.put(Constants.COM_MOBILE,sp.getUserMobile());
                postParam.put(Constants.COM_IMG,params[0]);

                writer.write(Util.getPostDataFromHashmap(postParam));
                writer.flush();
                writer.close();
                os.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String response;
                    while ((response = reader.readLine()) != null){
                        sb.append(response);
                    }
                    return sb.toString();
                }

                return null;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            Log.d(TAG,"HUS: dddd "+s);
            parseProfilePicResponse(s);
        }
    }

    private void parseProfilePicResponse(String response) {
        try {
            SimplePojo current = JsonParser.SimpleParser(response);
            if (current.isReturned()){
                Toast.makeText(getApplicationContext(),current.getMessage(),Toast.LENGTH_LONG).show();
                finish();
            }else {
                Util.showSimpleDialog(this,"OOPS!!",current.getMessage());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"HUS: "+e.getMessage());
            Util.showParsingErrorAlert(this);
        }
    }
}
