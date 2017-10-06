package com.cusur.android.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.cusur.android.R
import com.cusur.android.base.BaseActivity
import com.cusur.android.dataclass.Publication
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_take_picture.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ActivityWritePost : BaseActivity() {

    private val REQUEST_TAKE_PHOTO = 567
    private val databaseReference = mDatabase.getReference("publication")
    private val userId = mFirebaseUser?.uid ?: ""
    lateinit var byteArrayPicture: ByteArray
    lateinit var mCurrentPhotoPath: String

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
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            setPic()
        } else if (requestCode == REQUEST_TAKE_PHOTO) {
            finish()
        }
    }


    private fun setPic() {
        // Get the dimensions of the View
        val targetW = ivTakenPicture.width
        val targetH = ivTakenPicture.height

        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = Math.min(photoW / targetW, photoH / targetH)

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inPurgeable = true

        val bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)
        ivTakenPicture.setImageBitmap(bitmap)
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

//    private fun dispatchTakePictureIntent() {
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        if (takePictureIntent.resolveActivity(packageManager) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
//        }
//    }


    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(packageManager) != null) {
//            // Create the File where the photo should go
//            try {
//                val photoFile = createImageFile()
//                val photoURI = FileProvider.getUriForFile(
//                        this@ActivityWritePost,
//                        "com.cusur.fileprovider",
//                        photoFile)
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
//            } catch (ex: IOException) {
//                // Error occurred while creating the File
//                Log.d("JEH", "An error occurred while trying to create the file")
//            }
//        }


        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile();
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                        this@ActivityWritePost,
                        "com.cusur.fileprovider",
                        photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }


    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageName = "PIC_$timeStamp"
        val imagePath = File(filesDir, "images")
        imagePath.mkdirs()
        val imageFile = File.createTempFile(imageName, ".jpg", imagePath)
        mCurrentPhotoPath = imageFile.absolutePath
        return imageFile
    }

    private fun handleTakenPicture(takenPicture: Bitmap) {
        compressPicture(takenPicture)
        ivTakenPicture.setImageBitmap(takenPicture)
    }

    private fun compressPicture(takenPicture: Bitmap) {

        val byteArrayOutputStream = ByteArrayOutputStream(takenPicture.width * takenPicture.height)
        takenPicture.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        byteArrayPicture = byteArrayOutputStream.toByteArray()

    }

    private fun setupClickListeners() {
        btnPost.setOnClickListener {

            val comment = etComment.text
            if (!TextUtils.isEmpty(comment)) {

                progressBar.visibility = View.VISIBLE
                btnPost.isEnabled = false

                val picturesFolder = "pictures/"
                val fileName = "/" + UUID.randomUUID().toString() + ".jpg"
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
                                this@ActivityWritePost.finish()
                                Toast.makeText(this@ActivityWritePost, "Post sent!.", Toast.LENGTH_SHORT).show()
                            }
                })
            } else {
                Toast.makeText(this@ActivityWritePost, "Write a comment for this picture", Toast.LENGTH_SHORT).show()
            }
        }
    }
}