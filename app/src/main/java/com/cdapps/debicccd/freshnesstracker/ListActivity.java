package com.cdapps.debicccd.freshnesstracker;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    private static final double MAX_SIZE = 150;
    private static final String SHARED_PREF_KEY = "freshness_tracker_prefs";
    private static final String NOTIFICATION_TIME_PREF_KEY = "notification_time";
    private static final String RED_DAYS_PREF_KEY = "red_days";
    private static final String YELLOW_DAYS_PREF_KEY = "yellow_days";

    private FoodRowAdapter mRowAdapter;
    private Context mContext;
    private Uri mPictureUri = Uri.parse("@mipmap/ic_launcher");
    private ImageButton mImageButton;
    private DBHandler mDB;
    private SharedPreferences mSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddFoodDialog();
            }
        });

        final ListView listView = (ListView) findViewById(R.id.listView);

        mRowAdapter = new FoodRowAdapter(this);

        listView.setAdapter(mRowAdapter);

        mContext = this;

        mDB = new DBHandler(this);

        mSharedPrefs = getSharedPreferences(SHARED_PREF_KEY, this.MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.restoreRows();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSettings() {
        final Dialog settingsDialog = new Dialog(this);
        settingsDialog.setContentView(R.layout.settings);
        settingsDialog.setTitle("Edit Settings");

        final Button notificationTimeButton = (Button) settingsDialog.findViewById(R.id.notificatonTimeButton);
        final EditText redEditText = (EditText) settingsDialog.findViewById(R.id.redEditText);
        final EditText yellowEditText = (EditText) settingsDialog.findViewById(R.id.yellowEditText);
        final Button confirmButton = (Button) settingsDialog.findViewById(R.id.confirmButton);
        final Button cancelButton = (Button) settingsDialog.findViewById(R.id.cancelButton);

        notificationTimeButton.setText(mSharedPrefs.getString(NOTIFICATION_TIME_PREF_KEY, "10:00"));
        redEditText.setText(""+mSharedPrefs.getInt(RED_DAYS_PREF_KEY, 1));
        yellowEditText.setText(""+mSharedPrefs.getInt(YELLOW_DAYS_PREF_KEY, 3));

        notificationTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = notificationTimeButton.getText().toString();
                int h = 10;
                int m = 0;
                try {
                    h = Integer.parseInt(time.split(":")[0]);
                    m = Integer.parseInt(time.split(":")[1]);
                } catch(Exception e){
                    // TODO: 8/3/2016 Handle exceptions
                }


                TimePickerDialog tp1 = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        notificationTimeButton.setText(hourOfDay + ":" + minute);
                    }
                }, h, m, true);
                tp1.show();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mSharedPrefs.edit();

                editor.putString(NOTIFICATION_TIME_PREF_KEY, notificationTimeButton.getText().toString());
                editor.putInt(RED_DAYS_PREF_KEY, Integer.parseInt(redEditText.getText().toString()));
                editor.putInt(YELLOW_DAYS_PREF_KEY, Integer.parseInt(yellowEditText.getText().toString()));

                editor.apply();

                settingsDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsDialog.dismiss();
            }
        });

        settingsDialog.show();
    }

    protected void showAddFoodDialog(){
        final Dialog addFoodDialog = new Dialog(this);
        addFoodDialog.setContentView(R.layout.add_food_dialog);
        addFoodDialog.setTitle("Add New Food");

        final Button confirmButton = (Button) addFoodDialog.findViewById(R.id.confirmButton);
        mImageButton = (ImageButton) addFoodDialog.findViewById(R.id.imageButton);

        final EditText nameEditText = (EditText) addFoodDialog.findViewById(R.id.foodNameDialogTextView);
        final DatePicker endDatePicker = (DatePicker) addFoodDialog.findViewById(R.id.datePicker);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat mdformat = new SimpleDateFormat("MM/dd/yyyy");
                String startDate = mdformat.format(calendar.getTime());

                String endDate = (endDatePicker.getMonth() + 1) + "/";
                endDate += endDatePicker.getDayOfMonth() + "/";
                endDate += endDatePicker.getYear();

                FoodRowView foodRowView = new FoodRowView(mContext);

                foodRowView.setName(nameEditText.getText().toString());
                foodRowView.setPictureUri(mPictureUri, mContext);
                foodRowView.setStartDate(startDate);
                foodRowView.setEndDate(endDate);

                mRowAdapter.addRow(foodRowView);
                mRowAdapter.notifyDataSetChanged();

                mDB.addFoodRowView(foodRowView);

                addFoodDialog.dismiss();
            }
        });

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager packageManager = mContext.getPackageManager();
                if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false){
                    Toast.makeText(mContext, "This device does not have a camera.", Toast.LENGTH_LONG).show();
                    return;
                }
                takePicture(v);
            }
        });


        mPictureUri = Uri.parse("@mipmap/ic_launcher");
        addFoodDialog.show();
    }

    public void takePicture(View v){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mPictureUri = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPictureUri);

        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmapImage = null;

                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mPictureUri);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                int nh = (int) ( bitmapImage.getHeight() * (MAX_SIZE / bitmapImage.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, (int) MAX_SIZE, nh, true);
                mImageButton.setImageBitmap(scaled);
            }
        }
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "FreshnessKeeper");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = mdformat.format(calendar.getTime());

        return new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
    }

    private void restoreRows(){
        mRowAdapter.clear();
        List<FoodRowView> rows = mDB.getAll();

        for(FoodRowView row : rows){
            if(daysUntil(row.getEndDate()) <= mSharedPrefs.getInt(RED_DAYS_PREF_KEY, 1)) {
                row.setBackgroundColor(ContextCompat.getColor(mContext, R.color.lightRed));
            } else if (daysUntil(row.getEndDate()) <= mSharedPrefs.getInt(YELLOW_DAYS_PREF_KEY, 3)){
                row.setBackgroundColor(ContextCompat.getColor(mContext, R.color.lightYellow));
            }
            mRowAdapter.addRow(row);
        }
    }

    private int daysUntil(String end){
        int m = 0;
        int d = 0;
        int y = 0;
        try{
            m = Integer.parseInt(end.split("/")[0])-1;
            d = Integer.parseInt(end.split("/")[1]);
            y = Integer.parseInt(end.split("/")[2]);

            //Log.d("DAYS", m + ":" + d + ":" + y);
        } catch(Exception e){
            // TODO: 8/3/2016 Handle exception
        }
        Calendar endDate = Calendar.getInstance();
        endDate.set(y, m, d);

        int result = 0;

        while(endDate.after(Calendar.getInstance())){
            //Log.d("DAYS", "end " + endDate.getTime() + ":" + "now " + Calendar.getInstance().getTime());
            result++;
            endDate.add(Calendar.DAY_OF_MONTH, -1);
        }

        Log.d("DAYS", "result = " + result);

        return result;
    }
}
