package com.cdapps.debicccd.freshnesstracker;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by Spidey7003 on 7/28/2016.
 */
public class FoodRowAdapter extends BaseAdapter{

    private ArrayList<FoodRowView> mRows;

    private Context mContext;

    public FoodRowAdapter(Context context){
        mContext = context;
        mRows = new ArrayList<FoodRowView>();
    }

    public void addRow(String name, String startDate, String endDate, Uri uri){
        FoodRowView v = new FoodRowView(mContext);
        v.setName(name);
        v.setStartDate(startDate);
        v.setEndDate(endDate);
        v.setPictureUri(uri, mContext);

        mRows.add(v);
    }

    @Override
    public int getCount() {
        return mRows.size();
    }

    @Override
    public Object getItem(int position) {
        if(position >= mRows.size()){
            return null;
        }

        return mRows.get(position);
    }

    @Override
    public long getItemId(int position) {
        if(position >= mRows.size()){
            return 0;
        }

        return mRows.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FoodRowView v = null;

        if(convertView == null){
            v = new FoodRowView(mContext);
            if(position < mRows.size()){
                v = mRows.get(position);
            }
        } else {
            v = (FoodRowView) convertView;
        }

        return v;
    }

    public void addRow(FoodRowView row) {
        this.mRows.add(row);
    }

    public void clear() {
        mRows.clear();
    }
}
