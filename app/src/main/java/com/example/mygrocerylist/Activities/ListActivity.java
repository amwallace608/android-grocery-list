package com.example.mygrocerylist.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.mygrocerylist.Data.DatabaseHandler;
import com.example.mygrocerylist.Model.Grocery;
import com.example.mygrocerylist.UI.RecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mygrocerylist.R;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Grocery> groceryList;
    private List<Grocery> listItems;
    private DatabaseHandler db;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText groceryItem, groceryQuantity;
    private Button saveBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                createPopupDialog();
            }
        });

        //set up DB handler and RecyclerView
        db = new DatabaseHandler(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //setup lists
        groceryList = new ArrayList<>();
        listItems = new ArrayList<>();
        //get groceries from DB
        groceryList = db.getAllGroceries();

        //prepare listItems groceries for display in recyclerView
        for(Grocery c : groceryList){
            Grocery grocery = new Grocery(c.getId(), c.getName(),
                    "Qty: " + c.getQuantity(),
                    "Added on: " + c.getDateAdded());
            //add to list
            listItems.add(grocery);
        }
        //create adapter with grocery list
        recyclerViewAdapter = new RecyclerViewAdapter(this,listItems);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

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
        grocery.setName(groceryItem.getText().toString());
        grocery.setQuantity(groceryQuantity.getText().toString());

        //Save to DB
        db.addGrocery(grocery);

        //Snackbar.make(v,"Item Saved!", Snackbar.LENGTH_LONG).show();
        //display ID of item added (ID = number of items in DB)
        //Log.d("Item Added ID: ",String.valueOf(db.getGroceryCount()));

        //delay before moving to list activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                //start list activity
                startActivity(new Intent(ListActivity.this, ListActivity.class));
                finish();
            }
        }, 1250);
    }

}
