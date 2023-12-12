package com.griffith.feedreeder_3061874

import android.content.Context
import androidx.room.Room
import com.griffith.feedreeder_3061874.data.CategoryStore
import com.griffith.feedreeder_3061874.data.EpisodeStore
import com.griffith.feedreeder_3061874.data.FeedFetcher
import com.griffith.feedreeder_3061874.data.FeedRepository
import com.griffith.feedreeder_3061874.data.FeedStore
import com.griffith.feedreeder_3061874.data.SubscriptionStore
import com.griffith.feedreeder_3061874.data.room.FeedreederDatabase
import com.griffith.feedreeder_3061874.data.room.TransactionRunner
import com.rometools.rome.io.SyndFeedInput
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener
import java.io.File

object Graph {
    lateinit var okHttpClient: OkHttpClient
    lateinit var db: FeedreederDatabase
        private set
    private val transactionRunner: TransactionRunner
        get() = db.transactionRunnerDao()

    private val syndFeedInput by lazy { SyndFeedInput() }


    val feedRepository by lazy {
        FeedRepository(
            feedFetcher = feedFetcher,
            feedStore = feedStore,
            episodeStore = episodeStore,
            categoryStore = categoryStore,
            transactionRunner = transactionRunner,
            mainDispatcher = mainDispatcher
        )
    }

    private val feedFetcher by lazy {
        FeedFetcher(
            okHttpClient = okHttpClient,
            syndFeedInput = syndFeedInput,
            ioDispatcher = ioDispatcher
        )
    }

    private val subscriptionStore by lazy {
        SubscriptionStore(
            subscriptionDao = db.subscriptionsDao(),
        )
    }


    val episodeStore by lazy {
        EpisodeStore(
            episodeDoa = db.episodesDao()
        )
    }

    val feedStore by lazy {
        FeedStore(
            feedDao = db.feedsDao(),
            feedFollowedEntryDao = db.feedFollowedEntryDao(),
            transactionRunner = transactionRunner
        )
    }

    val categoryStore by lazy {
        CategoryStore(
            categoriesDao = db.categoriesDao(),
            categoryEntryDao = db.feedCategoryEntryDao(),
            episodesDoa = db.episodesDao(),
            feedsDao = db.feedsDao()
        )
    }

    private val mainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main

    private val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO

    fun provide(context: Context) {
        okHttpClient = OkHttpClient.Builder()
            .cache(Cache(File(context.cacheDir, "http_cache"), (20 * 1024 * 1024).toLong()))
            .apply {
                eventListenerFactory(LoggingEventListener.Factory())
            }
            .build()

        db = Room.databaseBuilder(context, FeedreederDatabase::class.java, "data.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}