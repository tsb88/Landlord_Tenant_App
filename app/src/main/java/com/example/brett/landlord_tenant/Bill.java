package com.example.brett.landlord_tenant;

import java.util.Date;
import java.util.UUID;

/**
 * Created by kristenwong on 12/11/17.
 */

public class Bill {
    private String mTenant, mLandlord, mMessage, mUUID;
    private float mAmount;
    private Date mDueDate;
    private boolean mIsPaid;

    Bill(String tenant, String landlord, float amount, Date dueDate, String message) {
        mTenant = tenant;
        mLandlord = landlord;
        mAmount = amount;
        mDueDate = dueDate;
        mMessage = message;
        mIsPaid = false;
        mUUID = UUID.randomUUID().toString();
    }

    Bill() {}

    public String getmTenant() {
        return mTenant;
    }

    public void setmTenant(String mTenant) {
        this.mTenant = mTenant;
    }

    public String getmLandlord() {
        return mLandlord;
    }

    public void setmLandlord(String mLandlord) {
        this.mLandlord = mLandlord;
    }

    public float getmAmount() {
        return mAmount;
    }

    public void setmAmount(float mAmount) {
        this.mAmount = mAmount;
    }

    public Date getmDueDate() {
        return mDueDate;
    }

    public void setmDueDate(Date mDueDate) {
        this.mDueDate = mDueDate;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public boolean ismIsPaid() {
        return mIsPaid;
    }

    public void setmIsPaid(boolean mIsPaid) {
        this.mIsPaid = mIsPaid;
    }

    public String getmUUID() {
        return mUUID;
    }

    public void setmUUID(String mUUID) {
        this.mUUID = mUUID;
    }
}
