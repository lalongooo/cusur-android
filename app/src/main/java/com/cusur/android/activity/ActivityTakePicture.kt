package com.cusur.android.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.cusur.android.R
import com.cusur.android.base.BaseActivity
import kotlinx.android.synthetic.main.activity_take_picture.*


class ActivityTakePicture : BaseActivity() {

    private val REQUEST_IMAGE_CAPTURE = 567
    private var databaseReference = database.getReference("publication")

    companion object {
        val EXTRA_TAKE_PICTURE = "take_picture"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_picture)
        handleExtras(intent)
        setupClickListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data?.extras
            val bitmap = extras?.get("data") as Bitmap?
            if (bitmap != null) {
                handleTakenPicture(bitmap)
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            finish()
        }
    }

    private fun handleExtras(intent: Intent) {
        val extras = intent.extras
        if (extras != null) {
            val takePicture = extras.getBoolean(EXTRA_TAKE_PICTURE)
            if (takePicture) {
                dispatchTakePictureIntent()
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun handleTakenPicture(takenPicture: Bitmap) {
        ivTakenPicture.setImageBitmap(takenPicture)
    }

    private fun setupClickListeners() {
        btnPost.setOnClickListener {
            databaseReference
                    .child(firebaseUser?.uid)
                    .push().setValue(etComment.text.toString()) { databaseError, databaseReference ->
                Toast.makeText(this@ActivityTakePicture, "Post sent!.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    data class Publication(val name: String, val age: Int)
}