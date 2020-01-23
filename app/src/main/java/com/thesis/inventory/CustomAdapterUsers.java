package com.thesis.inventory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static androidx.core.content.ContextCompat.startActivity;

public class CustomAdapterUsers extends RecyclerView.Adapter<ViewHolder> {

    UsersActivity usersActivity;
    List<Model> modelList;
    Context context;

    public CustomAdapterUsers(UsersActivity usersActivity, List<Model> modelList/*, Context context*/) {
        this.usersActivity = usersActivity;
        this.modelList = modelList;
        //this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.model_layout, viewGroup,false);

        ViewHolder viewHolder = new ViewHolder(itemView);

        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, final int position) {

                            String id = modelList.get(position).getId();
                            String bar = modelList.get(position).getBarcode();
                            String title = modelList.get(position).getTitle();
                            String des = modelList.get(position).getDescription();
                            String price = modelList.get(position).getPrice();
                            String quan = modelList.get(position).getQuantity();
                            //Toast.makeText(usersActivity, title +"\n" +des, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(usersActivity, DetailsActivity.class);
                            intent.putExtra("dId", id);
                            intent.putExtra("dBar", bar);
                            intent.putExtra("dTitle", title);
                            intent.putExtra("dDes", des);
                            intent.putExtra("dPrice", price);
                            intent.putExtra("dQuantity", quan);

                            usersActivity.startActivity(intent);


            }

            @Override
            public void onItemLongClick(View view, final int position) {



            }
        });


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.titlerv.setText(modelList.get(position).getTitle());
        holder.desrv.setText(modelList.get(position).getDescription());
        holder.pricerv.setText("Unit Price $"+modelList.get(position).getPrice());
        holder.quantityrv.setText(modelList.get(position).quantity+" Left");

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}
