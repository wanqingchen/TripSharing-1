package com.example.tainingzhang.tripsharing_v0;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class info extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    ViewPager viewPager;
    LinearLayout slideDotspanel;
    private int dotscount;
    private ImageView[] dots;
    private GoogleApiClient mGoogleApiClient;
    ListView listView;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private FirebaseAuth firebaseAuth;
    Context context;
    String place_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        firebaseAuth = FirebaseAuth.getInstance();
        context = this;
        place_id = getIntent().getExtras().getString("PlaceId");
        //place_id = "12345678"; // you should get this id from the main activity;
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        DatabaseReference place = myRef.child("Place").child(place_id);
        final DatabaseReference user = myRef.child("users").child(firebaseAuth.getCurrentUser().getUid().toString());
        final DatabaseReference Comment = place.child("Comments");


        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        slideDotspanel = (LinearLayout) findViewById(R.id.SlideDots);

        // store the image get from Menglu to this array
        //
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        placePhotosTask(place_id, viewPager, this);


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
                                    user.addValueEventListener(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String userName = dataSnapshot.child("name").getValue().toString();
                                            String comments = editText.getText().toString();

                                            Comments c = new Comments(userName, comments);
                                            String id = Comment.push().getKey();
                                            Comment.child(id).setValue(c);

                                            alert(comments);
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
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
        i.putExtra("PlaceId", place_id);
        startActivity(i);
    }

    public void alert(String s) {
        Toast toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void update(String userName, String comments) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    abstract class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> {

        private int mHeight;
        private int mWidth;

        public PhotoTask(int width, int height) {
            mHeight = height;
            mWidth = width;
        }

        // Loads the first photo for a place id from the Geo Data API.
        // The place id must be the first (and only) parameter.
        @Override
        protected AttributedPhoto doInBackground(String... params) {
            if (params.length != 1) {
                return null;
            }
            final String placeId = params[0];
            AttributedPhoto attributedPhoto = null;
            ArrayList<CharSequence> attributionList = new ArrayList<CharSequence>();
            ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();

            PlacePhotoMetadataResult result = Places.GeoDataApi
                    .getPlacePhotos(mGoogleApiClient, placeId).await();

            if (result.getStatus().isSuccess()) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
                for (int i = 0; i < 5; i++) { // change i to get different numbers of photos
                    if (photoMetadataBuffer.getCount() > i && !isCancelled()) {
                        // Get the first bitmap and its attributions.
                        PlacePhotoMetadata photo = photoMetadataBuffer.get(i);
                        attributionList.add(photo.getAttributions());
                        // Load a scaled bitmap for this photo.
                        bitmapList.add(photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                                .getBitmap());
                    }
                }
                attributedPhoto = new AttributedPhoto(attributionList, bitmapList);
                // Release the PlacePhotoMetadataBuffer.
                photoMetadataBuffer.release();
            }
            return attributedPhoto;
        }

        // Holder for an image and its attribution.
        class AttributedPhoto {
            public final ArrayList<CharSequence> attribution;

            public final ArrayList<Bitmap> bitmap;

            public AttributedPhoto(ArrayList<CharSequence> attribution, ArrayList<Bitmap> bitmap) {
                this.attribution = attribution;
                this.bitmap = bitmap;
            }
        }
    }

    private void placePhotosTask(String placeId, final ViewPager viewPager, final Context context) {
        // Create a new AsyncTask that displays the bitmap and attribution once loaded.
        new PhotoTask(500, 500) {
            @Override
            protected void onPreExecute() {
                // Display a temporary image to show while bitmap is loading.
                //mImageView.setImageResource(R.drawable.empty_photo);
            }
            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    Bitmap[] images = new Bitmap[5];
                    for(int i = 0; i < attributedPhoto.bitmap.size(); i++) {
                        images[i] = attributedPhoto.bitmap.get(i);
                    }
                    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(context, images);

                    viewPager.setAdapter(viewPagerAdapter);

                    dotscount = viewPagerAdapter.getCount();
                    dots = new ImageView[dotscount];

                    for(int i = 0; i < dotscount; i++) {

                        dots[i] = new ImageView(context);
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

                }
            }
        }.execute(placeId);
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


