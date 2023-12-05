package com.griffith.feedreeder_3061874

import android.content.Context
import android.content.SharedPreferences

class LocalStorage(ctx: Context) {
    private val prefs: SharedPreferences =
        ctx.getSharedPreferences("com.griffith.feedreeder_3061874", Context.MODE_PRIVATE)

    fun saveProgress(url: String, progress: String) = prefs.edit().putString(url, progress).apply()
    fun getProgress(url: String): String? = prefs.getString(url, null)
}