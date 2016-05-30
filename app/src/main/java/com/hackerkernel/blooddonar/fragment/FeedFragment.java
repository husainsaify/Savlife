package com.hackerkernel.blooddonar.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.constant.EndPoints;
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.util.Util;

import java.io.File;
import java.net.URL;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FeedFragment extends Fragment {
    private RequestQueue mRequestQueue;
    private ProgressBar pb;
    private static final int SELECT_IMAGE_CODE = 100;

    @Bind(R.id.post_status_btn) Button mPostStatusBtn;
    @Bind(R.id.post_photo_btn) Button mPostPhotoBtn;

    public FeedFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = MyVolley.getInstance().getRequestQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_feed, container, false);

        ButterKnife.bind(this, view);

        mPostStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStatusAlertDialog();
            }
        });

        mPostPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open gallery
                Intent openGallery = new Intent(Intent.ACTION_PICK);
                openGallery.setType("image/*");
                startActivityForResult(openGallery,SELECT_IMAGE_CODE);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }


    private void openStatusAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View status = LayoutInflater.from(getActivity())
                .inflate(R.layout.alert_status_dialoge, null);
        final EditText statusEditText = (EditText) status.findViewById(R.id.edit_status);

        builder.setView(status)
                .setPositiveButton(R.string.post, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //get status from edit text
                String status = statusEditText.getText().toString();

                //call method to upload status to Database
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
        if (Util.isNetworkAvailable()){
            uploadStatusInBackground(status);
        }else {
            Toast.makeText(getActivity(),R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * Method to upload status in background
    * */
    private void uploadStatusInBackground(String status) {
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.POST_STATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };

    }

    /*
    * This method will run when someone will select and image
    * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null){

            //get uri of the selected image
            Uri image = data.getData();

            openPhotoAlertDialog(image);
        }
    }

    private void openPhotoAlertDialog(Uri imageUri){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.alert_image_dialoge,null);
        //find views
        ImageView image = (ImageView) view.findViewById(R.id.image_dialog_imageview);
        EditText editText = (EditText) view.findViewById(R.id.image_dialog_status);
        //set selected image to imageview
        /*Glide.with(getActivity())
                .load(new File(imageUri.getPath()))
                .into(image);*/
        image.setImageURI(imageUri);

        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();


    }

}
