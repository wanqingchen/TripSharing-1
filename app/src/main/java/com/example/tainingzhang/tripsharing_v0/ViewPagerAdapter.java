package com.example.tainingzhang.tripsharing_v0;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Taining on 11/1/17.
 */

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    //private Integer[] images = {R.drawable.slide1, R.drawable.slide2, R.drawable.slide3}; // change this type of this field. change to bitmap[]
    private Bitmap[] images;
    //
    // You should change this constructor, add the images field.
    //
    public ViewPagerAdapter(Context context, Bitmap[] images) {
        this.context = context;
        this.images = images;
    }
    //
    //
    //

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
       return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView2);
        //imageView.setImageResource(images[position]); // currently use integer to set the image. Ask Menglu how to set bitmap in imageView
        imageView.setImageBitmap(images[position]);
        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}
