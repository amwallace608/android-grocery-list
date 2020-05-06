package com.example.mygrocerylist.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.mygrocerylist.Data.DatabaseHandler;
import com.example.mygrocerylist.Model.Grocery;
import com.example.mygrocerylist.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText groceryItem, groceryQuantity;
    private Button saveBtn;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DatabaseHandler(this);

        bypassActivity();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               createPopupDialog();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void createPopupDialog(){
        //build alert dialog, inflate popup layout
        dialogBuilder = new AlertDialog.Builder(this);
        final View view = getLayoutInflater().inflate(R.layout.popup,null);
        //set up edit texts & button
        groceryItem = (EditText) view.findViewById(R.id.groceryItem);
        groceryQuantity = (EditText) view.findViewById(R.id.groceryQuantity);
        saveBtn = (Button) view.findViewById(R.id.saveBtn);

        //build dialog view, show popup
        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();
        //Save button onClickListener
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if there is user input, save
                if(!groceryItem.getText().toString().isEmpty()
                        && !groceryQuantity.getText().toString().isEmpty()) {
                    saveGroceryToDB(v);
                }
                else{
                    Snackbar.make(view,
                            "Enter grocery item and quantity",
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveGroceryToDB(View v) {
        Grocery grocery = new Grocery();
        String newGrocery = groceryItem.getText().toString();
        String newQuantity = groceryQuantity.getText().toString();

        grocery.setName(newGrocery);
        grocery.setQuantity(newQuantity);

        //Save to DB
        db.addGrocery(grocery);

        Snackbar.make(v,"Item Saved!", Snackbar.LENGTH_LONG).show();
        //display ID of item added (ID = number of items in DB)
        //Log.d("Item Added ID: ",String.valueOf(db.getGroceryCount()));
        //delay before moving to list activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                //start list activity
                startActivity(new Intent(MainActivity.this, ListActivity.class));
            }
        }, 1250);
    }
    //Check if database already has items, if it does go to list activity
    public void bypassActivity(){
        if(db.getGroceryCount() > 0){
            startActivity(new Intent(MainActivity.this,ListActivity.class));
            finish();
        }

    }
}
