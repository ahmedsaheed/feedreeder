package com.griffith.feedreeder_3061874.data

import android.util.Log
import com.griffith.feedreeder_3061874.data.room.FeedFollowedEntryDao
import com.griffith.feedreeder_3061874.data.room.FeedsDao
import com.griffith.feedreeder_3061874.data.room.TransactionRunner
import kotlinx.coroutines.flow.Flow

class FeedStore(
    private val feedDao: FeedsDao,
    private val feedFollowedEntryDao: FeedFollowedEntryDao,
    private val transactionRunner: TransactionRunner
) {
    fun feedWithUri(uri: String): Flow<FeedCollection> {
        return feedDao.feedWithUri(uri)
    }

    fun feedSortedByLastEpisode(limit: Int = Int.MAX_VALUE): Flow<List<FeedsExtraInfo>> {
        return feedDao.feedsSortedByLastEpisode(limit)
    }

    fun followedFeedSortedByLastEpisode(
        limit: Int = Int.MAX_VALUE
    ): Flow<List<FeedsExtraInfo>> {
        val res = feedDao.followedFeedsSortedByLastEpisode(limit)
        Log.w("FeedStore", "followedFeedSortedByLastEpisode: $res")
        return res
    }

    private suspend fun followedFeed(feedUri: String) {
        feedFollowedEntryDao.insert(FeedFollowedEntry(feedUri = feedUri))
    }

    suspend fun unfollowedFeed(feedUri: String) {
        feedFollowedEntryDao.deleteWithFeedUri(feedUri)
    }

    suspend fun toggleFeedFollowed(feedUri: String) = transactionRunner {
        if (feedFollowedEntryDao.isFeedFollowed(feedUri)) {
            unfollowedFeed(feedUri)
        } else {
            followedFeed(feedUri)
        }
    }

    suspend fun addFeed(feed: FeedCollection) {
        feedDao.insert(feed)
    }

    suspend fun isEmpty(): Boolean = feedDao.count() == 0
}