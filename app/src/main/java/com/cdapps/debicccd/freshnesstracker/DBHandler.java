package com.cdapps.debicccd.freshnesstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spidey7003 on 8/2/2016.
 */
public class DBHandler extends SQLiteOpenHelper {

    private static Context mContext;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "freshnessTracker";
    private static final String TABE_FOOD_ROWS = "foodRows";

    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_URI = "uri";
    private static final String KEY_START_DATE = "startDate";
    private static final String KEY_END_DATE = "endDate";

    public DBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FOOD_ROWS_TABLE = "CREATE TABLE " + TABE_FOOD_ROWS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_URI + " TEXT," +
                KEY_START_DATE + " TEXT," + KEY_END_DATE + " TEXT" + ")";

        db.execSQL(CREATE_FOOD_ROWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABE_FOOD_ROWS);

        onCreate(db);
    }

    public void addFoodRowView(FoodRowView foodRowView){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, foodRowView.getName());
        values.put(KEY_URI, foodRowView.getUri());
        values.put(KEY_START_DATE, foodRowView.getStartDate());
        values.put(KEY_END_DATE, foodRowView.getEndDate());

        db.insert(TABE_FOOD_ROWS, null, values);
        db.close();
    }

    public FoodRowView getFoodRowView(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABE_FOOD_ROWS, new String[] {
                KEY_ID,
                KEY_NAME,
                KEY_URI,
                KEY_START_DATE,
                KEY_END_DATE
            }, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
        }

        FoodRowView foodRowView = new FoodRowView(mContext);

        foodRowView.setID(Integer.parseInt(cursor.getString(0)));
        foodRowView.setName(cursor.getString(1));
        foodRowView.setPictureUri(Uri.parse(cursor.getString(2)), mContext);
        foodRowView.setStartDate(cursor.getString(3));
        foodRowView.setEndDate(cursor.getString(4));

        db.close();

        return foodRowView;
    }

    public List<FoodRowView> getAll(){
        List<FoodRowView> foodRowViews = new ArrayList<FoodRowView>();

        String selectQuery = "SELECT * FROM " + TABE_FOOD_ROWS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do {
                FoodRowView foodRowView = new FoodRowView(mContext);

                foodRowView.setID(Integer.parseInt(cursor.getString(0)));
                foodRowView.setName(cursor.getString(1));
                foodRowView.setPictureUri(Uri.parse(cursor.getString(2)), mContext);
                foodRowView.setStartDate(cursor.getString(3));
                foodRowView.setEndDate(cursor.getString(4));

                foodRowViews.add(foodRowView);
            } while (cursor.moveToNext());
        }

        db.close();

        return foodRowViews;
    }

    public int getCount(){
        String selectQuery = "SELECT * FROM " + TABE_FOOD_ROWS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.close();
        db.close();

        return  cursor.getCount();
    }

    public int updateRow(FoodRowView foodRowView){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, foodRowView.getName());
        values.put(KEY_URI, foodRowView.getUri());
        values.put(KEY_START_DATE, foodRowView.getStartDate());
        values.put(KEY_END_DATE, foodRowView.getEndDate());

        int ret = db.update(TABE_FOOD_ROWS, values, KEY_ID + " = ?", new String[] {String.valueOf(foodRowView.getID())});
        db.close();

        return ret;
    }

    public void deleteRow(FoodRowView foodRowView){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABE_FOOD_ROWS, KEY_ID + " = ?", new String[] { String.valueOf(foodRowView.getID())});
        db.close();
    }
}
