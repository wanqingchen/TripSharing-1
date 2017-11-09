package com.example.tainingzhang.tripsharing_v0;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class info extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout slideDotspanel;
    private int dotscount;
    private ImageView[] dots;
    ListView listView;
    FirebaseDatabase database;
    DatabaseReference myRef;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        context = this;
        String place_id = "12345678";
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        DatabaseReference place = myRef.child("Place").child(place_id);
        final DatabaseReference Comment = place.child("Comments");


        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        slideDotspanel = (LinearLayout) findViewById(R.id.SlideDots);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);

        viewPager.setAdapter(viewPagerAdapter);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for(int i = 0; i < dotscount; i++) {

            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            slideDotspanel.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for(int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        Comment.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> usernames = new ArrayList<>();
                List<String> contents = new ArrayList<>();

                for(DataSnapshot comment : dataSnapshot.getChildren()) {
                    usernames.add(comment.child("user").getValue(String.class));
                    contents.add(comment.child("Content").getValue(String.class));
                }
                alert(usernames.size() + "");
                final String[] userName = new String[usernames.size() + 1];
                String[] comments = new String[contents.size() + 1];
                for(int i = 0; i < usernames.size(); i++) {
                    userName[i] = usernames.get(i);
                    comments[i] = contents.get(i);
                }
                final CustomListAdapter listAdapter = new CustomListAdapter(context, userName, comments);
                listView = (ListView) findViewById(R.id.listView);
                listView.setAdapter(listAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if(i == listAdapter.getCount() - 1) {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(info.this);
                            View mView = getLayoutInflater().inflate(R.layout.dialog_comments, null);
                            final EditText editText = (EditText)mView.findViewById(R.id.editText);
                            Button btn = (Button) mView.findViewById(R.id.btnComment);
                            mBuilder.setView(mView);
                            final AlertDialog dialog = mBuilder.create();
                            dialog.show();
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String comments = editText.getText().toString();
                                    Comments c = new Comments("James", comments);
                                    String id = Comment.push().getKey();
                                    Comment.child(id).setValue(c);

                                    alert(comments);
                                    dialog.dismiss();
                                }

                            });
//                            mBuilder.setView(mView);
//                            AlertDialog dialog = mBuilder.create();
//                            dialog.show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                alert(databaseError.toString());
            }
        });



    }

    public void viewOnClick(View v) {
        Intent i = new Intent(getApplicationContext(), showDetail.class);
        startActivity(i);
    }

    public void alert(String s) {
        Toast toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void update(String userName, String comments) {

    }

}

class Comments{
    String user;
    String Content;
    public Comments(String user, String comments) {
        this.Content = comments;
        this.user = user;
    }
}
