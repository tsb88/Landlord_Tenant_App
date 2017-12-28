package com.example.brett.landlord_tenant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class messagesActivity extends AppCompatActivity {


    SharedPreferences mPrefs;
    String message;
    String name ="";
    String identifier = "";
    String username = "";
    String landlordUsername = "";

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference myRef = db.getReference().child("messages");

    EditText message_prompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!= null){
            name = extras.getString("name");
            identifier = extras.getString("identifier");
            username = extras.getString("username");
            landlordUsername = extras.getString("landlordUsername");
        }

        message_prompt = (EditText) findViewById(R.id.messages_prompt);

        mPrefs = getSharedPreferences("key", Context.MODE_PRIVATE);
        mPrefs.getString("message", message_prompt.getText().toString());

        Button button = (Button) findViewById(R.id.maint_button);

        final ListView list = (ListView) findViewById(R.id.messages_list);

        final FirebaseListAdapter<Maintenance> mAdapter;
        if(identifier.equals("landlord")){
            mAdapter = new FirebaseListAdapter<Maintenance>(this, Maintenance.class, android.R.layout.simple_list_item_2, myRef.child(username)) {
                @Override
                protected void populateView(View v, Maintenance model, int position) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText(model.getTitle());
                    ((TextView)v.findViewById(android.R.id.text2)).setText("Submitted by: " + model.getName());
                }
            };
        }
        else{
            mAdapter = new FirebaseListAdapter<Maintenance>(this, Maintenance.class, android.R.layout.simple_list_item_2, myRef.child(landlordUsername)) {
                @Override
                protected void populateView(View v, Maintenance model, int position) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText(model.getTitle());
                    ((TextView)v.findViewById(android.R.id.text2)).setText("Submitted by: " + model.getName());
                }
            };
        }

        list.setAdapter(mAdapter);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(messagesActivity.this);

                builder.setTitle("Send the message?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(identifier.equals("landlord")){
                            myRef.child(username).push().setValue(new Maintenance(name, message_prompt.getText().toString(), "" ));
                            Toast.makeText(messagesActivity.this, "Request submitted", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            myRef.child(landlordUsername).push().setValue(new Maintenance(name, message_prompt.getText().toString(), "" ));
                            Toast.makeText(messagesActivity.this, "Request submitted", Toast.LENGTH_SHORT).show();
                        }
                        message_prompt.setText("");

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

    }

    protected void onPause(){
        super.onPause();

        SharedPreferences.Editor ed = mPrefs.edit();
        message_prompt = (EditText) findViewById(R.id.messages_prompt);
        ed.putString("message", message_prompt.getText().toString());
        ed.commit();
    }
}
