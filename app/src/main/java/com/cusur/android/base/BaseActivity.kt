package com.cusur.android.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

abstract class BaseActivity : AppCompatActivity() {

    protected val mDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    protected val mStorage = FirebaseStorage.getInstance()
    protected val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    protected val mFirebaseUser: FirebaseUser? = mAuth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}