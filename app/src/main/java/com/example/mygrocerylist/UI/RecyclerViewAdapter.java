package com.example.mygrocerylist.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygrocerylist.Activities.DetailsActivity;
import com.example.mygrocerylist.Data.DatabaseHandler;
import com.example.mygrocerylist.Model.Grocery;
import com.example.mygrocerylist.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Grocery> groceryItems;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private LayoutInflater inflater;


    public RecyclerViewAdapter(Context context, List<Grocery> groceryItems) {
        this.context = context;
        this.groceryItems = groceryItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate list_item layout
        View view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.list_item, parent, false);
        //return viewholder with inflated list item view
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //get current grocery item
        Grocery grocery = groceryItems.get(position);
        //set TextViews for grocery item data
        holder.groceryName.setText(grocery.getName());
        holder.quantity.setText(grocery.getQuantity());
        holder.dateAdded.setText(grocery.getDateAdded());

    }

    @Override
    public int getItemCount() {
        return groceryItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView groceryName, quantity, dateAdded;
        public Button editBtn, deleteBtn;
        public int id;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);

            context = ctx;
            //set up TextViews & Buttons
            groceryName = (TextView) itemView.findViewById(R.id.name);
            quantity = (TextView) itemView.findViewById(R.id.quantity);
            dateAdded = (TextView) itemView.findViewById(R.id.dateAdded);
            editBtn = (Button) itemView.findViewById(R.id.editBtn);
            deleteBtn = (Button) itemView.findViewById(R.id.deleteBtn);

            editBtn.setOnClickListener(this);
            deleteBtn.setOnClickListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //go to details screen, with extra item info
                    int position = getAdapterPosition();
                    Grocery grocery = groceryItems.get(position);
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("name", grocery.getName());
                    intent.putExtra("quantity",grocery.getQuantity());
                    intent.putExtra("id",grocery.getId());
                    intent.putExtra("date",grocery.getDateAdded());
                    context.startActivity(intent);
                }
            });

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.editBtn:
                    //get grocery, pass to editItem
                    Grocery groceryEdt = groceryItems.get(getAdapterPosition());
                    editItem(groceryEdt);
                    break;
                case R.id.deleteBtn:
                    //get grocery, pass to deleteItem
                    Grocery groceryDel = groceryItems.get(getAdapterPosition());
                    deleteItem(groceryDel.getId());
                    break;
                default:
                    break;
            }
        }

        public void deleteItem(final int id){
            //alert dialog for delete confirmation
            alertDialogBuilder = new AlertDialog.Builder(context);

            inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.confirmationdialog,null);
            //setup buttons for dialog
            Button noBtn = (Button) view.findViewById(R.id.noBtn);
            Button yesBtn = (Button) view.findViewById(R.id.yesBtn);
            //create and show alert dialog view
            alertDialogBuilder.setView(view);
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            //no, don't delete, return to list view
            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            //yes, delete item from database
            yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //delete the item
                    DatabaseHandler db = new DatabaseHandler(context);
                    //delete from DB
                    db.deleteGrocery(id);
                    //Remove from list
                    groceryItems.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    Snackbar.make(view, "Item Deleted",
                            Snackbar.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            });
        }
        public void editItem(final Grocery grocery){
            //popup alert dialog to edit the grocery details
            alertDialogBuilder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.popup, null);

            //set up edit texts & button
            final EditText groceryItem = (EditText) view.findViewById(R.id.groceryItem);
            final EditText quantity = (EditText) view.findViewById(R.id.groceryQuantity);
            TextView title = (TextView) view.findViewById(R.id.tile);
            Button saveBtn = (Button) view.findViewById(R.id.saveBtn);
            //change title of popup
            title.setText("Edit Grocery Item");

            //Create and show alert dialog view
            alertDialogBuilder.setView(view);
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //update if there was user input
                    if(!groceryItem.getText().toString().isEmpty()
                            && !quantity.getText().toString().isEmpty()){

                        DatabaseHandler db = new DatabaseHandler(context);
                        //Update Item name and quantity
                        grocery.setName(groceryItem.getText().toString());
                        grocery.setQuantity(quantity.getText().toString());
                        //update DB and notify change
                        db.updateGrocery(grocery);
                        notifyItemChanged(getAdapterPosition(),grocery);
                        Snackbar.make(view, "Item Updated",
                                Snackbar.LENGTH_SHORT).show();

                        alertDialog.dismiss();
                    } else{
                        Snackbar.make(view, "Enter Grocery and Quantity",
                                Snackbar.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }
}
