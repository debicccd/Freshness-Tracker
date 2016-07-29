package com.cdapps.debicccd.freshnesstracker;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Spidey7003 on 7/28/2016.
 */
public class FoodRowView extends LinearLayout{

    private ImageView mImageView;
    private TextView mNameTextView;
    private TextView mStartDateTextView;
    private TextView mEndDateTextView;

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
}
