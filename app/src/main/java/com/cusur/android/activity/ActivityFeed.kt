package com.cusur.android.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cusur.android.R
import kotlinx.android.synthetic.main.activity_feed.*

class ActivityFeed : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        fab.setOnClickListener {
            val mainIntent = Intent(this@ActivityFeed, ActivityTakePicture::class.java)
            mainIntent.putExtra(ActivityTakePicture.EXTRA_TAKE_PICTURE, true)
            startActivity(mainIntent)
        }
    }
}