package com.cusur.android.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.cusur.android.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ActivitySplash : AppCompatActivity() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val SPLASH_DISPLAY_LENGTH = 2000

        Handler().postDelayed({

            var mainIntent = Intent(baseContext, ActivityLogin::class.java)
            val firebaseUser: FirebaseUser? = mAuth.currentUser

            if (firebaseUser != null) {
                mainIntent = Intent(baseContext, ActivityFeed::class.java)
            }

            startActivity(mainIntent)
            finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }
}