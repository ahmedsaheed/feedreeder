package com.griffith.feedreeder_3061874.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "feed_followed_entries",
    foreignKeys = [
        ForeignKey(
            entity = FeedCollection::class,
            parentColumns = ["uri"],
            childColumns = ["feed_uri"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class FeedFollowedEntry(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "feed_uri") val feedUri: String
)