package com.example.brett.landlord_tenant;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class contactActivity extends AppCompatActivity {

    String name = "";
    String identifier = "";
    String username = "";
    String landlordName ="";
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference myRef = db.getReference();
    List<Tenant> tenants = new ArrayList<Tenant>();
    List<Landlord> landlord = new ArrayList<Landlord>();
    boolean visibility;

    Landlord myLandlord = new Landlord();
    Tenant t = new Tenant();

    List<String> myTenantList = new ArrayList<>();
    List<Tenant> myTenants = new ArrayList<>();

    ToggleButton button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!= null){
            name = extras.getString("name");
            identifier = extras.getString("identifier");
            username = extras.getString("username");
            landlordName = extras.getString("landlordUsername");
        }

        if(identifier.equals("landlord")){
            setContentView(R.layout.activity_contact_landlord);
        }
        else if (identifier.equals("tenant")){
            setContentView(R.layout.activity_contact);
            getVisibility();
            button = (ToggleButton) findViewById(R.id.toggleButton);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    t.setmVisibility(button.isChecked());
                    myRef.child("users").child("tenants").child(username).setValue(t);
                }
            });
        }
        else{
            setContentView(R.layout.activity_contact);
        }

        checkDatabaseReference();
    }

    public void checkDatabaseReference(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("users")){
                    if(dataSnapshot.child("users").hasChild("tenants")){
                        getTenantList();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("users")){
                    if(dataSnapshot.child("users").hasChild("landlords")){
                        getLandlordList();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        getmyTenants();
    }

    public void getLandlordList(){
        myRef.child("users").child("landlords").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    landlord.add(postSnapshot.getValue(Landlord.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(identifier.equals("tenant")){
            getmyLandlord();
        }
    }

    private void getmyLandlord() {
        myRef.child("users")
                .child("landlords").child(landlordName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myLandlord = dataSnapshot.getValue(Landlord.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Runnable r = new Runnable() {
            @Override
            public void run() {
                populateTexts();
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 50);
    }

    private void populateTexts() {
        TextView text1 = (TextView)findViewById(R.id.landlord_name);
        TextView text2 = (TextView)findViewById(R.id.landlord_info);
        String t1 = myLandlord.getmFirstName() + " " + myLandlord.getmLastName();
        String t2 = "Phone: " + myLandlord.getmPhoneNumber() + ", Email: " + myLandlord.getmEmail() ;
        text1.setText(t1);
        text2.setText(t2);
    }

    private void getVisibility(){
        myRef.child("users").child("tenants").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                t = dataSnapshot.getValue(Tenant.class);
                visibility = t.getmVisibility();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Runnable r = new Runnable() {
            @Override
            public void run() {
                ToggleButton button = (ToggleButton) findViewById(R.id.toggleButton);
                button.setChecked(visibility);
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 50);
    }


    private void getmyTenants() {
        if(identifier.equals("landlord")){
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
            h.postDelayed(r, 50);
        }
        else if(identifier.equals("tenant")){
            myRef.child("users")
                    .child("landlords").child(landlordName)
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
            h.postDelayed(r, 50);
        }

    }

    public void checkmyTenants(){
        if(identifier.equals("landlord")){
            for(int i =0; i<myTenantList.size();i++){
                for(int x=0; x<tenants.size();x++){
                    if(myTenantList.get(i).equals(tenants.get(x).getmUserName())){
                        myTenants.add(tenants.get(x));
                    }
                }
            }
        }
        else if(identifier.equals("tenant")){
            for(int i =0; i<myTenantList.size();i++){
                for(int x=0; x<tenants.size();x++){
                    if(myTenantList.get(i).equals(tenants.get(x).getmUserName()) && !myTenantList.get(i).equals(username)){
                        if(tenants.get(x).getmVisibility()){
                            myTenants.add(tenants.get(x));
                        }

                    }
                }
            }
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {
                populateList();
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 50);
    }

    public void populateList(){
            ListView listView = (ListView) findViewById(R.id.contact_list);
            ArrayAdapter<Tenant> mAdapter = new ArrayAdapter<Tenant>(this, android.R.layout.simple_list_item_2, myTenants){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){
                    Tenant tenant = getItem(position);
                    String _name = tenant.getmFirstName() + " " + tenant.getmLastName();
                    String _info = "Phone: " + tenant.getmPhoneNumber() + ", Email: " + tenant.getmEmail();
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
                    }
                    ((TextView)convertView.findViewById(android.R.id.text1)).setText(_name);
                    ((TextView)convertView.findViewById(android.R.id.text2)).setText(_info);
                    return convertView;
                }
            };
            listView.setAdapter(mAdapter);
    }
}
