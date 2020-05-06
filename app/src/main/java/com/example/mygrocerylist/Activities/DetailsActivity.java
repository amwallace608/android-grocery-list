package com.example.mygrocerylist.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mygrocerylist.R;

public class DetailsActivity extends AppCompatActivity {
    public TextView groceryName, quantity, dateAdded;
    public Button editBtn, deleteBtn;
    public int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        groceryName = (TextView) findViewById(R.id.itemNameDet);
        quantity = (TextView) findViewById(R.id.quantityDet);
        dateAdded = (TextView) findViewById(R.id.dateDet);
        editBtn = (Button) findViewById(R.id.editBtnDet);
        deleteBtn = (Button) findViewById(R.id.deleteBtnDet);
        //hide edit and delete buttons
        editBtn.setVisibility(View.INVISIBLE);
        deleteBtn.setVisibility(View.INVISIBLE);

        //get extras, set textViews in CardView
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            groceryName.setText(bundle.get("name").toString());
            quantity.setText(bundle.get("quantity").toString());
            dateAdded.setText(bundle.get("date").toString());
            id = bundle.getInt("id");
        }


    }
}
