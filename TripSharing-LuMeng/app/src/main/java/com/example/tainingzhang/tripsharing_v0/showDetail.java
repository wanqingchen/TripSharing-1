package com.example.tainingzhang.tripsharing_v0;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class showDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);

        String[] users = {"Jeff", "Cindy", "James"};
        ListAdapter usersAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, users);
        ListView userListView = (ListView) findViewById(R.id.userListView);
        userListView.setAdapter(usersAdapter);
    }
}
