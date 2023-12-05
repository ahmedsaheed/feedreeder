package com.griffith.feedreeder_3061874.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.griffith.feedreeder_3061874.data.Category
import com.griffith.feedreeder_3061874.data.Episode
import com.griffith.feedreeder_3061874.data.FeedCategoryEntry
import com.griffith.feedreeder_3061874.data.FeedCollection
import com.griffith.feedreeder_3061874.data.FeedFollowedEntry

@Database(
    entities = [
        FeedCollection::class,
        Episode::class,
        FeedCategoryEntry::class,
        Category::class,
        FeedFollowedEntry::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateTimeTypeConverters::class)
abstract class FeedreederDatabase : RoomDatabase() {
    abstract fun feedsDao(): FeedsDao
    abstract fun episodesDao(): EpisodesDoa
    abstract fun categoriesDao(): CategoriesDao
    abstract fun feedCategoryEntryDao(): FeedCategoryEntryDao
    abstract fun transactionRunnerDao(): TransactionRunnerDao
    abstract fun feedFollowedEntryDao(): FeedFollowedEntryDao
}