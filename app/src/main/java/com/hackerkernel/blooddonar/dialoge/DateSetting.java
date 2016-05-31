package com.hackerkernel.blooddonar.dialoge;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.*;
import android.widget.DatePicker;

/**
 * Created by Murtaza on 5/31/2016.
 */
public class DateSetting implements DatePickerDialog.OnDateSetListener {
    Context context;
    public DateSetting(Context context){
        this.context = context;
    }
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Toast.makeText(context,"Selected date is "+year+"/"+monthOfYear+"/"+dayOfMonth,Toast.LENGTH_LONG).show();
    }
}
