package com.qreatiq.travelgo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.support.v7.widget.Toolbar;
import android.view.*
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.qreatiq.travelgo.adapters.PlaceVisitAdapter
import com.qreatiq.travelgo.adapters.ViewPagerAdapter
import com.qreatiq.travelgo.cards.SliderAdapter
import com.qreatiq.travelgo.utils.Constant
import org.json.JSONArray
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [LocationDetailFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [LocationDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class LocationDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var viewLayout : View? = null
    var idLocation: String? = null
    private var queue: RequestQueue? = null
    private var locationName: TextView? = null
    private var locationDesc: TextView? = null

    private var loc: SharedPreferences? = null
    private var locID: String? = null
    private var editor: SharedPreferences.Editor? = null
    private var viewPager: ViewPager? = null
    private var images: ArrayList<String>? = arrayListOf()
    private var pagerAdapter: ViewPagerAdapter? = null

    var arrayPlaceVisit: ArrayList<JSONArray>? = arrayListOf()
    var placeVisitAdapter: PlaceVisitAdapter? = null;
    var placeVisitViewPager: ViewPager? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater!!.inflate(R.layout.fragment_location_detail, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        queue = Volley.newRequestQueue(activity)

        val findTourButton = view.findViewById<Button>(R.id.button5)

        viewPager = view.findViewById(R.id.pagerView) as ViewPager
        placeVisitViewPager = view.findViewById(R.id.place_visit) as ViewPager

        locationName = view.findViewById(R.id.textView6) as TextView
        locationDesc = view.findViewById(R.id.textView3) as TextView

        var toolbar : Toolbar = view.findViewById(R.id.toolbar) as Toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        (activity as AppCompatActivity).getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        (activity as AppCompatActivity).setTitle("Detail Lokasi")
        toolbar.setNavigationOnClickListener { activity!!.onBackPressed() }

        placeVisitAdapter = PlaceVisitAdapter(activity,arrayPlaceVisit)
        placeVisitViewPager!!.adapter=placeVisitAdapter

        getData()
        getPhoto()
        getPlaceVisit()

        findTourButton.setOnClickListener {
            editor!!.apply()
            activity!!.onBackPressed()
        }
    }

    fun getData(){
        val url = Constant.C_URL+"getPlaceDetail.php?id="+idLocation

        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null, Response.Listener { response ->
                locationName!!.setText(response.getJSONObject("data").getString("name"))
                locationDesc!!.setText(response.getJSONObject("data").getString("description"))

                loc = activity!!.getSharedPreferences("user_id", Context.MODE_PRIVATE)
                editor = loc!!.edit()
                editor!!.putString("location", response.getJSONObject("data").getString("name"))

//                activity1.locationDetail = response.getJSONObject("data").getString("name")
            },
            Response.ErrorListener { error -> Log.e("error", error.message) })
        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val header = HashMap<String, String>()
                header ["Content-Type"] = "application/json"
                return header
            }
        }
        queue!!.add(jsonObjectRequest)
    }

    fun getPhoto(){
        val url1 = Constant.C_URL+"getPhotoLocation.php?id="+idLocation
        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url1, null, Response.Listener { response ->
                for(x in 0..response.getJSONArray("photo").length()-1){
                    images!!.add(response.getJSONArray("photo").getJSONObject(x).getString("urlPhoto"))
                }
                pagerAdapter = ViewPagerAdapter(activity!!,images)
                viewPager!!.adapter = pagerAdapter


            },
            Response.ErrorListener { error -> Log.e("error", error.message) })
        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val header = HashMap<String, String>()
                header ["Content-Type"] = "application/json"
                return header
            }
        }


        queue!!.add(jsonObjectRequest)
    }

    fun getPlaceVisit(){
        val url1 = Constant.C_URL+"getPlaceVisit.php?id="+idLocation
        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url1, null, Response.Listener { response ->
                var json=JSONArray()
                var data=response.getJSONArray("data")
                for(x in 0..data.length()-1){
                    json.put(x%3,data.getJSONObject(x).getString("urlPhoto"))
                    if(x%3==2 || x==data.length()-1) {
                        arrayPlaceVisit!!.add(json)
                        json=JSONArray()
                    }
                }
                placeVisitAdapter!!.notifyDataSetChanged()

            },
            Response.ErrorListener { error -> Log.e("error", error.message) })
        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val header = HashMap<String, String>()
                header ["Content-Type"] = "application/json"
                return header
            }
        }


        queue!!.add(jsonObjectRequest)
    }
}
