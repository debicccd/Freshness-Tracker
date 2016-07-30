package com.cdapps.debicccd.freshnesstracker;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ListActivity extends AppCompatActivity {
    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    private static final double MAX_SIZE = 150;

    private FoodRowAdapter mRowAdapter;
    private Context mContext;
    private Uri mPictureUri = Uri.parse("@mipmap/ic_launcher");
    private ImageButton mImageButton;

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
            return true;
        }

        return super.onOptionsItemSelected(item);
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



                mRowAdapter.addRow(nameEditText.getText().toString(), startDate, endDate, mPictureUri);
                mRowAdapter.notifyDataSetChanged();
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
                Environment.DIRECTORY_PICTURES), "CameraDemo");

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
}
