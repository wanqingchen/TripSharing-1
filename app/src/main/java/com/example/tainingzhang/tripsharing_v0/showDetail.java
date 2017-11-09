package com.example.tainingzhang.tripsharing_v0;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class showDetail extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference Users;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        String place_id = getIntent().getExtras().getString("PlaceId");
        setContentView(R.layout.activity_show_detail);
        Button btnJoin = (Button) findViewById(R.id.btnJoin);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        final DatabaseReference user = myRef.child("users").child(firebaseAuth.getCurrentUser().getUid().toString());
        Users = myRef.child("Place").child(place_id).child("users");

        Users.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> userNames = new ArrayList<>();
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    userNames.add(user.getValue().toString());
                }
                String[] users = new String[userNames.size()];
                for(int i = 0; i < users.length; i++) {
                    users[i] = userNames.get(i);
                }
                ListAdapter usersAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, users);
                ListView userListView = (ListView) findViewById(R.id.userListView);
                userListView.setAdapter(usersAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Users.child(firebaseAuth.getCurrentUser().getUid().toString()).setValue(dataSnapshot.child("name").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                //Users.child(firebaseAuth.getCurrentUser().getUid().toString()).setValue(firebaseAuth.getCurrentUser().getDisplayName().toString());
            }
        });
    }
}
