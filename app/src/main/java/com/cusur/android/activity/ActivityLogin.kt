package com.cusur.android.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.cusur.android.R
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*


class ActivityLogin : AppCompatActivity() {

    private val callbackManager: CallbackManager = CallbackManager.Factory.create()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val TAG: String = "JEH"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton.setReadPermissions("email", "public_profile")
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                loginButton.visibility = View.INVISIBLE
                progressBar.visibility = View.VISIBLE
                handleFacebookAccessToken(result.accessToken)
            }

            override fun onCancel() {
                Log.d("FBLogin", "Cancelled!")
            }

            override fun onError(error: FacebookException?) {
                Log.d("FBLogin", "Error!")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        val firebaseUser: FirebaseUser? = mAuth.currentUser
        if (firebaseUser != null) {
            Log.d("JEH", "User already authenticated!")
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:" + token)

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithCredential:success")
                        val mainIntent = Intent(this@ActivityLogin, ActivityFeed::class.java)
                        startActivity(mainIntent)
                        finish()
                    } else {
                        Toast.makeText(this@ActivityLogin, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
    }
}
