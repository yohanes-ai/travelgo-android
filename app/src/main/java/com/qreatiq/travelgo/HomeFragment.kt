package com.qreatiq.travelgo


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StyleRes
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.*
import com.qreatiq.travelgo.cards.SliderAdapter
import com.qreatiq.travelgo.utils.DecodeBitmapTask
import com.ramotion.cardslider.CardSliderLayoutManager
import com.ramotion.cardslider.CardSnapHelper
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private val dotCoords = Array(5) { IntArray(2) }
    private val pics = intArrayOf(R.drawable.p1, R.drawable.p2, R.drawable.p3, R.drawable.p4, R.drawable.p5)
    private val maps = intArrayOf(
        R.drawable.map_paris,
        R.drawable.map_seoul,
        R.drawable.map_london,
        R.drawable.map_beijing,
        R.drawable.map_greece
    )
    private val descriptions =
        intArrayOf(R.string.text1, R.string.text2, R.string.text3, R.string.text4, R.string.text5)
    private val countries = arrayOf("PARIS", "SEOUL", "LONDON", "BEIJING", "THIRA")
    private val places = arrayOf("The Louvre", "Gwanghwamun", "Tower Bridge", "Temple of Heaven", "Aegeana Sea")
    private val temperatures = arrayOf("21°C", "19°C", "17°C", "23°C", "20°C")
    private val times =
        arrayOf("Aug 1 - Dec 15    7:00-18:00", "Sep 5 - Nov 10    8:00-16:00", "Mar 8 - May 21    7:00-18:00")

    private val sliderAdapter = SliderAdapter(pics, 20, OnCardClickListener())

    private lateinit var layoutManger: CardSliderLayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var mapSwitcher: ImageSwitcher
    private lateinit var temperatureSwitcher: TextSwitcher
    private lateinit var placeSwitcher: TextSwitcher
    private lateinit var clockSwitcher: TextSwitcher
    private lateinit var descriptionsSwitcher: TextSwitcher
    private lateinit var greenDot: View

    private lateinit var viewOfLayout: View

    private lateinit var country1TextView: TextView
    private lateinit var country2TextView: TextView
    private var countryOffset1: Int = 0
    private var countryOffset2: Int = 0
    private var countryAnimDuration: Long = 0
    private var currentPosition: Int = 0

    private lateinit var decodeMapBitmapTask: DecodeBitmapTask
    private lateinit var mapLoadListener: DecodeBitmapTask.Listener

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
        viewOfLayout = inflater!!.inflate(R.layout.fragment_home, container, false)

        initRecyclerView()
        initCountryText()
        initSwitchers()
        initGreenDot()

        return viewOfLayout
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
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun initRecyclerView() {
        var recyclerView : RecyclerView
        recyclerView = viewOfLayout.findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.setAdapter(sliderAdapter)
        recyclerView.setHasFixedSize(true)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onActiveCardChange()
                }
            }
        })

        layoutManger = recyclerView.getLayoutManager() as CardSliderLayoutManager

        CardSnapHelper().attachToRecyclerView(recyclerView)
    }

    override fun onPause() {
        super.onPause()
        if (isRemoving && this::decodeMapBitmapTask.isInitialized && decodeMapBitmapTask != null) {
            decodeMapBitmapTask.cancel(true)
        }
    }

    private fun initSwitchers() {
        temperatureSwitcher = viewOfLayout.findViewById(R.id.ts_temperature) as TextSwitcher
        temperatureSwitcher.setFactory(TextViewFactory(R.style.TemperatureTextView, true))
        temperatureSwitcher.setCurrentText(temperatures[0])

        placeSwitcher = viewOfLayout.findViewById(R.id.ts_place) as TextSwitcher
        placeSwitcher.setFactory(TextViewFactory(R.style.PlaceTextView, false))
        placeSwitcher.setCurrentText(places[0])

        clockSwitcher = viewOfLayout.findViewById(R.id.ts_clock) as TextSwitcher
        clockSwitcher.setFactory(TextViewFactory(R.style.ClockTextView, false))
        clockSwitcher.setCurrentText(times[0])

        descriptionsSwitcher = viewOfLayout.findViewById(R.id.ts_description) as TextSwitcher
        descriptionsSwitcher.setInAnimation(context, android.R.anim.fade_in)
        descriptionsSwitcher.setOutAnimation(context, android.R.anim.fade_out)
        descriptionsSwitcher.setFactory(TextViewFactory(R.style.DescriptionTextView, false))
        descriptionsSwitcher.setCurrentText(getString(descriptions[0]))

        mapSwitcher = viewOfLayout.findViewById(R.id.ts_map) as ImageSwitcher
        mapSwitcher.setInAnimation(context, R.anim.fade_in)
        mapSwitcher.setOutAnimation(context, R.anim.fade_out)
        mapSwitcher.setFactory(ImageViewFactory())
        mapSwitcher.setImageResource(maps[0])

        mapLoadListener = object : DecodeBitmapTask.Listener{
            override fun onPostExecuted(bitmap: Bitmap) {
                (mapSwitcher.nextView as ImageView).setImageBitmap(bitmap)
                mapSwitcher.showNext()
            }
        }
    }

    private fun initCountryText() {
        countryAnimDuration = resources.getInteger(R.integer.labels_animation_duration).toLong()
        countryOffset1 = resources.getDimensionPixelSize(R.dimen.left_offset)
        countryOffset2 = resources.getDimensionPixelSize(R.dimen.card_width)
        country1TextView = viewOfLayout.findViewById(R.id.tv_country_1) as TextView
        country2TextView = viewOfLayout.findViewById(R.id.tv_country_2) as TextView

        country1TextView.setX(countryOffset1.toFloat())
        country2TextView.setX(countryOffset2.toFloat())
        country1TextView.setText(countries[0])
        country2TextView.setAlpha(0f)

        country1TextView!!.typeface = Typeface.createFromAsset(context!!.assets, "open-sans-extrabold.ttf")
        country2TextView!!.typeface = Typeface.createFromAsset(context!!.assets, "open-sans-extrabold.ttf")
    }

    private fun initGreenDot() {
        mapSwitcher.getViewTreeObserver().addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mapSwitcher.getViewTreeObserver().removeOnGlobalLayoutListener(this)

                val viewLeft = mapSwitcher.getLeft()
                val viewTop = mapSwitcher.getTop() + mapSwitcher.getHeight() / 3

                val border = 100
                val xRange = Math.max(1, mapSwitcher.getWidth() - border * 2)
                val yRange = Math.max(1, mapSwitcher.getHeight() / 3 * 2 - border * 2)

                val rnd = Random()

                var i = 0
                val cnt = dotCoords.size
                while (i < cnt) {
                    dotCoords[i][0] = viewLeft + border + rnd.nextInt(xRange)
                    dotCoords[i][1] = viewTop + border + rnd.nextInt(yRange)
                    i++
                }

                greenDot = viewOfLayout.findViewById(R.id.green_dot)
                greenDot.setX(dotCoords[0][0].toFloat())
                greenDot.setY(dotCoords[0][1].toFloat())
            }
        })
    }

    private fun setCountryText(text: String, left2right: Boolean) {
        val invisibleText: TextView
        val visibleText: TextView
        if (country1TextView.getAlpha() > country2TextView.getAlpha()) {
            visibleText = country1TextView
            invisibleText = country2TextView
        } else {
            visibleText = country2TextView
            invisibleText = country1TextView
        }

        val vOffset: Int
        if (left2right) {
            invisibleText.x = 0f
            vOffset = countryOffset2
        } else {
            invisibleText.x = countryOffset2.toFloat()
            vOffset = 0
        }

        invisibleText.text = text

        val iAlpha = ObjectAnimator.ofFloat(invisibleText, "alpha", 1f)
        val vAlpha = ObjectAnimator.ofFloat(visibleText, "alpha", 0f)

        val iX = ObjectAnimator.ofFloat(invisibleText, "x", countryOffset1.toFloat())
        val vX = ObjectAnimator.ofFloat(visibleText, "x", vOffset.toFloat())

        val animSet = AnimatorSet()
        animSet.playTogether(iAlpha, vAlpha, iX, vX)
        animSet.duration = countryAnimDuration
        animSet.start()
    }

    private fun onActiveCardChange() {
        val pos = layoutManger.getActiveCardPosition()
        if (pos == RecyclerView.NO_POSITION || pos == currentPosition) {
            return
        }

        onActiveCardChange(pos)
    }

    private fun onActiveCardChange(pos: Int) {
        val animH = intArrayOf(R.anim.slide_in_right, R.anim.slide_out_left)
        val animV = intArrayOf(R.anim.slide_in_top, R.anim.slide_out_bottom)

        val left2right = pos < currentPosition
        if (left2right) {
            animH[0] = R.anim.slide_in_left
            animH[1] = R.anim.slide_out_right

            animV[0] = R.anim.slide_in_bottom
            animV[1] = R.anim.slide_out_top
        }

        setCountryText(countries[pos % countries.size], left2right)

        temperatureSwitcher.setInAnimation(context, animH[0])
        temperatureSwitcher.setOutAnimation(context, animH[1])
        temperatureSwitcher.setText(temperatures[pos % temperatures.size])

        placeSwitcher.setInAnimation(context, animV[0])
        placeSwitcher.setOutAnimation(context, animV[1])
        placeSwitcher.setText(places[pos % places.size])

        clockSwitcher.setInAnimation(context, animV[0])
        clockSwitcher.setOutAnimation(context, animV[1])
        clockSwitcher.setText(times[pos % times.size])

        descriptionsSwitcher.setText(getString(descriptions[pos % descriptions.size]))

        showMap(maps[pos % maps.size])

        ViewCompat.animate(greenDot)
            .translationX(dotCoords[pos % dotCoords.size][0].toFloat())
            .translationY(dotCoords[pos % dotCoords.size][1].toFloat())
            .start()

        currentPosition = pos
    }

    private fun showMap(@DrawableRes resId: Int) {
        if (this::decodeMapBitmapTask.isInitialized && decodeMapBitmapTask != null) {
            decodeMapBitmapTask.cancel(true)
        }

        val w = mapSwitcher.getWidth()
        val h = mapSwitcher.getHeight()

        decodeMapBitmapTask = DecodeBitmapTask(resources, resId, w, h, mapLoadListener)
        decodeMapBitmapTask.execute()
    }

    private inner class TextViewFactory internal constructor(
        @param:StyleRes @field:StyleRes internal val styleId: Int, internal val center: Boolean
    ) :
        ViewSwitcher.ViewFactory {

        override fun makeView(): View {
            val textView = TextView(context)

            if (center) {
                textView.gravity = Gravity.CENTER
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                textView.setTextAppearance(context, styleId)
            } else {
                textView.setTextAppearance(styleId)
            }

            return textView
        }

    }

    private inner class ImageViewFactory : ViewSwitcher.ViewFactory {
        override fun makeView(): View {
            val imageView = ImageView(context)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP

            val lp =
                FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            imageView.layoutParams = lp

            return imageView
        }
    }

    private inner class OnCardClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            val lm = recyclerView.getLayoutManager() as CardSliderLayoutManager?

            if (lm!!.isSmoothScrolling) {
                return
            }

            val activeCardPosition = lm.activeCardPosition
            if (activeCardPosition == RecyclerView.NO_POSITION) {
                return
            }

            val clickedPosition = recyclerView.getChildAdapterPosition(view)
            if (clickedPosition == activeCardPosition) {
                val intent = Intent(context, DetailsActivity::class.java)
                intent.putExtra(DetailsActivity.BUNDLE_IMAGE_ID, pics[activeCardPosition % pics.size])

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent)
                } else {
                    val cardView = view as CardView
                    val sharedView = cardView.getChildAt(cardView.childCount - 1)
                    val options = ActivityOptions.makeSceneTransitionAnimation(activity, sharedView, "shared")
                    startActivity(intent, options.toBundle())
                }
            } else if (clickedPosition > activeCardPosition) {
                recyclerView.smoothScrollToPosition(clickedPosition)
                onActiveCardChange(clickedPosition)
            }
        }
    }
}
