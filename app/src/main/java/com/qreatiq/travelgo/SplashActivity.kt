package com.qreatiq.travelgo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class SplashActivity : AppCompatActivity() {

    private var user: SharedPreferences? = null
    private var userID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Splash);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        user = getSharedPreferences("user_id", Context.MODE_PRIVATE)
        userID = user!!.getString("user_id", "Data Not Found")

        route();
        finish();
    }

    private fun route(){

        var intent: Intent? = null
//        intent = Intent(this, LoginMenuActivity::class.java);
        if(userID.equals("Data Not Found")){
            intent = Intent(this, LoginMenuActivity::class.java);
        }
        else{
            intent = Intent(this, MainActivity::class.java);
        }

        startActivity(intent);
    }
}
