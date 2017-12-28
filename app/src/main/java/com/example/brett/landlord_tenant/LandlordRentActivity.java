package com.example.brett.landlord_tenant;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LandlordRentActivity extends AppCompatActivity{
    private Button mChooseDueDateButton, mSubmitBillButton;
    private Date mDate;
    private EditText mInputMessage, mInputBillAmount;
    private Spinner mSpinner;
    private ListView mListView;
    private String mLandlordUserName, mSelectedTenant, mBillMessage;
    private float mBillAmount;
    private ArrayList<String> mTenantList;
    private ArrayAdapter<String> mSpinnerAdapter;
    private BillListAdapter mBillListAdapter;
    private ArrayList<Bill> mBillsList;

    private static final String DATE_DIALOG_TAG = "dateDialog";
    private static final String KEY_LANDLORD_USERNAME = "landlordUsername";
    private static final String LANDLORD_TENANT_DEBUG_TAG = "LandlordTenantApp";
    private static final DatabaseReference REF = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_rent);
        getSupportActionBar().setTitle("Rent");

        mTenantList = new ArrayList<>();
        mBillsList = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mLandlordUserName = extras.getString(KEY_LANDLORD_USERNAME);
            Log.d(LANDLORD_TENANT_DEBUG_TAG, "LandlordRentActivity: landlord username extra = " + mLandlordUserName);
        }

        mChooseDueDateButton = (Button) findViewById(R.id.button_choose_due_date);
        mChooseDueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                DateSelectorFragment dialog = new DateSelectorFragment();

                dialog.show(fragmentManager, DATE_DIALOG_TAG);
            }
        });

        mInputBillAmount = (EditText) findViewById(R.id.input_bill_amount);
        mInputBillAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().equals("")) {
                    mBillAmount = Float.parseFloat(charSequence.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mInputMessage = (EditText) findViewById(R.id.input_bill_message);
        mInputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mBillMessage = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mSpinner = (Spinner) findViewById(R.id.spinner_landlord_billpay);
        mSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mTenantList);
        mSpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedTenant = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        getTenantList();

        mSubmitBillButton = (Button) findViewById(R.id.button_send_bill);
        mSubmitBillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bill bill = new Bill(mSelectedTenant, mLandlordUserName, mBillAmount, mDate, mBillMessage);
                REF
                        .child("mBills")
                        .child(mLandlordUserName)
                        .child(bill.getmUUID().toString())
                        .setValue(bill);

                REF
                        .child("mBills")
                        .child(mSelectedTenant)
                        .child(bill.getmUUID().toString())
                        .setValue(bill);

                mInputBillAmount.setText("");
                mInputMessage.setText("");
                mChooseDueDateButton.setText(R.string.choose_due_date);
            }
        });

        mListView = (ListView) findViewById(R.id.listview_landlord_bill_history);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        mBillListAdapter = new BillListAdapter(mBillsList, getApplicationContext());
        mBillListAdapter.setmIsLandlord(true);
        mListView.setAdapter(mBillListAdapter);
        getBillsList();

       mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
               Log.d(LANDLORD_TENANT_DEBUG_TAG, "LandlordRentActivity: item long click on bill " + i);

               Bill bill = mBillsList.get(i);
               if(!bill.ismIsPaid()) {
                   REF
                           .child("mBills")
                           .child(mLandlordUserName)
                           .child(bill.getmUUID())
                           .child("mIsPaid")
                           .setValue(true);

                   REF
                           .child("mBils")
                           .child(bill.getmTenant())
                           .child(bill.getmUUID())
                           .child("mIsPaid")
                           .setValue(true);
               } else {
                   REF
                           .child("mBills")
                           .child(mLandlordUserName)
                           .child(bill.getmUUID())
                           .child("mIsPaid")
                           .setValue(false);

                   REF
                           .child("mBills")
                           .child(bill.getmTenant())
                           .child(bill.getmUUID())
                           .child("mIsPaid")
                           .setValue(false);
               }

               return true;
           }
       });
    }

    private void getTenantList() {
        REF
                .child("users")
                .child("landlords")
                .child(mLandlordUserName)
                .child("mTenants")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(LANDLORD_TENANT_DEBUG_TAG, "LandlordRentActivity: getTenantList->onDataChanged called");
                mTenantList.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Log.d(LANDLORD_TENANT_DEBUG_TAG, "LandlordRentActivity: dataSnapshot value = " + (String) snapshot.getValue());

                    if(!snapshot.getValue().equals("0")) {
                        mTenantList.add((String) snapshot.getValue());
                        Log.d(LANDLORD_TENANT_DEBUG_TAG, "LandlordRentActivity: onDataChanged, data added to list = " + snapshot.getValue());
                    }
                }
                mSpinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getBillsList() {
        REF
                .child("mBills")
                .child(mLandlordUserName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mBillsList.clear();

                        for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            Bill bill = snapshot.getValue(Bill.class);
                            mBillsList.add(bill);
                        }
                        mBillListAdapter.updateList(mBillsList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void onDateSelected(Date date) {
        mDate = date;
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd yyyy");
        mChooseDueDateButton.setText(format.format(mDate));
    }
}
