package com.example.brett.landlord_tenant;

import android.content.Context;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Created by kristenwong on 12/6/17.
 */

// Class to handle firebase async calls

public interface OnGetDataListener {
    public void onStart();
    public void onSuccess(DataSnapshot snapshot);
    public void onFailure(DatabaseError error);
}
