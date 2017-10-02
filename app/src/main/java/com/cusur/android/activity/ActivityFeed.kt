package com.cusur.android.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.cusur.android.R
import com.cusur.android.adapter.AdapterPostsFeed
import com.cusur.android.base.BaseActivity
import com.cusur.android.dataclass.Publication
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.android.synthetic.main.activity_feed.*


class ActivityFeed : BaseActivity() {

    private val TAG = ActivityFeed::class.simpleName
    private val databaseReference = mDatabase.getReference("publication")
    private val myUserId = mFirebaseUser?.uid ?: ""
    private var publications: MutableList<Publication> = mutableListOf()
    private val mAdapter = AdapterPostsFeed(publications)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        setUpClickListeners()
        setUpRecyclerView()
        retrieveData()
    }

    private fun setUpClickListeners() {
        fab.setOnClickListener {
            val mainIntent = Intent(this@ActivityFeed, ActivityWritePost::class.java)
            mainIntent.putExtra(ActivityWritePost.EXTRA_TAKE_PICTURE, true)
            startActivity(mainIntent)
        }
    }

    private fun setUpRecyclerView() {
        rvPosts.layoutManager = LinearLayoutManager(ActivityFeed@ this)
        rvPosts.adapter = mAdapter
        rvPosts.setHasFixedSize(true)
    }

    private fun retrieveData() {
        val publicationsQuery = databaseReference
                .child(myUserId)
                .orderByChild("dateCreated")

        publicationsQuery.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(databaseError: DatabaseError?) {
                Log.d(TAG, databaseError.toString())
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot?, p1: String?) {
                Log.d(TAG, dataSnapshot.toString())
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                Log.d(TAG, dataSnapshot.toString())
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                Log.d(TAG, dataSnapshot.toString())

                val publication = Publication(
                        dataSnapshot.child("comment").value.toString(),
                        dataSnapshot.child("downloadUrl").value.toString(),
                        dataSnapshot.child("dateCreated").value.toString().toLong()
                )
                mAdapter.add(publication)
                rvPosts.smoothScrollToPosition(0)
                rvPosts.visibility = View.VISIBLE
                cameraIcon.visibility = View.INVISIBLE
                tvEmptyMessage.visibility = View.INVISIBLE
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot?) {
                Log.d(TAG, dataSnapshot.toString())
            }
        })
    }
}