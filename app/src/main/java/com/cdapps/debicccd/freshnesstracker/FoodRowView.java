package com.cdapps.debicccd.freshnesstracker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by Spidey7003 on 7/28/2016.
 */
public class FoodRowView extends LinearLayout{
    private static double MAX_SIZE = 128;

    private ImageView mImageView;
    private TextView mNameTextView;
    private TextView mStartDateTextView;
    private TextView mEndDateTextView;
    private Uri mPictureUri;

    public FoodRowView(Context context){
        super(context);

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        inflater.inflate(R.layout.food_item_row, this);

        mImageView = (ImageView) findViewById(R.id.imageView);
        mNameTextView = (TextView) findViewById(R.id.nameTextView);
        mStartDateTextView = (TextView) findViewById(R.id.startDateTextView);
        mEndDateTextView = (TextView) findViewById(R.id.endDateTextView);
    }

    public void setName(String name){
        mNameTextView.setText(name);
    }

    public void setStartDate(String date){
        mStartDateTextView.setText(date);
    }

    public void setEndDate(String date){
        mEndDateTextView.setText(date);
    }

    public void setPictureUri(Uri uri, Context context){
        mPictureUri = uri;

        Bitmap bitmapImage = null;
        try {
            bitmapImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        int nh = (int) ( bitmapImage.getHeight() * (MAX_SIZE / bitmapImage.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, (int) MAX_SIZE, nh, true);
        mImageView.setImageBitmap(scaled);
    }
}
