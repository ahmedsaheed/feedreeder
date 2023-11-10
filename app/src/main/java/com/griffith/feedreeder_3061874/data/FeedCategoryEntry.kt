package com.griffith.feedreeder_3061874.data

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "feed_category_entries",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FeedCollection::class,
            parentColumns = ["uri"],
            childColumns = ["feed_uri"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("feed_uri", "category_id", unique = true),
        Index("category_id"),
        Index("feed_uri")
    ]
)

@Immutable
data class FeedCategoryEntry (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "feed_uri") val feedUri : String,
    @ColumnInfo(name = "category_id") val categoryId: Long
)