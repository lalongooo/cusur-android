package com.cusur.android.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.cusur.android.R
import com.cusur.android.base.BaseActivity
import com.cusur.android.dataclass.Publication
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_take_picture.*
import java.io.ByteArrayOutputStream
import java.util.*

class ActivityWritePost : BaseActivity() {

    private val REQUEST_IMAGE_CAPTURE = 567
    private val databaseReference = mDatabase.getReference("publication")
    private val userId = mFirebaseUser?.uid ?: ""
    lateinit var byteArrayPicture: ByteArray

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
        compressPicture(takenPicture)
    }

    private fun compressPicture(takenPicture: Bitmap) {

        val byteArrayOutputStream = ByteArrayOutputStream()
        takenPicture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        byteArrayPicture = byteArrayOutputStream.toByteArray()

    }

    private fun setupClickListeners() {
        btnPost.setOnClickListener {

            val comment = etComment.text
            if (!TextUtils.isEmpty(comment)) {

                progressBar.visibility = View.VISIBLE
                btnPost.isEnabled = false

                val picturesFolder = "pictures/"
                val fileName = "/" + UUID.randomUUID().toString() + ".png"
                val storageReference: StorageReference = mStorage.getReference(picturesFolder + userId + fileName)
                val uploadTask = storageReference.putBytes(byteArrayPicture)

                uploadTask.addOnFailureListener({

                    progressBar.visibility = View.INVISIBLE
                    btnPost.isEnabled = true
                    Toast.makeText(this@ActivityWritePost, "An error has occurred. Try again.", Toast.LENGTH_SHORT).show()

                }).addOnSuccessListener({ taskSnapshot ->

                    val publication = Publication(
                            etComment.text.toString(),
                            taskSnapshot.downloadUrl.toString()
                    )

                    databaseReference
                            .child(userId)
                            .push()
                            .setValue(publication) { databaseError, databaseReference ->
                                etComment.setText("")
                                progressBar.visibility = View.INVISIBLE
                                btnPost.isEnabled = true
                                Toast.makeText(this@ActivityWritePost, "Post sent!.", Toast.LENGTH_SHORT).show()
                            }
                })
            } else {
                Toast.makeText(this@ActivityWritePost, "Write a comment for this picture", Toast.LENGTH_SHORT).show()
            }
        }
    }
}