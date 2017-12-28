package com.example.brett.landlord_tenant;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by kristenwong on 12/11/17.
 */

public class DateSelectorFragment extends DialogFragment {
    private DatePicker mDatePicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date_selector, null);

        mDatePicker = (DatePicker) view.findViewById(R.id.date_picker);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.choose_due_date)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year, month, day).getTime();
                        LandlordRentActivity activity = (LandlordRentActivity) getActivity();

                        activity.onDateSelected(date);
                    }
                })
                .create();
    }
}
