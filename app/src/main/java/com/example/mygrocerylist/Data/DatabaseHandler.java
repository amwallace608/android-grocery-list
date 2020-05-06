package com.example.mygrocerylist.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.mygrocerylist.Model.Grocery;
import com.example.mygrocerylist.Util.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private Context context;
    public DatabaseHandler(@Nullable Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        this.context = context;
    }

    //create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        //create groceries table w/ SQL
        String CREATE_GROCERY_TABLE = "CREATE TABLE " + Constants.TABLE_NAME + "("
                + Constants.KEY_ID + " INTEGER PRIMARY KEY," + Constants.KEY_GROCERY_ITEM + " TEXT,"
                + Constants.KEY_QUANTITY+ " TEXT," + Constants.KEY_DATE_ADDED + " LONG" + ")";

        db.execSQL(CREATE_GROCERY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //delete existing table and recreate
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        //create table again
        onCreate(db);
    }

//CRUD Operation Methods
    //Add grocery item
    public void addGrocery(Grocery grocery){
        //instantiate db w/ properties to write
        SQLiteDatabase db = this.getWritableDatabase();
        //create key value pairs for grocery data
        ContentValues groceryValues = new ContentValues();
        groceryValues.put(Constants.KEY_GROCERY_ITEM,grocery.getName());
        groceryValues.put(Constants.KEY_QUANTITY,grocery.getQuantity());
        groceryValues.put(Constants.KEY_DATE_ADDED, java.lang.System.currentTimeMillis());

        //insert into table
        db.insert(Constants.TABLE_NAME, null,groceryValues);
        Log.d("Saved: ", "Saved grocery item to DB");
        db.close();
    }

    //Get grocery by Id
    public Grocery getGrocery(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        //cursor w/ db query for contact of specified id
        Cursor cursor = db.query(Constants.TABLE_NAME,
                new String[] {Constants.KEY_ID, Constants.KEY_GROCERY_ITEM,
                        Constants.KEY_QUANTITY, Constants.KEY_DATE_ADDED},
                Constants.KEY_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null);
        //move cursor to first occurance of specified Id (if cursor is not null)
        if(cursor != null){
            cursor.moveToFirst();
            //create grocery w/ retrieved data
            Grocery grocery = new Grocery();
            grocery.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
            grocery.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_GROCERY_ITEM)));
            grocery.setQuantity(cursor.getString(cursor.getColumnIndex(Constants.KEY_QUANTITY)));
            //convert timestamp from milliseconds to readable format
            java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
            String formattedDate = dateFormat.format(
                    new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_DATE_ADDED)))
                            .getTime());
            grocery.setDateAdded(formattedDate);

            cursor.close();
            return grocery;
        }
        else{
            Log.d("Error: ", "Error: Database entry with id: " + id + " does not exist");
            return null;
        }
    }
    //Get all groceries
    public List<Grocery> getAllGroceries(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Grocery> groceryList = new ArrayList<>();
        //get groceries in table, order by date
        Cursor cursor = db.query(Constants.TABLE_NAME, new String[]{
                Constants.KEY_ID, Constants.KEY_GROCERY_ITEM, Constants.KEY_QUANTITY,
                Constants.KEY_DATE_ADDED},
                null, null,null,null,
                Constants.KEY_DATE_ADDED + " DESC");
        //loop through groceries in table
        if(cursor.moveToFirst()){
            do {
                //get grocery data at cursor
                Grocery grocery = new Grocery();
                grocery.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
                grocery.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_GROCERY_ITEM)));
                grocery.setQuantity(cursor.getString(cursor.getColumnIndex(Constants.KEY_QUANTITY)));
                //convert timestamp from milliseconds to readable format
                java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
                String formattedDate = dateFormat.format(
                        new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_DATE_ADDED)))
                                .getTime());
                grocery.setDateAdded(formattedDate);
                //add grocery to list
                groceryList.add(grocery);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return groceryList;
    }

    //update/edit grocery item - returns Id of grocery
    public int updateGrocery(Grocery grocery){
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues groceryValues = new ContentValues();
        groceryValues.put(Constants.KEY_GROCERY_ITEM,grocery.getName());
        groceryValues.put(Constants.KEY_QUANTITY,grocery.getQuantity());
        groceryValues.put(Constants.KEY_DATE_ADDED,java.lang.System.currentTimeMillis());

        //update row corresponding to grocery id
        return db.update(Constants.TABLE_NAME, groceryValues, Constants.KEY_ID+ "=?",
                new String[] {String.valueOf(grocery.getId())});
    }

    //Delete grocery item
    public void deleteGrocery(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        //delete item from table according to grocery ID
        db.delete(Constants.TABLE_NAME, Constants.KEY_ID + "=?",
                new String[]{String.valueOf(id)});

        db.close();
    }

    //get number of groceries in database
    public int getGroceryCount(){
        //Select all table entries SQL
        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        //close cursor and return count
        cursor.close();
        return count;
    }

}
