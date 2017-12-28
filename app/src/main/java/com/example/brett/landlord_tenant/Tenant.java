package com.example.brett.landlord_tenant;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kristenwong on 12/4/17.
 */

public class Tenant {
    private String mFirstName;
    private String mLastName;
    private String mUserName;
    private String mLandlordUserName;
    private String mPassword;
    private String mEmail;
    private int mPhoneNumber;
    private boolean contact_visibility;

    private static final String LANDLORD_TENANT_DEBUG_TAG = "LandlordTenantApp";
/*
    * * Make sure to call updateDatabase() on all Landlord objects after changes are made to ensure
    * that the changes are reflected in the database.
    *
    * - we will probably need to add additional properties as needed later
 */

    Tenant(String first, String last, String userName, String password) {
        mFirstName = first;
        mLastName = last;
        mUserName = userName;
        mPassword = password;
        mEmail= "";
        mPhoneNumber = 0;
    }

    Tenant() {
        mFirstName = "";
        mLastName = "";
        mUserName = "";
        mLandlordUserName = "";
        mPassword = "";
        mEmail = "";
        mPhoneNumber = 0;
        contact_visibility = false;
    }

    public String getmFirstName() {
        return mFirstName;
    }

    public void setmFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getmLastName() {
        return mLastName;
    }

    public void setmLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getmLandlordUserName() {
        return mLandlordUserName;
    }

    public boolean getmVisibility(){return contact_visibility;}

    public void setmLandlordUserName(String mLandlordUserName) {
        Log.d("LandlordTenantApp", "setLandlord called");
        DatabaseReference landlordRef = FirebaseDatabase.getInstance().getReference().child("users").child("landlords");
        landlordRef.child(mLandlordUserName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Landlord landlord = dataSnapshot.getValue(Landlord.class);
                Log.d("LandlordTenantApp", "landlord retrieved from setLandlord: " + landlord.getmFirstName() + landlord.getmLastName());
                if(landlord != null) {
                    HashMap<String, String> tenants =  landlord.getmTenants();
                    tenants.put(mUserName, mUserName);
                    landlord.setmTenants(tenants);
                    landlord.updateDatabase();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        this.mLandlordUserName = mLandlordUserName;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public int getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmPhoneNumber(int mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public void setmVisibility(boolean b){this.contact_visibility = b;}

    public void updateDatabase() {
        Log.d(LANDLORD_TENANT_DEBUG_TAG, "updating tenant in database: " + this.mFirstName + " " + this.mLastName + " " + this.mUserName);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child("tenants");
        ref.child(mUserName).setValue(this);
    }


}
