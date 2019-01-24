package com.qreatiq.travelgo.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qreatiq.travelgo.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class HistoryTransactionAdapter extends RecyclerView.Adapter<HistoryTransactionAdapter.MyViewHolder> {
    public List<JSONObject> dataSet;
    CreateTourAdapter.ClickListener clickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //        public TextView name,account;
        public TextView location,price;
        public View v;

        public MyViewHolder(View view,int i) {
            super(view);
            location = (TextView) view.findViewById(R.id.location);
            price = (TextView) view.findViewById(R.id.price);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public void setOnItemClickListener(CreateTourAdapter.ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position);
    }

    public HistoryTransactionAdapter(List<JSONObject> dataSet) {
        this.dataSet=dataSet;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_history_transaction_item, viewGroup, false);
        return new MyViewHolder(view,i);
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        JSONObject data=dataSet.get(i);
        try {
            NumberFormat formatter = new DecimalFormat("#,###");
            myViewHolder.location.setText(data.getString("location"));
            myViewHolder.price.setText("Rp. "+formatter.format(data.getInt("price")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}