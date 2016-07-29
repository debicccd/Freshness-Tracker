package com.cdapps.debicccd.freshnesstracker;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ListActivity extends AppCompatActivity {

    private FoodRowAdapter mRowAdapter;

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

        Button confirmButton = (Button) addFoodDialog.findViewById(R.id.confirmButton);

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

                mRowAdapter.addRow(nameEditText.getText().toString(), startDate, endDate);
                mRowAdapter.notifyDataSetChanged();
                addFoodDialog.dismiss();
            }
        });
        addFoodDialog.show();
    }
}
