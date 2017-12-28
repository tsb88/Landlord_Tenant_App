package com.example.brett.landlord_tenant;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by kristenwong on 12/17/17.
 */

public class TenantUtilitiesActivity extends AppCompatActivity {
	private Spinner mChooseTenantSpinner, mChooseUtilityTypeSpinner;
	private EditText mInputBillAmount, mInputMessage;
	private Button mButtonChooseDate, mButtonSubmitBill;
	private ListView mListViewBillHistory;
	private Date mDate;
	private String mTenantUserName, mSelectedTenant, mBillMessage, mSelectedUtilityType, mLandlordUserName;
	private float mBillAmount;
	private ArrayList<String> mTenantList;
	private ArrayAdapter<String> mTenantListSpinnerAdapter;
	private BillListAdapter mBillListAdapter;
	private ArrayList<Bill> mBillList;

	private static final String KEY_TENANT_USERNAME = "tenantusername";
	private static final String LANDLORD_TENANT_DEBUG_TAG = "LandlordTenantApp";
	private static final String DATE_DIALOG_TAG = "dateDialog";
	private static final String LANDLORD_USERNAME_QUERY = "mLandlordUserName";
	private static final String UTILITIES_QUERY = "mUtilityBills";
	private static final String BILLS_QUERY = "mBills";
	private static final String LANDLORDS_QUERY = "landlords";

	private static final DatabaseReference REF = FirebaseDatabase.getInstance().getReference();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tenant_utilities);
		getSupportActionBar().setTitle("Utilities");

		mTenantList = new ArrayList<>();
		mBillList = new ArrayList<>();

		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			mTenantUserName = extras.getString(KEY_TENANT_USERNAME);
			Log.d(LANDLORD_TENANT_DEBUG_TAG, "TenantUtilitiesActivity: onCreate called with tenant username = " + mTenantUserName);
		}

		mButtonChooseDate = (Button) findViewById(R.id.button_choose_utility_due_date);
		mButtonChooseDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				FragmentManager fragmentManager = getFragmentManager();
				UtilityDataSelectorFragment dialog = new UtilityDataSelectorFragment();
				dialog.show(fragmentManager, DATE_DIALOG_TAG);
			}
		});

		mInputBillAmount = (EditText) findViewById(R.id.input_utility_bill_amount);
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

		mInputMessage = (EditText) findViewById(R.id.input_utility_message);
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

		mChooseTenantSpinner = (Spinner) findViewById(R.id.spinner_tenant_utilities);
		mTenantListSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mTenantList);
		mTenantListSpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
		mChooseTenantSpinner.setAdapter(mTenantListSpinnerAdapter);
		mChooseTenantSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				mSelectedTenant = adapterView.getItemAtPosition(i).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});

		mChooseUtilityTypeSpinner = (Spinner) findViewById(R.id.spinner_tenant_utility_type);
		ArrayAdapter<CharSequence> utilityAdapter = ArrayAdapter.createFromResource(this, R.array.utility_types, android.R.layout.simple_spinner_item);
		utilityAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
		mChooseUtilityTypeSpinner.setAdapter(utilityAdapter);
		mChooseUtilityTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				mSelectedUtilityType = adapterView.getItemAtPosition(i).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});

		mButtonSubmitBill = (Button) findViewById(R.id.button_send_utility_bill);
		mButtonSubmitBill.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String billMessage = mSelectedUtilityType + " - " + mBillMessage;
				Bill bill = new Bill(mSelectedTenant, mTenantUserName, mBillAmount, mDate, billMessage);
				REF
					.child(UTILITIES_QUERY)
					.child(mTenantUserName)
					.child(bill.getmUUID().toString())
					.setValue(bill);

				REF
					.child(BILLS_QUERY)
					.child(mSelectedTenant)
					.child(bill.getmUUID().toString())
					.setValue(bill);
				mInputBillAmount.setText("");
				mInputMessage.setText("");
				mButtonChooseDate.setText(R.string.choose_due_date);
			}
		});

		mListViewBillHistory = (ListView) findViewById(R.id.listivew_tenant_utility_bill_history);
		mListViewBillHistory.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		mBillListAdapter = new BillListAdapter(mBillList, getApplicationContext());
		mBillListAdapter.setmIsLandlord(true);
		mListViewBillHistory.setAdapter(mBillListAdapter);

		mListViewBillHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
				Bill bill = mBillList.get(i);

				if(!bill.ismIsPaid()) {
					REF
						.child(UTILITIES_QUERY)
						.child(mTenantUserName)
						.child(bill.getmUUID().toString())
						.child("mIsPaid")
						.setValue(true);

					REF
						.child(BILLS_QUERY)
						.child(mSelectedTenant)
						.child(bill.getmUUID().toString())
						.child("mIsPaid")
						.setValue(true);
				} else {
					REF
						.child(UTILITIES_QUERY)
						.child(mTenantUserName)
						.child(bill.getmUUID().toString())
						.child("mIsPaid")
						.setValue(false);

					REF
						.child(BILLS_QUERY)
						.child(mSelectedTenant)
						.child(bill.getmUUID().toString())
						.child("mIsPaid")
						.setValue(false);
				}
				return true;
			}
		});

		getLandlord();
		getBillsList();
	}

	private void getLandlord() {
		REF
			.child("users")
			.child("tenants")
			.child(mTenantUserName)
			.child(LANDLORD_USERNAME_QUERY)
			.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					mLandlordUserName = (String) dataSnapshot.getValue();
					getTenantList();

					Log.d(LANDLORD_TENANT_DEBUG_TAG, "TenantUtilitiesActivity: landlord retrieved = " + mLandlordUserName);
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			});
	}

	private void getTenantList() {
		REF
			.child("users")
			.child(LANDLORDS_QUERY)
			.child(mLandlordUserName)
			.child("mTenants")
			.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					mTenantList.clear();

					for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
						if(!snapshot.getValue().equals("0")) {
							mTenantList.add((String) snapshot.getValue());
						}
					}
					mTenantListSpinnerAdapter.notifyDataSetChanged();
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			});
	}

	private void getBillsList() {
		REF
			.child(UTILITIES_QUERY)
			.child(mTenantUserName)
			.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					mBillList.clear();

					for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
						Bill bill = snapshot.getValue(Bill.class);
						mBillList.add(bill);
					}
					mBillListAdapter.updateList(mBillList);
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			});
	}

	public void onDateSelected(Date date) {
		mDate = date;
		SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd yyyy");
		mButtonChooseDate.setText(format.format(mDate));
	}
}
