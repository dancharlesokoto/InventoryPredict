package com.thesis.inventory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapterOrdered extends RecyclerView.Adapter<ViewHolderCart> {

    OrderedActivity orderedActivity;
    List<Model> modelList;
    Context context;

    public CustomAdapterOrdered(OrderedActivity orderedActivity, List<Model> modelList/*, Context context*/) {
        this.orderedActivity = orderedActivity;
        this.modelList = modelList;
        //this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderCart onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.check_model_layout, viewGroup,false);

        ViewHolderCart viewHolder = new ViewHolderCart(itemView);

        viewHolder.setOnClickListener(new ViewHolderCart.ClickListener() {
            @Override
            public void onItemClick(View view, final int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(orderedActivity);

                String[] options = {"Do you want to delete Product?", "Delete Product Record"};

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == 1){

                            orderedActivity.deleteData(position);

                        }

                    }
                }).create().show();

               /* String id = modelList.get(position).getTimestamp();
                String title = modelList.get(position).getTitle();
                String des = modelList.get(position).getDescription();
                String pric = modelList.get(position).getPrice();
                String quan = modelList.get(position).getQuantity();
                String bar = modelList.get(position).getBarcode();

                Intent intent = new Intent(cartActivity,CartUpdateActivity.class);
                intent.putExtra("cId", id);
                intent.putExtra("cBar", bar);
                intent.putExtra("cTitle", title);
                intent.putExtra("cDes", des);
                intent.putExtra("cPrice", pric);
                intent.putExtra("cQuantity", quan);

                cartActivity.startActivity(intent);*/

            }

            @Override
            public void onItemLongClick(View view, final int position) {


            }
        });


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCart holder, int position) {

        holder.titlerv.setText(modelList.get(position).getTitle());
        holder.pricerv.setText("$"+modelList.get(position).getPrice());
        holder.quantityrv.setText(modelList.get(position).getQuantity()+" Unit(s)");
        holder.timestamp.setText(modelList.get(position).getTimestamp());

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}
