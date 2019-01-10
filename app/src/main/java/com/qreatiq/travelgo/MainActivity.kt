package com.qreatiq.travelgo

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

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

        val bottomNavigation : BottomNavigationView = findViewById(R.id.navigation)

        mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            var fragment : Fragment = fragment1
            var id : Int = 0;
            when (item.itemId) {
                R.id.navigation_home -> {
                    fragment = fragment1
                    id = R.id.navigation_home
                }
                R.id.navigation_travel -> {
                    fragment = fragment2
                    id = R.id.navigation_travel
                }
                R.id.navigation_profile -> {
                    fragment = fragment5;
                    id = R.id.navigation_profile
                }
            }
            Log.e("XXX", "XXX " + id.toString())

            var tempFragment : Fragment? = fragmentManager.findFragmentByTag(id.toString()) as Fragment?
            if(tempFragment == null){
                fragmentManager.beginTransaction().add(R.id.frame, fragment, id.toString()).addToBackStack(id.toString()).commit();
            }else{
                fragmentManager.beginTransaction().attach(tempFragment)
            }

            return@OnNavigationItemSelectedListener true
        }

        if (savedInstanceState == null) {
            Log.e("XXX", "XXX " + R.id.navigation_home.toString())
            fragmentManager.beginTransaction().replace(R.id.frame,  HomeFragment(),  R.id.navigation_home.toString()).commit();
        }

        fragmentManager.addOnBackStackChangedListener {
            var f : Fragment = fragmentManager.findFragmentById(R.id.frame)

            if(f is HomeFragment){
                bottomNavigation.menu.getItem(0).setChecked(true)
            }else if(f is FindTourFragment){
                bottomNavigation.menu.getItem(1).setChecked(true)
            }else if(f is ProfileFragment){
                bottomNavigation.menu.getItem(4).setChecked(true)
            }else if(f is TourFragment){
                bottomNavigation.menu.getItem(1).setChecked(true)
            }
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    fun <T : Fragment> Activity.findFragmentByTag(tag: String, ifNone: (String) -> T): T
            = fragmentManager.findFragmentByTag(tag) as T? ?: ifNone(tag)

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBackPressed() {
        if(fragmentManager.backStackEntryCount > 0){
            fragmentManager.popBackStack()
        }else{
            super.onBackPressed()
        }
    }
}
