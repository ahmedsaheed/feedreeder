package com.griffith.feedreeder_3061874.data

import android.util.Log
import com.griffith.feedreeder_3061874.data.room.CategoriesDao
import com.griffith.feedreeder_3061874.data.room.EpisodesDoa
import com.griffith.feedreeder_3061874.data.room.FeedCategoryEntryDao
import com.griffith.feedreeder_3061874.data.room.FeedsDao
import kotlinx.coroutines.flow.Flow

class CategoryStore (
    private val categoriesDao : CategoriesDao,
    private val categoryEntryDao: FeedCategoryEntryDao,
    private val episodesDoa: EpisodesDoa,
    private val feedsDao: FeedsDao
) {
    fun categoriesSortedByFeedCount(
        limit: Int = Integer.MAX_VALUE
    ) : Flow<List<Category>> {
        return categoriesDao.categoriesSortedByFeedCount(limit)
    }

    fun feedInCategorySortedByFeedCount(
        categoryId: Long,
        limit: Int = Int.MAX_VALUE
    ) : Flow<List<FeedsExtraInfo>> {
        return feedsDao.feedsInCategorySortedByLastEpisode(categoryId, limit)
    }

    fun episodeFromFeedsInCategory(
        categoryId: Long,
        limit: Int = Int.MAX_VALUE
    ) : Flow<List<EpisodeToFeed>> {
        return episodesDoa.episodesFromFeedInCategory(categoryId, limit)
    }

    suspend fun addCategory(category: Category) : Long{
        return when (val local = categoriesDao.getCategoryWithName(category.name)) {
            null -> categoriesDao.insert(category)
            else -> local.id
        }
    }

    suspend fun addFeedToCategory(feedUri: String, categoryId: Long) {
        categoryEntryDao.insert(
            FeedCategoryEntry(feedUri = feedUri, categoryId = categoryId)
        )
    }
}