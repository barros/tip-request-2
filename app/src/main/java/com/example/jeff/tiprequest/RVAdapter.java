package com.example.jeff.tiprequest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    Context mContext;
    List<ReceiptRecord> items;
    boolean editMode;

    public RVAdapter(List<ReceiptRecord> items, boolean inEditMode) {
        this.items = items;
        this.editMode = inEditMode;
    }

    @Override
    public RVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_row, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RVAdapter.ViewHolder holder, int position) {
        final ReceiptRecord receipt = items.get(position);
        String totalString = receipt.toString();
        holder.tvTotal.setText(totalString);
        String infoString;
        if (receipt.getLocation().equals("")) {
            holder.tvInfo.setText(receipt.getTime());
        } else {
            infoString = receipt.getLocation() + " - " + receipt.getTime();
            holder.tvInfo.setText(infoString);
        }
        final int index = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent map = new Intent(mContext, DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("subtotal", receipt.getSubtotal());
                bundle.putDouble("total", receipt.getTotal());
                bundle.putDouble("tipAmount", receipt.getTipAmount());
                bundle.putDouble("tipPercent", receipt.getTipPercent());
                LatLng temp = receipt.getLocLatLng();
                bundle.putDouble("lat", temp.latitude);
                bundle.putDouble("lon", temp.longitude);
                bundle.putString("location", receipt.getLocation());
                bundle.putString("time", receipt.getTime());
                map.putExtras(bundle);
                mContext.startActivity(map);
            }
        });
        if (editMode) {
            holder.ivDelete.setVisibility(View.VISIBLE);
        } else {
            holder.ivDelete.setVisibility(View.INVISIBLE);
        }
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(UserInfo.getAccountID());
                System.out.println(receipt.firebaseKey);
                System.out.println(ref);
                System.out.println(ref.child(receipt.getFirebaseKey()));
                ref.child(receipt.getFirebaseKey()).removeValue();
            }
        });

    }

    @Override
    public int getItemCount() {
        if (items == null)
            return 0;
        else
            return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvTotal;
        public TextView tvInfo;
        public ImageView ivDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTotal = itemView.findViewById(R.id.tvTip10);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}

