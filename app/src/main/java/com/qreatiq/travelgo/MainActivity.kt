package com.qreatiq.travelgo

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import android.util.SparseArray
import java.util.*


class MainActivity : AppCompatActivity(),
    HomeFragment.OnFragmentInteractionListener,
    FindTourFragment.OnFragmentInteractionListener,
    ProfileFragment.OnFragmentInteractionListener{

    private val fragmentManager = getSupportFragmentManager()
    private lateinit var mOnNavigationItemSelectedListener : BottomNavigationView.OnNavigationItemSelectedListener
    private val TAG_HOME = "HOME"
    private val TAG_FIND_TOUR = "FIND_TOUR"
    private val TAG_PROFILE = "PROFILE"
    private var myFragments:LinkedList<Fragment> = LinkedList()
    private var fragmentCurr : Fragment = Fragment()
    var locationDetail : String = ""
    private var prefs: SharedPreferences? = null
    var bottomNavigation : BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.navigation) as BottomNavigationView

        val fragmentHome : Fragment = HomeFragment()
        val fragmentFindTour : Fragment = FindTourFragment()
        val fragmentProfile : Fragment = ProfileFragment()

        mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    fragmentManager.beginTransaction().replace(R.id.frame, HomeFragment()).commit()
                    fragmentCurr = fragmentHome
                    myFragments.push(fragmentHome)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_location -> {
                    fragmentManager.beginTransaction().replace(R.id.frame, FindTourFragment()).commit()
                    fragmentCurr = fragmentFindTour
                    myFragments.push(fragmentFindTour)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    fragmentManager.beginTransaction().replace(R.id.frame, ProfileFragment()).commit()
                    fragmentCurr = fragmentProfile
                    myFragments.push(fragmentProfile)
                    return@OnNavigationItemSelectedListener true
                }
            }

            return@OnNavigationItemSelectedListener false
        }

        prefs = getSharedPreferences("user_id", Context.MODE_PRIVATE)
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().add(R.id.frame, fragmentProfile, TAG_PROFILE).hide(fragmentProfile).commit()
            fragmentManager.beginTransaction().add(R.id.frame, fragmentFindTour, TAG_FIND_TOUR).hide(fragmentFindTour).commit()
            fragmentManager.beginTransaction().add(R.id.frame, fragmentHome, TAG_HOME).commit()

            fragmentCurr = fragmentHome
            myFragments.push(fragmentHome)
        }

        fragmentManager.addOnBackStackChangedListener {
            var f : Fragment = fragmentManager.findFragmentById(R.id.frame)

            if(f is HomeFragment){
                bottomNavigation!!.menu.getItem(0).setChecked(true)
            }else if(f is FindTourFragment){
                bottomNavigation!!.menu.getItem(1).setChecked(true)
            }else if(f is ProfileFragment){
                bottomNavigation!!.menu.getItem(4).setChecked(true)
            }
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    fun <T : Fragment> Activity.findFragmentByTag(tag: String, ifNone: (String) -> T): T
            = fragmentManager.findFragmentByTag(tag) as T? ?: ifNone(tag)

    override fun onResume() {
        super.onResume()

        if(!prefs!!.getString("location","").equals("")) {
            supportFragmentManager.beginTransaction().replace(R.id.frame, FindTourFragment()).commit()
            bottomNavigation!!.menu.getItem(2).setChecked(true)
        }
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

//    override fun onBackPressed() {
//        if(fragmentManager.backStackEntryCount > 0){
//            fragmentManager.popBackStack()
//        }else{
//            if(myFragments.count() > 0){
//                var fragment : Fragment = myFragments.pop()
//                while(fragment != null && fragment == fragmentCurr){
//                    fragment = myFragments.pop()
//                }
//                fragmentManager.beginTransaction().hide(fragmentCurr).show(fragment).commit()
//                fragmentCurr = fragment
//            }else{
//                super.onBackPressed()
//            }
//        }
//    }
}
