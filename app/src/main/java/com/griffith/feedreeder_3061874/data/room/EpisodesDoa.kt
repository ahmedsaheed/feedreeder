package com.griffith.feedreeder_3061874.data.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.griffith.feedreeder_3061874.data.Episode
import com.griffith.feedreeder_3061874.data.EpisodeToFeed
import kotlinx.coroutines.flow.Flow

@Dao
abstract class EpisodesDoa : BaseDao<Episode> {
    @Query(
        """
            SELECT * FROM episodes WHERE uri = :uri
        """
    )
    abstract fun episode(uri: String): Flow<Episode>

    @Query(
        """
            SELECT * FROM episodes WHERE episode_uri = :feedUri 
            ORDER BY datetime(published) DESC
            LIMIT :limit
        """
    )
    abstract fun episodesFromFeedUri(
        feedUri: String,
        limit: Int
    ): Flow<List<Episode>>

    @Transaction
    @Query(
        """
            SELECT episodes.* FROM episodes
            INNER JOIN feed_category_entries ON episodes.episode_uri = feed_category_entries.feed_uri
            WHERE category_id = :categoryId
            ORDER BY datetime(published) DESC
            LIMIT :limit
        """
    )
    abstract fun episodesFromFeedInCategory(
        categoryId: Long,
        limit: Int
    ): Flow<List<EpisodeToFeed>>

    @Transaction
    @Query(
        """
            SELECT episodes.* FROM episodes
            INNER JOIN feed_followed_entries ON episodes.episode_uri = feed_followed_entries.feed_uri
            ORDER BY datetime(published) DESC
        """
    )
    abstract fun episodeFromFollowedFeed(): Flow<List<Episode>>

    @Query("SELECT COUNT(*) FROM episodes")
    abstract suspend fun count(): Int


}