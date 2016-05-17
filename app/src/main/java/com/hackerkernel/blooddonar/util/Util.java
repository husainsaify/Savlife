package com.hackerkernel.blooddonar.util;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;

import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.infrastructure.MyApplication;

/**
 * Utility methods
 */
public class Util {
    public static boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() &&
                networkInfo.isConnected();
    }

    public static void showRedSnackbar(View layoutForSnacbar,String message){
        Snackbar snack = Snackbar.make(layoutForSnacbar,message,Snackbar.LENGTH_LONG);
        ViewGroup group = (ViewGroup) snack.getView();
        group.setBackgroundColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.colorPrimary));
        snack.show();
    }

    public static void showParsingErrorAlert(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.oops))
                .setMessage(context.getString(R.string.dont_worry_engineers_r_working)  )
                .setNegativeButton(context.getString(R.string.report_issue), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO:: take user to report issue area
                    }
                })
                .setPositiveButton(context.getString(R.string.try_again), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
