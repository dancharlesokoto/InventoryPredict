package com.thesis.inventory;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolderCart extends RecyclerView.ViewHolder {

    TextView titlerv, quantityrv, pricerv,timestamp;
    View mView;


    public ViewHolderCart(@NonNull View itemView) {
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

        titlerv = itemView.findViewById(R.id.utitlerv);
        pricerv = itemView.findViewById(R.id.upricerv);
        quantityrv = itemView.findViewById(R.id.uquantityrv);
        timestamp = itemView.findViewById(R.id.utimerv);
    }

    private  ViewHolderCart.ClickListener mClickListener;
    public interface ClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);


    }

    public void setOnClickListener(ViewHolderCart.ClickListener clickListener){

        mClickListener = clickListener;
    }
}
