package com.hackerkernel.blooddonar.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.adapter.FeedsListAdapter;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.constant.EndPoints;
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.parser.JsonParser;
import com.hackerkernel.blooddonar.pojo.FeedsListPojo;
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
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FeedFragment extends Fragment {
    private static final String TAG = FeedFragment.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private ProgressDialog pd;
    private MySharedPreferences sp;
    private static final int SELECT_IMAGE_CODE = 100;
    private LayoutInflater inflater;

    @Bind(R.id.post_status_btn) Button statusButton;
    @Bind(R.id.post_photo_btn) Button postPhotoButton;
    @Bind(R.id.feed_linear_layout) LinearLayout mLayoutForSnackbar;
    @Bind(R.id.feed_recyclerview) RecyclerView mFeedRecyclerView;
    @Bind(R.id.feed_progressbar) ProgressBar mProgressbar;
    @Bind(R.id.feed_placeholder) TextView mPlaceholder;

    public FeedFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(getActivity());
        //init pb
        pd = new ProgressDialog(getActivity());
        pd.setMessage(getString(R.string.please_Watit));
        pd.setCancelable(true);

        //init layout inflator
        inflater = getLayoutInflater(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_feed, container, false);

        ButterKnife.bind(this, view);

        //add layout manager to recyclerview
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        mFeedRecyclerView.setLayoutManager(manager);

        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStatusAlertDialog();
            }
        });
        postPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open gallery
                Intent openGallery = new Intent(Intent.ACTION_PICK);
                openGallery.setType("image/*");
                startActivityForResult(openGallery, SELECT_IMAGE_CODE);
            }
        });
        checkInternetAndFetchFeeds();
        // Inflate the layout for this fragment
        return view;
    }

    /*
    * Method to check internet connection and fetch feeds
    * */
    private void checkInternetAndFetchFeeds() {
        if (Util.isNetworkAvailable()){
            fetchFeedsInBackground();
        }else {
            Util.noInternetSnackBar(getActivity(),mLayoutForSnackbar);
        }
    }

    /*
    * Method to fetch feeds in background
    * */
    private void fetchFeedsInBackground() {
        //show pb
        mProgressbar.setVisibility(View.VISIBLE);
        mFeedRecyclerView.setVisibility(View.GONE);
        mPlaceholder.setVisibility(View.GONE);
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_FEEDS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"HUS: "+response);
                parseFeedsResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG,"HUS: fetchFeedsInBackground: "+error.getMessage());
                String errorString = MyVolley.handleVolleyError(error);
                //hide pb
                mProgressbar.setVisibility(View.GONE);
                mPlaceholder.setVisibility(View.VISIBLE);
                mPlaceholder.setText(errorString);
                mFeedRecyclerView.setVisibility(View.GONE);
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

    /*
    * Method to parse feeds response
    * */
    private void parseFeedsResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean returned = jsonObject.getBoolean(Constants.COM_RETURN);
            String message = jsonObject.getString(Constants.COM_MESSAGE);
            //success
            if (returned) {
                //hide pb
                mProgressbar.setVisibility(View.GONE);
                mPlaceholder.setVisibility(View.GONE);
                mFeedRecyclerView.setVisibility(View.VISIBLE);
                //if return is success get data array
                JSONArray dataArray = jsonObject.getJSONArray(Constants.COM_DATA);
                List<FeedsListPojo> list = JsonParser.FeedsListParser(dataArray);
                setupFeedsRecyclerview(list);
            }else {
                //hide pb & recylerview show placeholder
                mProgressbar.setVisibility(View.GONE);
                mPlaceholder.setVisibility(View.VISIBLE);
                mPlaceholder.setText(message);
                mFeedRecyclerView.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"HUS: parseFeedsResponse: "+e.getMessage());
            Util.showParsingErrorAlert(getActivity());
        }
    }

    /*
    * Method to setup feeds recyclerview
    * */
    private void setupFeedsRecyclerview(List<FeedsListPojo> list) {
        FeedsListAdapter adapter = new FeedsListAdapter(getActivity());
        adapter.setList(list);
        mFeedRecyclerView.setAdapter(adapter);
    }

    private void openStatusAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View status = inflater.inflate(R.layout.alert_status_dialoge, null);
        final EditText statusEditText = (EditText) status.findViewById(R.id.edit_status);

        builder.setView(status)
                .setPositiveButton(R.string.post, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get status from edit text
                        String status = statusEditText.getText().toString().trim();
                        checkInternetAndUploadStatus(status);
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*
    * Method to check internet and upload status
    * */
    private void checkInternetAndUploadStatus(String status) {
        if (Util.isNetworkAvailable()) {
            if (status.isEmpty()){
                Toast.makeText(getActivity(),"OOPS! Status cannot be empty",Toast.LENGTH_SHORT).show();
                return;
            }
            uploadStatusInBackground(status);
        } else {
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * Method to upload status in background
    * */
    private void uploadStatusInBackground(final String status) {
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.POST_STATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                //parse response and show results
                parseUploadStatusResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                error.printStackTrace();
                String volleyError = MyVolley.handleVolleyError(error);
                if (volleyError != null) {
                    Util.showRedSnackbar(mLayoutForSnackbar, volleyError);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY, Util.generateApiKey(sp.getUserMobile()));
                params.put(Constants.COM_MOBILE, sp.getUserMobile());
                params.put(Constants.COM_STATUS, status);
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null) {
            //get uri of the selected image
            Uri image = data.getData();
            Log.d(TAG, "HUS: selectedImage: " + image);
            openPhotoAlertDialog(image);
        }
    }


    private void openPhotoAlertDialog(Uri imageUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = inflater.inflate(R.layout.alert_image_dialoge, null);
        //find views
        ImageView postImageView = (ImageView) view.findViewById(R.id.image_dialog_imageview);
        final EditText postStatusView = (EditText) view.findViewById(R.id.image_dialog_status);

        //set selected image to imageview
        String imagePath = ImageUtil.getFilePathFromUri(getActivity(), imageUri);
        final Bitmap lowResBitmap = ImageUtil.decodeBitmapFromFilePath(imagePath, 400, 200);
        postImageView.setImageBitmap(lowResBitmap);

        builder.setView(view)
                .setPositiveButton(R.string.post, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get status text and base64 image
                        String status = postStatusView.getText().toString().trim();
                        String imageBase64 = ImageUtil.compressImageToBase64(lowResBitmap);
                        checkInternetAndUploadPhoto(status,imageBase64);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*
    * Method to check internet and perform validation before uploading image
    * */
    private void checkInternetAndUploadPhoto(String status, String imageBase64) {
        if (Util.isNetworkAvailable()){
            if (status.isEmpty()){
                Toast.makeText(getActivity(),"OOPS! Status cannot be empty",Toast.LENGTH_LONG).show();
                return;
            }
            //call async task to upload image to server
            UploadPhotoStatus uploadPhotoStatus = new UploadPhotoStatus();
            uploadPhotoStatus.execute(status,imageBase64);
        }else {
            Toast.makeText(getActivity(),R.string.no_internet_connection,Toast.LENGTH_LONG).show();
        }
    }

    /*
    * Asynctask to upload status image to server
    * */
    public class UploadPhotoStatus extends AsyncTask<String,Integer,String> {
        public UploadPhotoStatus(){
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(EndPoints.POST_STATUS);
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
                postParam.put(Constants.COM_STATUS,params[0]);
                postParam.put(Constants.COM_IMG,params[1]);

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
            parseUploadStatusResponse(s);
        }
    }

    /*
    * Method to parse upload status response
    * text status or photo status
    * */
    private void parseUploadStatusResponse(String response) {
        try {
            SimplePojo simplePojo = JsonParser.SimpleParser(response);
            if (simplePojo.isReturned()) {
                Toast.makeText(getActivity(), simplePojo.getMessage(), Toast.LENGTH_LONG).show();

                //call method to referesh feed list
                checkInternetAndFetchFeeds();
            } else {
                //show alert dialog
                Util.showSimpleDialog(getActivity(),getString(R.string.oops),simplePojo.getMessage());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"HUS: parseUploadStatusResponse: "+e.getMessage());
            Util.showParsingErrorAlert(getActivity());
        }
    }
}
