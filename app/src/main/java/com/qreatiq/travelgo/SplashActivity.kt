package com.qreatiq.travelgo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Splash);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        route();
        finish();
    }

    private fun route(){
        val intent = Intent(this, LoginMenuActivity::class.java);
        startActivity(intent);
    }
}
