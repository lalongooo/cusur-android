package com.cusur.android.dataclass

import com.google.firebase.database.ServerValue

data class Publication(
        val comment: String,
        val downloadUrl: String,
        val postDate: Long = 0L,
        val dateCreated: Map<String, String> = ServerValue.TIMESTAMP
)