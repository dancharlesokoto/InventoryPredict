package com.thesis.inventory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapterPred extends RecyclerView.Adapter<ViewHolder> {

    PredictActivity predictActivity;
    List<ProModel> modelList;
    Context context;

    public CustomAdapterPred(PredictActivity predictActivity, List<ProModel> modelList/*, Context context*/) {
        this.predictActivity = predictActivity;
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
            public void onItemClick(View view, int position) {

                String id = modelList.get(position).getId();
                String title = modelList.get(position).getTitle();
                String des = modelList.get(position).getDescription();
                String pric = modelList.get(position).getPrice();
                String quan = modelList.get(position).getQuantity();
                String bar = modelList.get(position).getBarcode();
                String prono = modelList.get(position).getProno();

                Intent intent = new Intent(predictActivity,SeverActivity.class);
                intent.putExtra("zId", id);
                intent.putExtra("zBar", bar);
                intent.putExtra("zTitle", title);
                intent.putExtra("zDes", des);
                intent.putExtra("zPrice", pric);
                intent.putExtra("zQuantity", quan);
                intent.putExtra("zProno", prono);

                predictActivity.startActivity(intent);

            }

            @Override
            public void onItemLongClick(View view, final int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(predictActivity);

                String[] options = {"Do you want to delete Product?", "Delete Product Record"};

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == 1){

                            predictActivity.deleteData(position);

                        }

                    }
                }).create().show();
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
