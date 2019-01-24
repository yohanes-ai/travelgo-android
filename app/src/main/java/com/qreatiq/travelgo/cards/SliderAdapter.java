package com.qreatiq.travelgo.cards;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.qreatiq.travelgo.R;
import com.qreatiq.travelgo.utils.Constant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SliderAdapter extends RecyclerView.Adapter<SliderCard> {

    private final int count;
    private final ArrayList<String> content;
    private final View.OnClickListener listener;
    private ClickListener clickListener;

    public void setOnItemClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    public interface ClickListener{
        void onItemClick(int position, View v);
    }

    public SliderAdapter(ArrayList<String> content, int count, View.OnClickListener listener) {
        this.content = content;
        this.count = count;
        this.listener = listener;
    }

    @Override
    public SliderCard onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.layout_slider_card, parent, false);

        return new SliderCard(view);
    }

    @Override
    public void onBindViewHolder(final SliderCard holder, int position) {
        if (listener != null) {
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(holder.getAdapterPosition(), view);
                }
            });

        Picasso.get().load(Constant.Companion.getC_URL_IMAGES()+"location/"+content.get(position)).into(holder.imageView);
        }
    }

    @Override
    public void onViewRecycled(SliderCard holder) {
        holder.clearContent();
    }

    @Override
    public int getItemCount() {
        return content.size();
    }

}