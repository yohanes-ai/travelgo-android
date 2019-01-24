package com.qreatiq.travelgo

import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.facebook.login.LoginResult
import java.util.Arrays.asList
import com.facebook.login.widget.LoginButton
import java.util.*
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.*
import com.qreatiq.travelgo.utils.Constant
import kotlinx.android.synthetic.main.activity_login_form.*
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.email
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.HashMap
import kotlin.math.log

class LoginFormActivity : AppCompatActivity() {
	private val callbackManager = CallbackManager.Factory.create();
	private var queue: RequestQueue? = null

	private var saveLogin: SharedPreferences? = null
	private var editor: SharedPreferences.Editor? = null


	private var user: SharedPreferences? = null
	private var userID: String? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login_form)

		val EMAIL = "email"

		queue = Volley.newRequestQueue(this)

		val loginButton = findViewById<View>(R.id.login_button) as LoginButton
		loginButton.setReadPermissions(Arrays.asList(EMAIL))
		// If you are using in a fragment, call loginButton.setFragment(this);

		// Callback registration

		loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
			override fun onSuccess(loginResult: LoginResult) {
				// App code
				val accessToken = AccessToken.getCurrentAccessToken()
				val isLoggedIn = accessToken != null && !accessToken.isExpired

                var userid: String = loginResult.accessToken.userId

                val graphRequest = GraphRequest.newMeRequest(
                    loginResult.accessToken
                ) { `object`, response -> displayUserInfo(`object`) }
                val paramaters = Bundle()
                paramaters.putString("fields", "email, first_name")
                graphRequest.parameters = paramaters
                graphRequest.executeAsync()
            }

			override fun onCancel() {
				// App code
			}

			override fun onError(exception: FacebookException) {
				// App code
			}
		})
	}

    fun displayUserInfo(object1: JSONObject) {
        var email: String? = null
        var first_name: String? = null
        try {
            email = object1.getString("email")
            first_name = object1.getString("first_name")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

		val url = Constant.C_URL + "login.php"

		var jsonObject = JSONObject()
		jsonObject.put("email", email)
		jsonObject.put("first_name", first_name)

		val jsonObjectRequest = object:JsonObjectRequest(Request.Method.POST, url, jsonObject, Response.Listener { response ->

			if(response.getString("status").equals("success")){
				saveLogin = getSharedPreferences("user_id", Context.MODE_PRIVATE)
				editor = saveLogin!!.edit()
				editor!!.putString("user_id", response.getString("data"))
				editor!!.apply()

				val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
				startActivity(intent)
			}
		},
			Response.ErrorListener { error -> Log.e("error", error.message) })
		{
			@Throws(AuthFailureError::class)
			override fun getHeaders(): Map<String, String> {
				val header = HashMap<String, String>()
				header ["Content-Type"] = "application/json"
				return header
			}
		}

		queue!!.add(jsonObjectRequest)
    }

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		callbackManager.onActivityResult(requestCode, resultCode, data)
		super.onActivityResult(requestCode, resultCode, data)
	}

	fun goToMain(v : View){
		var emailInput = findViewById<View>(R.id.editText3) as EditText
		var passwordInput = findViewById<View>(R.id.editText4) as EditText

		if(emailInput.text.isEmpty()){
			editText3.setError("Please fill username")
		}else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput.text).matches()){
			editText3.setError("Please input valid email")
		}else if(passwordInput.text.isEmpty()){
			editText4.setError("Please fill password")
		}else {
			val url = Constant.C_URL + "login.php"

			var jsonObject = JSONObject()
			jsonObject.put("email", emailInput!!.text.toString())
			jsonObject.put("password", passwordInput!!.text.toString())


			val jsonObjectRequest =
				object : JsonObjectRequest(Request.Method.POST, url, jsonObject, Response.Listener { response ->
					Log.d("data123", response.toString())
					if (response.getString("status").equals("success")) {
						saveLogin = getSharedPreferences("user_id", Context.MODE_PRIVATE)
						editor = saveLogin!!.edit()
						editor!!.putString("user_id", response.getString("data"))
						editor!!.apply()

						val intent = Intent(this, MainActivity::class.java)
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
						startActivity(intent)
					} else {
						Toast.makeText(this, "Email / Password Wrong!", Toast.LENGTH_LONG).show()
					}
				},
					Response.ErrorListener { error -> Log.e("error", error.message) }) {
					@Throws(AuthFailureError::class)
					override fun getHeaders(): Map<String, String> {
						val header = HashMap<String, String>()
						header["Content-Type"] = "application/json"
						return header
					}
				}

			queue!!.add(jsonObjectRequest)
		}
	}

	fun hideKeyboard(view: View) {
		val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
		inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
	}
}
