package com.hackerkernel.blooddonar.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.Toast;

import com.hackerkernel.blooddonar.R;

import java.util.zip.Inflater;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReviewUsFragment extends Fragment {
    private String statusText, caption;
    private EditText statusEditText, captionEditText;
    private Button addPhotoBtn;
    @Bind(R.id.post_status_btn) Button statusButton;
    @Bind(R.id.post_photo)Button postPhotoButton;


    public ReviewUsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_review_us, container, false);

        ButterKnife.bind(this, view);
        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStatusAlertDialoge();
                statusEditText.setText("");
            }
        });


        // Inflate the layout for this fragment

        return view;
    }


    private void openStatusAlertDialoge(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Post Status");
        View status = LayoutInflater.from(getActivity())
                .inflate(R.layout.alert_status_dialoge, null);
        statusEditText = (EditText) status.findViewById(R.id.edit_status);

        builder.setView(status);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                statusText = statusEditText.getText().toString();
                Toast.makeText(getActivity(), "status Is:" + statusText, Toast.LENGTH_LONG).show();
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

    private void openPhotoAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Post Picture");
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.alert_image_dialoge,null);
        captionEditText = (EditText) view.findViewById(R.id.edit_caption);
        addPhotoBtn = (Button) view.findViewById(R.id.select_photo_btn);
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
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
        dialog.show();


    }

}
