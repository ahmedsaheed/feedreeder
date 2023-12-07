package com.griffith.feedreeder_3061874

import android.content.Context
import android.content.SharedPreferences

class LocalStorage(ctx: Context) {
    val ReaderProgressPrefix = "reader_progress"
    private val prefs: SharedPreferences =
        ctx.getSharedPreferences("com.griffith.feedreeder_3061874", Context.MODE_PRIVATE)

    fun saveScrollProgress(url: String, progress: String) = prefs.edit().putString(url, progress).apply()
    fun getScrollProgress(url: String): String? = prefs.getString(url, null)


    fun setReadingProgress(url: String, progress: String) = prefs.edit().putString(ReaderProgressPrefix + url, progress).apply()
    fun getReadingProgress(url: String): String? = prefs.getString(ReaderProgressPrefix + url, null)

}