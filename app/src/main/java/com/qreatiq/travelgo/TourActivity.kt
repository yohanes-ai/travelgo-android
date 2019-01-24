package com.qreatiq.travelgo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.qreatiq.travelgo.adapters.FindTourAdapter
import com.qreatiq.travelgo.adapters.PackageTourAdapter
import com.qreatiq.travelgo.objects.FindTour
import com.qreatiq.travelgo.objects.PackageTour
import com.qreatiq.travelgo.utils.Constant
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TourFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [TourFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class TourActivity : AppCompatActivity() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listView: ListView? = null
    private var button : Button? = null
    private var viewLayout : View? = null
    private var packageTours = arrayListOf<PackageTour>()
    var id: Int? = null
    private var queue: RequestQueue? = null
    private var location: TextView? = null
    private var description: TextView? = null
    private var context: Context = this

    private var user: SharedPreferences? = null
    private var userID: String? = null
    private var editor: SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_tour)

        listView = findViewById(R.id.listview) as ListView
        button = findViewById(R.id.button7) as Button
        queue = Volley.newRequestQueue(this)
        location = findViewById(R.id.textView11) as TextView
        description = findViewById(R.id.textView17) as TextView

        var extras: Intent = intent
        id=extras.getIntExtra("id",1)

        user = this.getSharedPreferences("user_id", Context.MODE_PRIVATE)
        userID = user!!.getString("user_id", "Data Not Found")

        getData()

        listView!!.setOnItemClickListener { parent, view, position, id ->
            val packageTour = packageTours[position]
            alert(packageTour.detail!!,packageTour.title ) {
                positiveButton("Beli Paket") { toast("Paket berhasil dimasukan ke keranjang") }
                negativeButton("Tutup") { }
            }.show()
        }


        button!!.setOnClickListener{
            postBooking()
        }

        listView!!.setItemsCanFocus(true)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        viewLayout = inflater!!.inflate(R.layout.fragment_tour, container, false)
//
//
//        return viewLayout
//    }

    fun getData(){
        val url = Constant.C_URL+"getDetailPackage.php?id="+id
        Log.d("data",url)


        val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
                    for(x in 0..response.getJSONObject("package").getJSONArray("detail").length()-1){
                        var data=response.getJSONObject("package").getJSONArray("detail").getJSONObject(x);
                        packageTours.add(PackageTour(
                                data.getInt("id"),
                                data.getString("name"),
                                "",
                                data.getInt("price"),
                                data.getString("description")
                        ))
                    }

                    val adapter = PackageTourAdapter(this, packageTours)
                    listView!!.adapter = adapter

                    location!!.setText(response.getJSONObject("package").getString("tour"))
                    description!!.setText(response.getJSONObject("package").getString("address"))
                },
                Response.ErrorListener { error -> Log.e("error", error.message) }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        queue!!.add(jsonObjectRequest)
    }

    fun postBooking(){

        val url = Constant.C_URL + "postBooking.php"

        val format = DecimalFormat("#,###")
        var totalBayar : Int = 0
        val jsonObject1 = JSONObject()
        val jsonArray = JSONArray()
        val arrayList = ArrayList<JSONObject>()
        for(packageTour in packageTours){
            val jsonObject = JSONObject()
            totalBayar += packageTour.qty * packageTour.price!!
            jsonObject.put("tourpack", packageTour.id)
            jsonObject.put("jumlahOrang", packageTour.qty)
            jsonObject.put("total", packageTour.qty * packageTour.price!!)
            arrayList.add(jsonObject)
        }

        for(x in 0..arrayList.size-1){
            jsonArray.put(x, arrayList.get(x))
        }
        jsonObject1.put("detail", jsonArray)

        jsonObject1.put("totalInvoice", totalBayar)
        jsonObject1.put("user", userID)

        val jsonObjectRequest = object :
            JsonObjectRequest(Request.Method.POST, url, jsonObject1,
                Response.Listener { response ->
                    Log.d("paket", response.toString())
                    alert("Total yang harus dibayarkan Rp " + format.format(totalBayar),"Konfirmasi Pembayaran") {
                        positiveButton("Bayar Paket") { toast("Paket berhasil dimasukan ke keranjang") }
                        negativeButton("Tutup") { }
                    }.show()
                },
                Response.ErrorListener { error -> Log.e("error", error.message) }
            ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        queue!!.add(jsonObjectRequest)

    }
}
