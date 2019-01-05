package com.qreatiq.travelgo

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Spinner
import android.view.*
import android.widget.ArrayAdapter
import com.qreatiq.travelgo.adapters.FindTourAdapter
import com.qreatiq.travelgo.objects.FindTour


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
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var listView: ListView
    private lateinit var spinner : Spinner
    private lateinit var viewLayout : View
    private var listener: OnFragmentInteractionListener? = null

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

        var findTours = arrayListOf<FindTour>()
        for(i in 1..6){
            var findTour : FindTour = FindTour(0, i.toString() + "D2N" , "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", "https://i.imgur.com/zZSwAwH.png")
            findTours.add(findTour)

        }

        val adapter = FindTourAdapter(context!!, findTours)
        listView.adapter = adapter
        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedTour = findTours[position]
        }

        var cities = arrayListOf<String>()
        cities.add("Bali")
        cities.add("Jakarta")
        cities.add("Medan")
        cities.add("Pekanbaru")
        cities.add("Aceh")
        cities.add("Surabaya")

        val adapterSpinner = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, cities)
        spinner.adapter = adapterSpinner

        return viewLayout
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
