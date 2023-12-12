package com.griffith.feedreeder_3061874.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "feed_followed_entries",
    indices = [
        Index("feed_uri", unique = true)
    ]
)

data class FeedFollowedEntry(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "feed_uri") val feedUri: String
)