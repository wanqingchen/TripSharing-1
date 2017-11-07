package com.example.tainingzhang.tripsharing_v0;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Photos extends FragmentActivity implements OnConnectionFailedListener {

    private static final String TAG = "Photos";
    private GoogleApiClient mGoogleApiClient;
    private String placeId = MainActivity.getPlaceID();
    private ImageView mImageView1;
    private TextView mText1;
    private ImageView mImageView2;
    private TextView mText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, placeId);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mImageView1 = (ImageView) findViewById(R.id.photo1);
        mText1 = (TextView) findViewById(R.id.attribution1);
        mImageView2 = (ImageView) findViewById(R.id.photo2);
        mText2 = (TextView) findViewById(R.id.attribution2);
        placePhotosTask(placeId, mImageView1, mText1, 0);
        placePhotosTask(placeId, mImageView2, mText2, 1);

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
                for (int i = 0; i < 2; i++) { // change i to get different numbers of photos
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

    private void placePhotosTask(String placeId, final ImageView photo, final TextView attri, final int num) {
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
                    if (attributedPhoto.bitmap.size() > num) {
                        // Photo has been loaded, display it.
                        photo.setImageBitmap(attributedPhoto.bitmap.get(num));
                        // Display the attribution as HTML content if set.
                        if (attributedPhoto.attribution == null) {
                            attri.setVisibility(View.GONE);
                        } else {
                            attri.setVisibility(View.VISIBLE);
                            attri.setText(Html.fromHtml(attributedPhoto.attribution.get(num).toString()));
                        }
                    }
                }
            }
        }.execute(placeId);
    }

}
