package com.griffith.feedreeder_3061874.data.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.griffith.feedreeder_3061874.data.FeedCollection
import com.griffith.feedreeder_3061874.data.FeedsExtraInfo
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FeedsDao: BaseDao<FeedCollection> {
    @Query("SELECT * FROM feeds WHERE uri = :uri")
    abstract fun feedWithUri(uri: String): Flow<FeedCollection>


    @Transaction
    @Query(
        """
            SELECT feeds.*, last_episode_date, (followed_entries.feed_uri IS NOT NULL) AS is_followed
            FROM feeds
            INNER JOIN (
                SELECT episode_uri, MAX(published) AS last_episode_date
                FROM episodes
                GROUP BY episode_uri
            ) episodes ON feeds.uri = episodes.episode_uri
             LEFT JOIN feed_followed_entries AS followed_entries ON followed_entries.feed_uri = episodes.episode_uri
             ORDER BY datetime(last_episode_date) DESC
             LIMIT :limit
        """
    )

    abstract fun feedsSortedByLastEpisode(
        limit: Int
    ) : Flow<List<FeedsExtraInfo>>

    @Transaction
    @Query(
        """
            SELECT feeds.*, last_episode_date, (followed_entries.feed_uri IS NOT NULL) AS is_followed
            FROM feeds
            INNER JOIN(
                SELECT episodes.episode_uri, MAX(published) AS last_episode_date
                 FROM episodes
                 INNER JOIN feed_category_entries ON episodes.episode_uri = feed_category_entries.feed_uri
                 WHERE category_id = :categoryId
                 GROUP BY episodes.episode_uri
            ) inner_query ON feeds.uri = inner_query.episode_uri
            LEFT JOIN feed_followed_entries AS followed_entries ON followed_entries.feed_uri = inner_query.episode_uri
            ORDER BY datetime(last_episode_date) DESC
            LIMIT :limit
         """
    )

    abstract fun feedsInCategorySortedByLastEpisode(
        categoryId: Long,
        limit: Int
    ): Flow<List<FeedsExtraInfo>>


    @Transaction
    @Query(
    """
        SELECT feeds.*, last_episode_date, (followed_entries.feed_uri IS NOT NULL) is_followed
        FROM feeds
        INNER JOIN (
            SELECT episode_uri, MAX(published) AS last_episode_date FROM episodes 
            GROUP BY episode_uri
        ) episodes ON episode_uri = episodes.episode_uri
        INNER JOIN feed_followed_entries AS followed_entries ON followed_entries.feed_uri = episodes.episode_uri
        ORDER BY datetime(last_episode_date) DESC
        LIMIT :limit
    """
    )

    abstract fun followedFeedsSortedByLastEpisode(
        limit : Int
    ) : Flow<List<FeedsExtraInfo>>

   @Query("SELECT COUNT(*) FROM feeds")
   abstract suspend fun count() : Int
}