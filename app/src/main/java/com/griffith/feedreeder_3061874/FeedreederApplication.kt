package com.griffith.feedreeder_3061874

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory

class FeedreederApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            // Disable `Cache-Control` header support as some podcast images disable disk caching.
            .respectCacheHeaders(false)
            .build()
    }
}