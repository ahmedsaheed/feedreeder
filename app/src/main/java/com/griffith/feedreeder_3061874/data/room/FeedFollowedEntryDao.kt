package com.griffith.feedreeder_3061874.data.room

import androidx.room.Dao
import androidx.room.Query
import com.griffith.feedreeder_3061874.data.FeedFollowedEntry
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FeedFollowedEntryDao : BaseDao<FeedFollowedEntry> {
    @Query("DELETE FROM feed_followed_entries WHERE feed_uri = :feedUri")
    abstract suspend fun deleteWithFeedUri(feedUri: String)

    @Query("SELECT * FROM feed_followed_entries")
    abstract fun getAllFollowedFeed(): Flow<List<FeedFollowedEntry>>

    @Query("SELECT COUNT(*) FROM feed_followed_entries WHERE feed_uri = :feedUri")
    protected abstract suspend fun feedFollowRowCount(feedUri: String): Int

    suspend fun isFeedFollowed(feedUri: String): Boolean = feedFollowRowCount(feedUri) > 0
}