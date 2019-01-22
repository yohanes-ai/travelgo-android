package com.qreatiq.travelgo.cards;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.qreatiq.travelgo.R;

public class SliderAdapter extends RecyclerView.Adapter<SliderCard> {

    private final int count;
    private final int[] content;
    private final View.OnClickListener listener;
    private ClickListener clickListener;

    public void setOnItemClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    public interface ClickListener{
        void onItemClick(int position, View v);
    }

    public SliderAdapter(int[] content, int count, View.OnClickListener listener) {
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
        }
        holder.setContent(content[position % content.length]);
    }

    @Override
    public void onViewRecycled(SliderCard holder) {
        holder.clearContent();
    }

    @Override
    public int getItemCount() {
        return count;
    }

}
