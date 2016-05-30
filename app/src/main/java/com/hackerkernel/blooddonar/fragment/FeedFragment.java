package com.hackerkernel.blooddonar.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.hackerkernel.blooddonar.pojo.SimplePojo;
import com.hackerkernel.blooddonar.storage.MySharedPreferences;
import com.hackerkernel.blooddonar.util.ImageUtil;
import com.hackerkernel.blooddonar.util.Util;

import org.json.JSONException;

import java.util.HashMap;
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

    @Bind(R.id.post_status_btn)
    Button statusButton;
    @Bind(R.id.post_photo_btn)
    Button postPhotoButton;
    @Bind(R.id.feed_linear_layout)
    LinearLayout linearLayout;

    public FeedFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(getActivity());
        //init pb
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Uploading Post");
        pd.setCancelable(true);

        //init layout inflator
        inflater = getLayoutInflater(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_feed, container, false);

        ButterKnife.bind(this, view);
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

        // Inflate the layout for this fragment
        return view;
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
                try {
                    SimplePojo simplePojo = JsonParser.SimpleParser(response);
                    if (simplePojo.isReturned()) {
                        Toast.makeText(getActivity(), simplePojo.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), simplePojo.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                error.printStackTrace();
                String volleyError = MyVolley.handleVolleyError(error);
                if (volleyError != null) {
                    Util.showRedSnackbar(linearLayout, volleyError);
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
            uploadPhotoInBackground(status,imageBase64);
        }else {
            Toast.makeText(getActivity(),R.string.no_internet_connection,Toast.LENGTH_LONG).show();
        }
    }

    /*
    * Method to upload photo to the server
    * */
    private void uploadPhotoInBackground(String status, String imageBase64) {

    }
}
