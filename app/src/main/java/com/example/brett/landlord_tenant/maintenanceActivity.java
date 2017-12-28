package com.example.brett.landlord_tenant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class maintenanceActivity extends AppCompatActivity {

    private SharedPreferences mPrefs;
    String title;
    String description;

    EditText maintenance_title;
    EditText maintenance_description;

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference myRef = db.getReference().child("maintenance");
    List<Maintenance> maintenances = new ArrayList<>();


    String name ="";
    String identifier = "";
    String username = "";
    String landlordUsername = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!= null){
            name = extras.getString("name");
            identifier = extras.getString("identifier");
            username = extras.getString("username");
            landlordUsername = extras.getString("landlordUsername");
        }

        if(identifier.equals("landlord")){
            setContentView(R.layout.activity_maintenance_landlord);
            myRef = myRef.child(username);
        }
        else if (identifier.equals("tenant")){
            setContentView(R.layout.activity_maintenance);
            myRef = myRef.child(landlordUsername);
            Button button = (Button) findViewById(R.id.maint_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(maintenanceActivity.this);

                    builder.setTitle("You are sure you want to submit?");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            myRef.push().setValue(new Maintenance(name, maintenance_title.getText().toString(), maintenance_description.getText().toString()) );
                            maintenance_title.setText("");
                            maintenance_description.setText("");
                            Toast.makeText(maintenanceActivity.this, "Request submitted", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
            maintenance_title = (EditText) findViewById(R.id.maintenance_title);
            maintenance_description = (EditText) findViewById(R.id.maintenance_description);

            mPrefs = getSharedPreferences("key", Context.MODE_PRIVATE);
            title = mPrefs.getString("title", maintenance_title.getText().toString());
            description = mPrefs.getString("description", maintenance_description.getText().toString());

            maintenance_title.setText(title);
            maintenance_description.setText(description);
        }
        else{
            setContentView(R.layout.activity_maintenance);
        }




        checkDatabaseReference();

        final ListView list = (ListView) findViewById(R.id.maintenance_list);

        final FirebaseListAdapter<Maintenance> mAdapter;
        if(identifier.equals("landlord")){
            mAdapter = new FirebaseListAdapter<Maintenance>(this, Maintenance.class, android.R.layout.simple_list_item_2, myRef) {
                @Override
                protected void populateView(View v, Maintenance model, int position) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText(model.getTitle());
                    ((TextView)v.findViewById(android.R.id.text2)).setText("Submitted by: " + model.getName());
                }
            };
        }
        else{
            mAdapter = new FirebaseListAdapter<Maintenance>(this, Maintenance.class, android.R.layout.simple_list_item_2, myRef) {
                @Override
                protected void populateView(View v, Maintenance model, int position) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText(model.getTitle());
                    ((TextView)v.findViewById(android.R.id.text2)).setText("Submitted by: " + model.getName());
                }
            };
        }
        list.setAdapter(mAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(maintenanceActivity.this);
                adb.setTitle(maintenances.get(position).getTitle());
                adb.setMessage(maintenances.get(position).getDescription());
                adb.setPositiveButton("Ok", null);
                adb.show();
            }
        });

        //Long click to delete message
        //ONLY LANDLORDS CAN DELETE
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //ONLY A LANDLORD SHOULD BE ABLE TO DELETE
                if(identifier.equals("landlord")){
                    AlertDialog.Builder adb = new AlertDialog.Builder(maintenanceActivity.this);
                    adb.setTitle("Is this maintenance request complete?");
                    adb.setNegativeButton("No", null);
                    adb.setPositiveButton("Yes", new AlertDialog.OnClickListener(){
                        public void onClick(DialogInterface dialogInterface, int which){
                            mAdapter.getRef(position).removeValue();
                        }
                    });
                    adb.show();
                }
                return true;
            }
        });

    }

    protected void onPause(){
        super.onPause();

        if(identifier.equals("tenant")){
            maintenance_title = (EditText) findViewById(R.id.maintenance_title);
            maintenance_description = (EditText) findViewById(R.id.maintenance_description);

            SharedPreferences.Editor ed = mPrefs.edit();
            ed.putString("title", maintenance_title.getText().toString());
            ed.putString("description", maintenance_description.getText().toString());
            ed.commit();
        }

    }

    public void checkDatabaseReference(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    maintenances.add(postSnapshot.getValue(Maintenance.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
