package com.qreatiq.travelgo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.qreatiq.travelgo.cards.SliderAdapter
import org.w3c.dom.Text
import java.util.HashMap


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
class LocationDetailFragment : AppCompatActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_location_detail)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
        queue = Volley.newRequestQueue(this)
        var extras: Intent = intent
        idLocation=extras.getStringExtra("id")

        val findTourButton = findViewById<Button>(R.id.button5)

        val url = "https://3gomedia.com/travel-go/api/getPlaceDetail.php?id="+idLocation

        locationName = findViewById(R.id.textView6) as TextView
        locationDesc = findViewById(R.id.textView3) as TextView


        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null, Response.Listener { response ->
                locationName!!.setText(response.getJSONObject("data").getString("name"))
                locationDesc!!.setText(response.getJSONObject("data").getString("description"))

                loc = getSharedPreferences("user_id", Context.MODE_PRIVATE)
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

        findTourButton.setOnClickListener {
            editor!!.apply()
            onBackPressed()
//            val fragmentManager = getFragmentManager()
//            val fragment: Fragment = FindTourFragment()
//            fragmentManager!!.beginTransaction().replace(R.id.frame, fragment)
//                .addToBackStack(R.id.navigation_home.toString()).commit();
        }
    }
}
