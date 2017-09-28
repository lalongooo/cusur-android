package com.cusur.android.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.cusur.android.R
import com.cusur.android.base.BaseActivity

class ActivitySplash : BaseActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val SPLASH_DISPLAY_LENGTH = 2000

        Handler().postDelayed({

            var mainIntent = Intent(baseContext, ActivityLogin::class.java)
            if (mFirebaseUser != null) {
                mainIntent = Intent(baseContext, ActivityFeed::class.java)
            }

            startActivity(mainIntent)
            finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }
}