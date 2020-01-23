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

public class CustomAdapterRole extends RecyclerView.Adapter<AdminViewHolder> {

    RoleActivity roleActivity;
    List<AdminModel> modelList;
    Context context;

    public CustomAdapterRole(RoleActivity roleActivity, List<AdminModel> modelList/*, Context context*/) {
        this.roleActivity = roleActivity;
        this.modelList = modelList;
        //this.context = context;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.admin_model_layout, viewGroup,false);

        AdminViewHolder viewHolder = new AdminViewHolder(itemView);

        viewHolder.setOnClickListener(new AdminViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                String id = modelList.get(position).getId();
                String name = modelList.get(position).getName();
                String mobile = modelList.get(position).getMobile();
                String email = modelList.get(position).getEmail();
                String role = modelList.get(position).getRole();


                Intent intent = new Intent(roleActivity,AdminProfileActivity.class);
                intent.putExtra("update", "update");
                intent.putExtra("aId", id);
                intent.putExtra("aName", name);
                intent.putExtra("aMobile", mobile);
                intent.putExtra("aEmail", email);
                intent.putExtra("aRole", role);

                roleActivity.startActivity(intent);

            }

            @Override
            public void onItemLongClick(View view, final int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(roleActivity);

                String[] options = {"Do you want to delete Admin?", "Delete Admin"};

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == 1){

                            roleActivity.deleteData(position);

                        }

                    }
                }).create().show();
            }
        });


        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {

        holder.namerv.setText(modelList.get(position).getName());
        holder.mobilerv.setText(modelList.get(position).getMobile());
        holder.emailrv.setText(modelList.get(position).getEmail());
        holder.rolerv.setText(modelList.get(position).getRole());

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}
