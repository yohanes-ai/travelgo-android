package com.qreatiq.travelgo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.qreatiq.travelgo.R
import com.qreatiq.travelgo.objects.PackageTour
import com.squareup.picasso.Picasso

class PackageTourAdapter(private val context : Context, private val dataSource : ArrayList<PackageTour>) : BaseAdapter(){
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.list_item_package_tour, parent, false)

            holder = ViewHolder()
            holder.thumbnailImageView = view.findViewById<ImageView>(R.id.imageView5)
            holder.titleTextView = view.findViewById<TextView>(R.id.textView9)
            holder.priceTextView = view.findViewById<TextView>(R.id.textView21)
            holder.detailTextView = view.findViewById<TextView>(R.id.textView10)

            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val titleTextView = holder.titleTextView
        val priceTextView = holder.priceTextView
        val contentTextView = holder.detailTextView
        val thumbnailImageView = holder.thumbnailImageView

        val tour = getItem(position) as PackageTour

        titleTextView.text = tour.title
        priceTextView.text = "Rp " + tour.price.toString()
        contentTextView.text = tour.content
        Picasso.get().load(tour.imageURL).placeholder(R.mipmap.ic_launcher).into(thumbnailImageView)

        return view
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    private class ViewHolder {
        lateinit var titleTextView: TextView
        lateinit var detailTextView: TextView
        lateinit var priceTextView: TextView
        lateinit var thumbnailImageView: ImageView
    }
}