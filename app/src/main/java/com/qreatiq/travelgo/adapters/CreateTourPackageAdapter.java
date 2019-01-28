package com.qreatiq.travelgo.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qreatiq.travelgo.R;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class CreateTourPackageAdapter extends RecyclerView.Adapter<CreateTourPackageAdapter.MyViewHolder> {
    public List<JSONObject> dataSet;
    ClickListener clickListener;
    Context context;
    ProgressDialog dialog;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,description,price;
        public ImageView imageView;
        public View v;

        public MyViewHolder(View view,int i) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image);
            name = (TextView) view.findViewById(R.id.name);
            description = (TextView) view.findViewById(R.id.description);
            price = (TextView) view.findViewById(R.id.price);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(getAdapterPosition());
                }
            });
            view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.setHeaderTitle("Select The Action");
                    menu.add(0, 1, 0, "Edit");//groupId, itemId, order, title
                    menu.add(0, 2, 0, "Delete");
                    clickListener.onLongItemClick(getAdapterPosition());
                }
            });
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position);
        void onLongItemClick(int position);
    }

    public CreateTourPackageAdapter(List<JSONObject> dataSet, Context context) {
        this.dataSet=dataSet;
        this.context=context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_create_package_create_tour_item, viewGroup, false);
        dialog = new ProgressDialog(context);
        dialog.setMessage("Loading...");
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
        if(this.dataSet.size()>0) {
            dialog.show();
        }
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
        JSONObject json = dataSet.get(i);
        try {
            if(!json.isNull("image")) {
                if(!json.getBoolean("link")) {
                    myViewHolder.imageView.setImageBitmap(StringToBitMap(json.getString("image")));
                    dialog.dismiss();
                }
                else
                    Picasso.get()
                            .load(json.getString("image"))
                            .networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                            .into(myViewHolder.imageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
            }
            myViewHolder.name.setText(json.getString("name"));
            myViewHolder.description.setText((!json.getString("description").equals(""))?json.getString("description"):"-");
            NumberFormat formatter = new DecimalFormat("#,###");
            myViewHolder.price.setText("Rp. "+formatter.format(json.getDouble("price")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
