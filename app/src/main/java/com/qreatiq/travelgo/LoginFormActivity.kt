package com.qreatiq.travelgo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.facebook.login.LoginResult
import java.util.Arrays.asList
import com.facebook.login.widget.LoginButton
import java.util.*
import android.content.Intent
import com.facebook.*


class LoginFormActivity : AppCompatActivity() {
    private val callbackManager = CallbackManager.Factory.create();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_form)

        val EMAIL = "email"

        val loginButton = findViewById<View>(R.id.login_button) as LoginButton
        loginButton.setReadPermissions(Arrays.asList(EMAIL))
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // App code
                val accessToken = AccessToken.getCurrentAccessToken()
                val isLoggedIn = accessToken != null && !accessToken.isExpired
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    public fun goToMain(v : View){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
