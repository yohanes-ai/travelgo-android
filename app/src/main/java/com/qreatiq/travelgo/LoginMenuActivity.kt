package com.qreatiq.travelgo

import android.app.Dialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window

class LoginMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_menu)
    }

    public fun loginAction(v : View){
        val intent = Intent(this, LoginFormActivity::class.java)
        startActivity(intent)
    }

    public fun signUpAction(v : View){
        val intent = Intent( this, SignUpFormActivity::class.java)
        startActivity(intent)
    }
}
