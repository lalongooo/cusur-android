package com.cusur.android.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

import com.cusur.android.R
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.activity_login.*

class ActivityLogin : AppCompatActivity() {

    private val callbackManager: CallbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.setReadPermissions("email", "public_profile")
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                Log.d("FBLogin", "Success!")
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
}
