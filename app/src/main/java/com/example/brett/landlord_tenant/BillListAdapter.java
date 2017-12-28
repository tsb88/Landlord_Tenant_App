package com.example.brett.landlord_tenant;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by kristenwong on 12/12/17.
 */

public class BillListAdapter extends BaseAdapter {
    private ArrayList<Bill> mBillList;
    private LayoutInflater mInflater;
    private Context mContext;
    private boolean mIsLandlord;
    private ArrayList<Boolean> misItemChecked;

    BillListAdapter(ArrayList<Bill> billList, Context context) {
        mBillList = billList;
        mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        misItemChecked = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mBillList.size();
    }

    @Override
    public Object getItem(int i) {
        return mBillList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        TextView billAmount, billReceiver, message, dueDate, isPaid;
        misItemChecked.add(false);

        View v = mInflater.inflate(R.layout.landlord_bill_list_item, viewGroup, false);

        billAmount = (TextView) v.findViewById(R.id.text_bill_amount_landlord);
        billReceiver = (TextView) v.findViewById(R.id.text_bill_receiver);
        message = (TextView) v.findViewById(R.id.text_bill_message_landlord);
        dueDate = (TextView) v.findViewById(R.id.text_bill_due_date_landlord);
        isPaid = (TextView) v.findViewById(R.id.text_bill_paid_landlord);

        Bill bill = mBillList.get(i);

        DecimalFormat format = new DecimalFormat();
        format.setMinimumFractionDigits(2);
        String billAmountString = "$" + format.format(bill.getmAmount());
        billAmount.setText(billAmountString);

        if(ismIsLandlord()) billReceiver.setText(bill.getmTenant());
        else billReceiver.setText(bill.getmLandlord());

        message.setText(bill.getmMessage());

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy");
        String dueDateString = "DUE: " + dateFormat.format(bill.getmDueDate());
        dueDate.setText(dueDateString);

        if(bill.ismIsPaid()) {
            isPaid.setText(R.string.paid);
            isPaid.setTextColor(ContextCompat.getColor(mContext, R.color.colorPaleGreen));
            billAmount.setTextColor(ContextCompat.getColor(mContext, R.color.colorPaleGreen));
        } else {
            isPaid.setText(R.string.unpaid);
            isPaid.setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
            billAmount.setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
        }

        return v;
    }

    public void updateList(ArrayList<Bill> billList) {
        mBillList = billList;
        notifyDataSetChanged();
    }

    public boolean ismIsLandlord() {
        return mIsLandlord;
    }

    public void setmIsLandlord(boolean mIsLandlord) {
        this.mIsLandlord = mIsLandlord;
    }
}
