package com.qreatiq.travelgo

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StyleRes
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import com.qreatiq.travelgo.cards.SliderAdapter
import com.qreatiq.travelgo.utils.DecodeBitmapTask
import com.ramotion.cardslider.CardSliderLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import com.qreatiq.travelgo.R
import com.ramotion.cardslider.CardSnapHelper
import java.util.*

class MainActivity : AppCompatActivity(),
    LocationDetailFragment.OnFragmentInteractionListener,
    HomeFragment.OnFragmentInteractionListener,
    FindTourFragment.OnFragmentInteractionListener,
    ProfileFragment.OnFragmentInteractionListener,
    TourFragment.OnFragmentInteractionListener{

    private val fragmentManager = getSupportFragmentManager()
    private lateinit var mOnNavigationItemSelectedListener : BottomNavigationView.OnNavigationItemSelectedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment1 : Fragment = HomeFragment()
        val fragment2 : Fragment = FindTourFragment()
        var fragment5 : Fragment = ProfileFragment()

        mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            var fragment : Fragment = fragment1
            when (item.itemId) {
                R.id.navigation_home -> {
                    fragment = fragment1;
                    fragmentManager.beginTransaction().replace(R.id.frame, fragment).addToBackStack(R.id.navigation_home.toString()).commit();
                }
                R.id.navigation_travel -> {
                    fragment = fragment2;
                    fragmentManager.beginTransaction().replace(R.id.frame, fragment).addToBackStack(R.id.navigation_travel.toString()).commit();
                }
                R.id.navigation_profile -> {
                    fragment = fragment5;
                    fragmentManager.beginTransaction().replace(R.id.frame, fragment).addToBackStack(R.id.navigation_profile.toString()).commit();
                }
            }
            return@OnNavigationItemSelectedListener true
        }

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().replace(R.id.frame,  HomeFragment()).commit();
        }

        fragmentManager.addOnBackStackChangedListener {
            var f : Fragment = fragmentManager.findFragmentById(R.id.frame)
            if(f is HomeFragment){
                navigation.selectedItemId = R.id.navigation_home
            }
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBackPressed() {
        if(fragmentManager.backStackEntryCount > 0){
            fragmentManager.popBackStackImmediate()
        }else{
            super.onBackPressed()
        }
    }
}
