package com.hackerkernel.blooddonar.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.constant.EndPoints;
import com.hackerkernel.blooddonar.network.MyVolley;
import com.hackerkernel.blooddonar.util.Util;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FeedFragment extends Fragment {
    private RequestQueue mRequestQueue;
    private ProgressBar pb;

    @Bind(R.id.post_status_btn) Button statusButton;
    @Bind(R.id.post_photo) Button postPhotoButton;

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
        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStatusAlertDialog();
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
        postPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhotoAlertDialog();
            }
        });
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

    private void openPhotoAlertDialog(){
        /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Post Picture");
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.alert_image_dialoge,null);
        captionEditText = (EditText) view.findViewById(R.id.edit_caption);
        addPhotoBtn = (Button) view.findViewById(R.id.select_photo_btn);
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image*//*");
                startActivityForResult(i,100);
            }
        });
        caption = captionEditText.getText().toString();
        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();*/


    }

}
