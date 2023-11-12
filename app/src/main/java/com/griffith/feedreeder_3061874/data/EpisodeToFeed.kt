package com.griffith.feedreeder_3061874.data

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import java.util.Objects

class EpisodeToFeed {

    @Embedded
    lateinit var episode: Episode
    @Relation(parentColumn = "feed_uri", entityColumn = "uri")
    lateinit var _feed: List<FeedCollection>

    @get:Ignore
    val feed: FeedCollection
        get() = _feed[0]

    operator fun component1() = episode
    operator fun component2() = feed

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is EpisodeToFeed -> episode == other.episode && _feed == other._feed
        else -> false
    }

    override fun hashCode(): Int {
        return Objects.hash(episode, _feed)
    }
}