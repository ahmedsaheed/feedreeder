package com.griffith.feedreeder_3061874.data

import com.griffith.feedreeder_3061874.data.room.EpisodesDoa
import com.rometools.utils.Integers
import kotlinx.coroutines.flow.Flow

class EpisodeStore (
    private val episodeDoa : EpisodesDoa
) {
    fun episodeWithUri(episodeUri : String) : Flow<Episode> {
        return episodeDoa.episode(episodeUri)
    }

    fun episodesInFeed(
        feedUri : String,
        limit : Int = Integer.MAX_VALUE
    ) : Flow<List<Episode>> {
        return episodeDoa.episodesFromFeedUri(feedUri, limit)
    }

    suspend fun addEpisodes(episode: Collection<Episode>) = episodeDoa.insertAll(episode)
    suspend fun isEmpty() : Boolean = episodeDoa.count() == 0

}