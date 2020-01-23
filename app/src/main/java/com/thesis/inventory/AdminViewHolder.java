package com.thesis.inventory;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdminViewHolder extends RecyclerView.ViewHolder {

    TextView namerv, mobilerv, emailrv, rolerv;
    View mView;


    public AdminViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mClickListener.onItemClick(v, getAdapterPosition());
                return;
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onItemLongClick(v, getAdapterPosition());
                return true;
            }
        });

        namerv = itemView.findViewById(R.id.namerv);
        mobilerv = itemView.findViewById(R.id.mobilerv);
        emailrv = itemView.findViewById(R.id.emailrv);
        rolerv = itemView.findViewById(R.id.rolerv);
    }

    private  AdminViewHolder.ClickListener mClickListener;
    public interface ClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);


    }

    public void setOnClickListener(AdminViewHolder.ClickListener clickListener){

        mClickListener = clickListener;
    }
}
