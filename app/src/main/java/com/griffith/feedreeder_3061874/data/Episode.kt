package com.griffith.feedreeder_3061874.data

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Duration
import java.time.OffsetDateTime

@Entity(
    tableName = "episodes",
    indices = [
        Index("uri", unique = true),
        Index("feed_uri")
    ],
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
@Immutable
data class Episode(
    @PrimaryKey @ColumnInfo(name = "uri") val uri: String,
    @ColumnInfo(name = "episode_uri") val episodeUri: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "subtitle") val subtitle: String? = null,
    @ColumnInfo(name = "summary") val summary: String? = null,
    @ColumnInfo(name = "author") val author: String? = null,
    @ColumnInfo(name = "published") val published: OffsetDateTime,
    @ColumnInfo(name = "duration") val duration: Duration? = null,
    @ColumnInfo(name = "content") val content: String? = null
)