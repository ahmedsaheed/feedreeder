package com.griffith.feedreeder_3061874.data.room

import androidx.room.Query
import com.griffith.feedreeder_3061874.data.Category
import kotlinx.coroutines.flow.Flow

abstract class CategoriesDao : BaseDao<Category> {
    @Query(
        """
            SELECT categories.* from categories
            INNER JOIN (
                SELECT category_id, COUNT(feed_uri) AS feed_count FROM feed_category_entries
                GROUP BY category_id
            ) ON category_id = categories.id
            ORDER BY feed_count DESC
        LIMIT :limit
        """
    )

    abstract fun categoriesSortedByFeedCount(
        limit : Int
    ) : Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE name = :name")
    abstract suspend fun getCategoryWithName(name : String) : Category?
}