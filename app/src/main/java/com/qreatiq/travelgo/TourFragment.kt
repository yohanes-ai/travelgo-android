package com.qreatiq.travelgo

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.qreatiq.travelgo.adapters.PackageTourAdapter
import com.qreatiq.travelgo.objects.PackageTour
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast

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
class TourFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var listView: ListView
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
        viewLayout = inflater!!.inflate(R.layout.fragment_tour, container, false)
        listView = viewLayout!!.findViewById(R.id.listview)

        var packageTours = arrayListOf<PackageTour>()
        var packageTour1 = PackageTour(1, "Half Day Activity", "Belum termasuk tiket pesawat", "https://i.imgur.com/zZSwAwH.png", 1500000, "Termasuk harga tiket masuk Taman Air Nirwana, Air Mancur Indah")
        var packageTour2 = PackageTour(1, "Full Day Activity", "Belum termasuk tiket pesawat", "https://i.imgur.com/zZSwAwH.png", 2000000, "Termasuk harga penginapan, tiket masuk Taman Air Nirwana, Air Mancur Indah")

        packageTours.add(packageTour1)
        packageTours.add(packageTour2)

        val adapter = PackageTourAdapter(context!!, packageTours)
        listView.adapter = adapter
        listView.setOnItemClickListener { parent, view, position, id ->
            val packageTour = packageTours[position]
            alert(packageTour.detail!!,packageTour.title ) {
                positiveButton("Beli Paket") { toast("Paket berhasil dimasukan ke keranjang") }
                negativeButton("Tutup") { }
            }.show()
        }

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
         * @return A new instance of fragment TourFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TourFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
