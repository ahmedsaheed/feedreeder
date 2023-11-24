package com.griffith.feedreeder_3061874.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.griffith.feedreeder_3061874.data.room.TransactionRunner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FeedRepository(
    private val feedFetcher: FeedFetcher,
    private val feedStore: FeedStore,
    private val episodeStore: EpisodeStore,
    private val categoryStore: CategoryStore,
    private val transactionRunner: TransactionRunner,
    mainDispatcher: CoroutineDispatcher
) {
    private var refreshingJob: Job? = null
    private var scope = CoroutineScope(mainDispatcher)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateFeeds(force: Boolean) {
        if (refreshingJob?.isActive == true) {
            refreshingJob?.join()
        } else if (force || feedStore.isEmpty()) {
            refreshingJob = scope.launch {
                val res = feedFetcher(SampleFeeds)
//                Log.w("responseFromFeedRepo", res.toString());
                // log the response from the flow
//                .collect { value -> _state.value = value }
                res.collect { value -> println(value) }

                res.collect { Log.w("responseFromFeedRepo", it.toString()) }
                    res.filter { it is FeedRssResponse.Success }
                    .map { it as FeedRssResponse.Success }
                    .collect { (feed, episodes, categories) ->
                        transactionRunner {
                            feedStore.addFeed(feed)
                            episodeStore.addEpisodes(episodes)

                            categories.forEach { category ->
                                val categoryId = categoryStore.addCategory(category)
                                categoryStore.addFeedToCategory(
                                    feedUri = feed.uri,
                                    categoryId = categoryId
                                )
                            }
                        }
                    }
            }
        }
    }
}