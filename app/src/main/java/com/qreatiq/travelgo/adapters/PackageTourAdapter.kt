package com.qreatiq.travelgo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.qreatiq.travelgo.R
import com.qreatiq.travelgo.objects.PackageTour
import com.squareup.picasso.Picasso
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.text.DecimalFormat

class PackageTourAdapter(private val context : Context, private val dataSource : ArrayList<PackageTour>) : BaseAdapter(){
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val format = DecimalFormat("#,###")
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
            holder.addButton = view.findViewById<ImageButton>(R.id.imageButton2)
            holder.minButton = view.findViewById<ImageButton>(R.id.imageButton3)
            holder.packEditText = view.findViewById<EditText>(R.id.editText8)

            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val tour = getItem(position) as PackageTour

        holder.titleTextView.text = tour.title
        holder.priceTextView.text = "Rp " + format.format(tour.price)
        holder.detailTextView.text = tour.content
        if(!tour.imageURL.equals(""))
            Picasso.get().load(tour.imageURL).placeholder(R.mipmap.ic_launcher).into(holder.thumbnailImageView)
        holder.packEditText.setText(tour.qty.toString())

        holder.addButton.setOnClickListener(){
            var data : Int = holder.packEditText.text.toString().toInt() + 1
            holder.packEditText.setText(data.toString())
            dataSource[position].qty = data
        }

        holder.minButton.setOnClickListener(){
            var data : Int = holder.packEditText.text.toString().toInt() - 1
            if(data >= 0){
                holder.packEditText.setText((data).toString())
                dataSource[position].qty = data
            }
        }

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
        lateinit var addButton : ImageButton
        lateinit var minButton : ImageButton
        lateinit var packEditText: EditText
    }
}