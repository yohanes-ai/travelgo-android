package com.qreatiq.travelgo

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.*
import android.webkit.WebView
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.qreatiq.travelgo.adapters.FindTourAdapter
import com.qreatiq.travelgo.cards.SliderAdapter
import com.qreatiq.travelgo.objects.FindTour
import com.qreatiq.travelgo.utils.Constant
import org.jetbrains.anko.support.v4.act
import org.json.JSONObject
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FindTourFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FindTourFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FindTourFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = ""
    private var param2: String? = ""
    private lateinit var listView: ListView
    private lateinit var spinner : Spinner
    private lateinit var viewLayout : View
    private var listener: OnFragmentInteractionListener? = null
    private val datePicker: DatePicker? = null
    private val calendar: Calendar? = Calendar.getInstance()
    private val year: Int = 0
    private val month: Int = 0
    private val day: Int = 0
    private var date: EditText? = null
    private var queue: RequestQueue? = null
    private var cities = arrayListOf<String>()
    private var findTours = arrayListOf<FindTour>()
    var prefs: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var no_tour: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewLayout = inflater!!.inflate(R.layout.fragment_find_tour, container, false);
        listView = viewLayout!!.findViewById(R.id.listview)
        spinner = viewLayout!!.findViewById(R.id.spinner)
        date = viewLayout.findViewById(R.id.editText2) as EditText
        queue = Volley.newRequestQueue(activity)
        no_tour = viewLayout!!.findViewById(R.id.no_tour) as TextView

//        prefs = activity!!.getSharedPreferences("adas",0)

        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedTour = findTours[position]

            val in1 = Intent(activity, TourActivity::class.java)
            in1.putExtra("id",selectedTour.id)
            startActivity(in1)

//            val fragmentManager = getFragmentManager()
//            val fragment: TourFragment = TourFragment()
//            fragment.id=findTours.get(position).id
//            val activity1 = activity as MainActivity?
//            activity1!!.fragmentCurr=TourFragment()
//            getFragmentManager()!!.beginTransaction().replace(R.id.frame, fragment)
//                    .addToBackStack(R.id.navigation_home.toString()).commit();
        }

        prefs = activity!!.getSharedPreferences("user_id", Context.MODE_PRIVATE)
        editor = prefs!!.edit()

        val url = Constant.C_URL+"getPlaces.php"

        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null, Response.Listener { response ->
                for(location in 0..response.getJSONArray("data").length()-1){
                    cities.add(response.getJSONArray("data").getJSONObject(location).getString("name"))
                }

                val adapterSpinner = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, cities)
                spinner.adapter = adapterSpinner

                if(!prefs!!.getString("location","").equals(""))
                    spinner.setSelection(cities.indexOf(prefs!!.getString("location",null)))

                editor!!.remove("location")
                editor!!.commit()
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

        val myDateListener = DatePickerDialog.OnDateSetListener { arg0, arg1, arg2, arg3 ->
            calendar!!.set(Calendar.YEAR, arg1);
            calendar!!.set(Calendar.MONTH, arg2);
            calendar!!.set(Calendar.DAY_OF_MONTH, arg3);
            showDate(arg1, arg2 + 1, arg3)
        }

        date!!.setOnClickListener{
            var dialog = DatePickerDialog(
                activity,
                myDateListener,
                calendar!!.get(Calendar.YEAR),
                calendar!!.get(Calendar.MONTH),
                calendar!!.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.minDate=System.currentTimeMillis() - 1000;
            dialog.show()
        }

        var toolbar : Toolbar = viewLayout!!.findViewById<Toolbar>(R.id.job_list_toolbar);
        (activity as AppCompatActivity).setSupportActionBar(toolbar);
        if((activity as AppCompatActivity).getSupportActionBar()!=null) {
            (activity as AppCompatActivity).getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
            (activity as AppCompatActivity).getSupportActionBar()?.setDisplayShowHomeEnabled(true);
            (activity as AppCompatActivity).getSupportActionBar()?.setTitle("Pilih Tour")
            toolbar.setNavigationOnClickListener { activity!!.onBackPressed() }
        }

        return viewLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        date!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(!date!!.text.toString().equals(""))
                    getData()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if(!date!!.text.toString().equals(""))
                    getData()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        })
    }

    private fun showDate(year: Int, month: Int, day: Int) {
        date!!.setText(
            StringBuilder().append(day).append("/")
                .append(month).append("/").append(year)
        )
    }

    fun getData(){
        var dialog = ProgressDialog(activity)
        dialog!!.setMessage("Loading...")
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        val url = Constant.C_URL+"getPackage.php"

        val json = JSONObject()
        json.put("location",cities.get(spinner.selectedItemPosition))
        json.put("date",date!!.text.toString())
//        Log.d("data",json.toString())

        val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST, url, json,
                Response.Listener { response ->
//                    Log.d("responseData", response.toString())
                    findTours.clear()
                    if(response.getJSONArray("user").length()>0) {
                        for (x in 0..response.getJSONArray("user").length() - 1) {
                            var data = response.getJSONArray("user").getJSONObject(x)
                            var findTour: FindTour = FindTour(data.getInt("id"), data.getString("tour"), data.getString("date_start") + " - " + data.getString("date_end"),
                                Constant.C_URL_IMAGES+"tour/"+data.getString("photo"))
                            findTours.add(findTour)
                        }

                        val adapter = FindTourAdapter(context!!, findTours)
                        listView.adapter = adapter
                        no_tour!!.visibility = View.GONE
                    }
                    else{
                        no_tour!!.visibility = View.VISIBLE
                    }

                    dialog.dismiss()
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

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FindTourFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FindTourFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
