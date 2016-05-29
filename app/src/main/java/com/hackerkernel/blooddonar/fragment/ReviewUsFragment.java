package com.hackerkernel.blooddonar.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hackerkernel.blooddonar.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReviewUsFragment extends Fragment {
    private String statusText;
    private EditText statusEditText;
    @Bind(R.id.post_status_btn) Button statusButton;


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
                openAlertDialoge();
                statusEditText.setText("");
            }
        });


        // Inflate the layout for this fragment

        return view;
    }


    private void openAlertDialoge(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Post Status");
        View status = LayoutInflater.from(getActivity())
                .inflate(R.layout.alert_dialoge_layout, null);
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
    }

}
