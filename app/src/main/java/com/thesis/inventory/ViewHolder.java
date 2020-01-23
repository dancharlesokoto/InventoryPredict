package com.thesis.inventory;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView titlerv, desrv, quantityrv, pricerv;
    View mView;


    public ViewHolder(@NonNull View itemView) {
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

        titlerv = itemView.findViewById(R.id.titlerv);
        desrv = itemView.findViewById(R.id.desrv);
        pricerv = itemView.findViewById(R.id.pricerv);
        quantityrv = itemView.findViewById(R.id.quantityrv);
    }

    private  ViewHolder.ClickListener mClickListener;
    public interface ClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);


    }

    public void setOnClickListener(ViewHolder.ClickListener clickListener){

        mClickListener = clickListener;
    }
}
