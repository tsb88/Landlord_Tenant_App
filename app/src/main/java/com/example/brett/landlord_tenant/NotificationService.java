package com.example.brett.landlord_tenant;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationService extends Service {

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference maintenance = db.getReference().child("maintenance");
    DatabaseReference messages = db.getReference().child("messages");
    int maintenance_count=0;
    int messages_count=0;
    int slave =1;
    int slave2 = 1;
    String name = "";
    String identifier = "";
    String landlord = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int StartId){
        name = (String) intent.getExtras().get("name");
        identifier = (String) intent.getExtras().get("identifier");
        landlord = (String) intent.getExtras().get("landlord");
        return START_STICKY;
    }

    @Override
    public void onCreate(){

        maintenance.child(landlord).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    maintenance_count++;
                }
                if(slave <= maintenance_count){
                    postNotification("Landlord_tenant", "New maintenance request!");
                    slave = maintenance_count;
                }
                maintenance_count=0;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        messages.child(landlord).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    messages_count++;
                }
                if(slave2 <= messages_count){
                    postNotification("Landlord_tenant", "New message!");
                    slave2 = messages_count;
                }
                messages_count=0;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void postNotification(String title, String text){
        Intent resultIntent = new Intent(this, Login.class);
        String CHANNEL_ID = "my_channel_01";
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
          .setSmallIcon(R.drawable.ic_action_name)
          .setContentTitle(title)
          .setContentText(text);
        mBuilder.setContentIntent(resultPendingIntent);
        int mNotificationID = 001;
        NotificationManager mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyManager.notify(mNotificationID,mBuilder.build());
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
