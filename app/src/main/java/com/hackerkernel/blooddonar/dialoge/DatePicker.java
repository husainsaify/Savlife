package com.hackerkernel.blooddonar.dialoge;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by Murtaza on 5/31/2016.
 */
public class DatePicker extends DialogFragment  {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        DateSetting dateSetting = new DateSetting(getActivity());
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(),dateSetting,year,month,day);
        return dialog;
    }
}
