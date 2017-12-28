package com.example.brett.landlord_tenant;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by kristenwong on 12/4/17.
 */

public class Landlord {
    private String mFirstName;
    private String mLastName;
    private String mUsername;
    private String mPassword;
    private String mEmail;
    private int mPhoneNumber;
    private HashMap<String, String> mTenants;

    private static final String LANDLORD_TENANT_DEBUG_TAG = "LandlordTenantApp";

/*
    * * Make sure to call updateDatabase() on all Landlord objects after changes are made to ensure
    * that the changes are reflected in the database.
    *
    * - we will probably need to add additional properties as needed later
 */

    Landlord(String first, String last, String userName, String password) {
        Log.d(LANDLORD_TENANT_DEBUG_TAG, "Landlord constructor called: " + first + " " + last + " " + userName);

        mFirstName = first;
        mLastName = last;
        mUsername = userName;
        mPassword = password;
        mTenants = new HashMap<>();
        mTenants.put("-0-", "0");
        mEmail = "";
        mPhoneNumber = 0;
    }

    Landlord() {
        mFirstName = "";
        mLastName = "";
        mUsername = "";
        mPassword = "";
        mTenants = new HashMap<>();
        mTenants.put("-0-", "0");
        mEmail = "";
        mPhoneNumber = 0;
    }

    public String getmFirstName() {
        return mFirstName;
    }

    public void setmFirstName(String mFirstName) {
        Log.d(LANDLORD_TENANT_DEBUG_TAG, "Landlord: setFirstName called");
        this.mFirstName = mFirstName;
    }

    public String getmLastName() {
        return mLastName;
    }

    public void setmLastName(String mLastName) {
        Log.d(LANDLORD_TENANT_DEBUG_TAG, "Landlord: setLastName called");
        this.mLastName = mLastName;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        Log.d(LANDLORD_TENANT_DEBUG_TAG, "Landlord: setUserName called");
        this.mUsername = mUsername;
    }

    public HashMap<String,String> getmTenants() {
        return mTenants;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public void setmTenants(HashMap<String, String> mTenants) {
        Log.d(LANDLORD_TENANT_DEBUG_TAG, "Landlord: setTenants called");
        this.mTenants = mTenants;
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

    public void updateDatabase() {
        Log.d(LANDLORD_TENANT_DEBUG_TAG, "updating landlord in database: " + this.getmFirstName() + this.getmLastName() + " " + this.getmUsername());
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child("landlords").child(mUsername).setValue(this);
    }

}
