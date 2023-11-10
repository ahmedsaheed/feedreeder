package com.griffith.feedreeder_3061874.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.time.OffsetDateTime
import java.util.Objects

class FeedsExtraInfo {
    @Embedded
    lateinit var feed: FeedCollection

    @ColumnInfo(name = "last_episode_date")
    var lastEpisodeDate: OffsetDateTime? = null

    @ColumnInfo(name = "is_followed")
    var isFollowed =  false

    operator fun component1() = feed
    operator fun component2() = lastEpisodeDate
    operator fun component3() = isFollowed

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is FeedsExtraInfo -> {
            feed == other.feed &&
                    lastEpisodeDate == other.lastEpisodeDate &&
                    isFollowed == other.isFollowed
        }
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(feed, lastEpisodeDate, isFollowed)
}