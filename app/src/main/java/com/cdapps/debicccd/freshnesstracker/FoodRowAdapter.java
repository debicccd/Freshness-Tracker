package com.cdapps.debicccd.freshnesstracker;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by Spidey7003 on 7/28/2016.
 */
public class FoodRowAdapter extends BaseAdapter{

    private Context mContext;

    public FoodRowAdapter(Context context){
        mContext = context;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FoodRowView v = null;

        if(convertView == null){
            v = new FoodRowView(mContext);
            v.setName("Food " + position);
        } else {
            v = (FoodRowView) convertView;
        }

        return v;
    }
}
