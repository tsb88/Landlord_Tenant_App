package com.example.brett.landlord_tenant;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class leaseActivity extends AppCompatActivity {

    CheckBox checkBox;
    CheckBox checkBox2;
    Bitmap bitmap;
    Button clear,save;
    SignatureView signatureView;
    String name = "";
    String identifier = "";
    String username = "";
    String landlordUserName ="";
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference myRef = db.getReference();


    List<String> myTenantList = new ArrayList<>();
    List<Maintenance> completedTenants = new ArrayList<>();
    List<Tenant> tenants = new ArrayList<>();
    List<Tenant> myTenants = new ArrayList<>();

    private static final String LANDLORD_TENANT_DEBUG_TAG = "LandlordTenantApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!= null){
            name = extras.getString("name");
            identifier = extras.getString("identifier");
            username = extras.getString("username");
            landlordUserName = extras.getString("landlordUsername");
        }

        if(identifier.equals("landlord")){
            setContentView(R.layout.activity_lease_landlord);
            clear = (Button) findViewById(R.id.lease_landlord_button);
            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myRef.child("lease").child(username).removeValue();
                }
            });

            getTenantList();
            getmyTenants();
            final ListView list = (ListView) findViewById(R.id.maintenance_list);

        }
        else{
            setContentView(R.layout.activity_lease);
            checkBox = (CheckBox) findViewById(R.id.lease_checkbox);
            checkBox2 = (CheckBox) findViewById(R.id.lease_checkbox_read);

            save = (Button) findViewById(R.id.lease_button_save);
            signatureView = findViewById(R.id.signature_view);
            clear = findViewById(R.id.lease_button_clear);

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkBox.isChecked()){
                        if(checkBox2.isChecked()){
                            bitmap = signatureView.getSignatureBitmap();
                            saveImage(bitmap);
                        }else{
                            Toast.makeText(leaseActivity.this, "You have not checked that you received a copy of the lease", Toast.LENGTH_SHORT).show();
                        }
                    }//if
                    else{
                        Toast.makeText(leaseActivity.this, "You have not checked that you read and understand the lease", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signatureView.clearCanvas();
                }
            });
        }
    }
    public void saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        //Toast.makeText(this, encodedImage, Toast.LENGTH_LONG).show();
        myRef.child("lease").child(landlordUserName).push().setValue(new Maintenance(name, encodedImage, ""));
    }

    public void getTenantList(){
        myRef.child("users").child("tenants").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    tenants.add(postSnapshot.getValue(Tenant.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getmyTenants() {
        myRef.child("users")
          .child("landlords").child(username)
          .child("mTenants").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    myTenantList.add(postSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Runnable r = new Runnable() {
            @Override
            public void run() {
                checkmyTenants();
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 150);
    }

    public void checkmyTenants() {

        for(int i =0; i<myTenantList.size();i++){
            for(int x=0; x<tenants.size();x++){
                if(myTenantList.get(i).equals(tenants.get(x).getmUserName())){
                    myTenants.add(tenants.get(x));
                }
            }
        }

        DatabaseReference dbref = myRef.child("lease").child(username);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    completedTenants.add(postSnapshot.getValue(Maintenance.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Runnable r = new Runnable() {
            @Override
            public void run() {
                populateList();
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 150);
    }

    public boolean findCompletedTenants(String _name){

        for(int i=0;i<myTenants.size();i++){
            for(int x=0; x<completedTenants.size();x++){
                if(_name.equals(completedTenants.get(x).getName())){
                    return true;
                }
            }
        }
        return false;
    }


    public void populateList(){
        ListView listView = (ListView) findViewById(R.id.lease_landlord_list);
        ArrayAdapter<Tenant> mAdapter = new ArrayAdapter<Tenant>(this, android.R.layout.simple_list_item_checked, myTenants){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                Tenant t = getItem(position);
                String _name = t.getmFirstName() + " " + t.getmLastName();
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_checked, parent, false);
                }
                ((CheckedTextView)convertView.findViewById(android.R.id.text1)).setText(_name);
                if(findCompletedTenants(_name)){
                    ((CheckedTextView)convertView.findViewById(android.R.id.text1)).setChecked(true);
                }
                else{
                    ((CheckedTextView)convertView.findViewById(android.R.id.text1)).setChecked(false);
                }
                return convertView;
            }
        };
        listView.setAdapter(mAdapter);
    }
}
