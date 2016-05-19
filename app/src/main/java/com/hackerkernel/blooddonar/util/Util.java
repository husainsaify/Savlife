package com.hackerkernel.blooddonar.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hackerkernel.blooddonar.R;
import com.hackerkernel.blooddonar.activity.HomeActivity;
import com.hackerkernel.blooddonar.activity.NoInternetActivity;
import com.hackerkernel.blooddonar.activity.OtpVerificationActivity;
import com.hackerkernel.blooddonar.constant.Constants;
import com.hackerkernel.blooddonar.infrastructure.BaseActivity;
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

    /*
    * Method to send user to OTP verification activity
    * */
    public static void goToOtpVerificationActivity(Context activity,String mobileNumber){
        Intent intent = new Intent(activity, OtpVerificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.COM_MOBILE,mobileNumber);
        activity.startActivity(intent);
    }

    /*
    * Method to send user to NO Internet Activity
    * */
    public static void goToNoInternetActivity(Context activity){
        Intent intent = new Intent(activity, NoInternetActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void goToHomeActivity(Activity activity) {
        Intent intent = new Intent(activity, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void dialNumber(Activity activity, String phonenumber){
        Uri callUri = Uri.parse("tel://"+phonenumber);
        Intent callIntent = new Intent(Intent.ACTION_CALL,callUri);
        try{
            activity.startActivity(callIntent);
        }catch (SecurityException e){
            Toast.makeText(activity,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }
}
