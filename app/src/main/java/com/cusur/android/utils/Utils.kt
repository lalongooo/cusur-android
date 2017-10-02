package com.cusur.android.utils

class Utils {

    private val SECOND_MILLIS = 1000
    private val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private val DAY_MILLIS = 24 * HOUR_MILLIS

    fun getTimeAgo(timestamp: Long): String? {
        var time = timestamp
        if (time < 1000000000000L) {
            time *= 1000 // If timestamp given in seconds, convert to millis
        }

        val now = System.currentTimeMillis()
        if (time > now || time <= 0) {
            return null
        }

        val diff = now - time
        if (diff < MINUTE_MILLIS) {
            return "just now"
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago"
        } else if (diff < 50 * MINUTE_MILLIS) {
            return "${diff / MINUTE_MILLIS} minutes ago"
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago"
        } else if (diff < 24 * HOUR_MILLIS) {
            return "${diff / HOUR_MILLIS} hours ago"
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday"
        } else {
            return "${diff / DAY_MILLIS} days ago"
        }
    }
}