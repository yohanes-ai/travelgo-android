package com.qreatiq.travelgo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap
import android.app.Activity
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.support.design.widget.BottomSheetDialog
import android.util.Base64
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import com.facebook.FacebookSdk
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.login.LoginManager
import com.qreatiq.travelgo.utils.Constant
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProfileFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private var email: EditText? = null
    private var password: EditText? = null
    private var confirm_password: EditText? = null
    private var name: EditText? = null
    private var phone: EditText? = null
    private var tour_name: EditText? = null
    private var tour_description: EditText? = null
    private var save: Button? = null
    private var create_package: Button? = null
    private var layout: ConstraintLayout? = null
    private var queue: RequestQueue? = null
    private var user: SharedPreferences? = null
    private var userID: String? = null
    private var logout: Button? = null
    private var editor: SharedPreferences.Editor? = null
    private var history: LinearLayout? = null

    var tour_photo: ImageView? = null
    var image_string: String = ""
    var link: Boolean? = false

    internal var focusListener: View.OnFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (!hasFocus)
            hideKeyboard(v)
    }
    var dialog: ProgressDialog? = null

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
        user = activity!!.getSharedPreferences("user_id", Context.MODE_PRIVATE)
        userID = user!!.getString("user_id", "Data Not Found")

        FacebookSdk.sdkInitialize(getApplicationContext());


        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name = view.findViewById(R.id.name) as EditText
        email = view.findViewById(R.id.email) as EditText
        password = view.findViewById(R.id.password) as EditText
        confirm_password = view.findViewById(R.id.confirm_password) as EditText
        phone = view.findViewById(R.id.phone) as EditText
        tour_name = view.findViewById(R.id.tour_name) as EditText
        tour_description = view.findViewById(R.id.tour_description) as EditText
        save = view.findViewById(R.id.button4) as Button
        create_package = view.findViewById(R.id.create_package) as Button
        layout = view.findViewById(R.id.frameLayout3) as ConstraintLayout
        queue = Volley.newRequestQueue(activity)
        history = view.findViewById(R.id.history) as LinearLayout
        tour_photo = view.findViewById(R.id.image) as ImageView

        if(!userID.equals("Data Not Found")){
            dialog = ProgressDialog(activity)
            dialog!!.setMessage("Loading..")
            dialog!!.show()
            getData()
        }


        save!!.setOnClickListener{
            saveData(it)
//            name!!.setFocusable(false);
//            email!!.setFocusable(false);
//            password!!.setFocusable(false);
//            confirm_password!!.setFocusable(false);
//            phone!!.setFocusable(false);
//            tour_name!!.setFocusable(false);
//            tour_description!!.setFocusable(false);
        }

        create_package!!.setOnClickListener{
            val `in` = Intent(activity, PackageActivity::class.java)
            startActivity(`in`)
        }

        logout = view!!.findViewById(R.id.logoutBtn) as Button

        logout!!.setOnClickListener{

            user = activity!!.getSharedPreferences("user_id", Context.MODE_PRIVATE)
            editor = user!!.edit()
            editor!!.clear().apply()

            FacebookSdk.sdkInitialize(activity)
            LoginManager.getInstance().logOut()

            val intent = Intent(activity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            activity!!.finish()
            startActivity(intent)

        }

        layout!!.setOnTouchListener { v, event ->
            hideKeyboard(v)
            true
        }

        history!!.setOnClickListener {
            startActivity(Intent(activity,HistoryTransactionActivity::class.java))
        }
    }

    fun getData(){
        val url = Constant.C_URL+"profile.php"

        val jsonObject = JSONObject()
        jsonObject.put("id",userID)

        val jsonObjectRequest = object :
            JsonObjectRequest(Request.Method.POST, url, jsonObject,
                Response.Listener { response ->

                    name!!.setText(if(!response.getJSONObject("user").isNull("name")) response.getJSONObject("user").getString("name") else "")
                    email!!.setText(if(!response.getJSONObject("user").isNull("email")) response.getJSONObject("user").getString("email") else "")
                    phone!!.setText(if(!response.getJSONObject("user").isNull("phone")) response.getJSONObject("user").getString("phone") else "")
                    if(!response.getJSONObject("user").isNull("name_tour")) {
                        create_package!!.visibility=View.VISIBLE
                        tour_name!!.setText(response.getJSONObject("user").getString("name_tour"))
                    }
                    if(!response.getJSONObject("user").isNull("description_tour"))
                        tour_description!!.setText(response.getJSONObject("user").getString("description_tour"))

                    if(!response.getJSONObject("user").isNull("photo_tour")){
                        link=true
                        image_string=response.getJSONObject("user").getString("photo_tour")

                        Picasso.get()
                                .load(Constant.C_URL_IMAGES+"tour/"+image_string)
                                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .into(tour_photo)
                        tour_photo!!.visibility=View.VISIBLE
                    }
                    dialog!!.dismiss()
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

    fun hideKeyboard(view: View) {
        val inputMethodManager = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun saveData(view: View){
        hideKeyboard(view)

        if(!password!!.text.toString().equals(confirm_password!!.text.toString())){
            val snackbar = Snackbar.make(view, "Confirm Password not same with Password", Snackbar.LENGTH_LONG)
            snackbar.show()
        }
        else {
            var dialog = ProgressDialog(activity)
            dialog!!.setMessage("Saving...")
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
            val url = Constant.C_URL + "saveProfile.php"

            val json = JSONObject()
            json.put("name", name!!.text.toString())
            json.put("email", email!!.text.toString())
            json.put("password", password!!.text.toString())
            json.put("phone", phone!!.text.toString())
            json.put("tour_name", tour_name!!.text.toString())
            json.put("tour_description", tour_description!!.text.toString())
            json.put("id", userID)

            json.put("tour_photo", image_string)
            json.put("link", link)

            val jsonObjectRequest = object :
                    JsonObjectRequest(Request.Method.POST, url, json,
                            Response.Listener { response ->
                                Log.d("data", response.toString())
                                if (response.getString("status").equals("berhasil")) {
                                    val snackbar = Snackbar.make(layout!!, "Profile Updated", Snackbar.LENGTH_LONG)
                                    snackbar.show()
                                } else {
                                    val snackbar = Snackbar.make(layout!!, response.getString("message"), Snackbar.LENGTH_LONG)
                                    snackbar.show()
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
            jsonObjectRequest.retryPolicy = DefaultRetryPolicy(60000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            queue!!.add(jsonObjectRequest)
        }
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
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}
