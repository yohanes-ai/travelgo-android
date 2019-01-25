package com.qreatiq.travelgo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qreatiq.travelgo.R;
import com.qreatiq.travelgo.utils.Constant;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class PlaceVisitAdapter extends PagerAdapter {

    private Context context;
    ArrayList<JSONArray> images;
    public PlaceVisitAdapter(Context context, ArrayList<JSONArray> images){
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater  = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.list_place_visit_item, null);
        ImageView image1 = (ImageView) view.findViewById(R.id.image1);
        ImageView image2 = (ImageView) view.findViewById(R.id.image2);
        ImageView image3 = (ImageView) view.findViewById(R.id.image3);
        try {
            Picasso.get()
                    .load(Constant.Companion.getC_URL_IMAGES()+"visit_place/"+images.get(position).getString(0))
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(image1);
            if(!images.get(position).isNull(1))
            Picasso.get()
                    .load(Constant.Companion.getC_URL_IMAGES()+"visit_place/"+images.get(position).getString(1))
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(image2);
            if(!images.get(position).isNull(2))
            Picasso.get()
                    .load(Constant.Companion.getC_URL_IMAGES()+"visit_place/"+images.get(position).getString(2))
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(image3);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}
