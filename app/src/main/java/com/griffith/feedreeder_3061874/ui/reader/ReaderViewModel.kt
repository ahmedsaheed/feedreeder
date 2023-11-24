package com.griffith.feedreeder_3061874.ui.reader

import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.griffith.feedreeder_3061874.Graph
import com.griffith.feedreeder_3061874.data.EpisodeStore
import com.griffith.feedreeder_3061874.data.FeedStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration

data class ReaderUiState(
    val title: String = "",
    val subTitle: String = "",
    val duration: Duration? = null,
    val feedName: String = "",
    val author: String = "",
    val summary: String = "",
    val podcastImageUrl: String = "",
    val content: String? = null
)


class ReaderViewModel (
    episodeStore: EpisodeStore,
    feedStore: FeedStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val episodeUri: String = Uri.decode(savedStateHandle.get<String>("episodeUri")!!)

    var uiState by mutableStateOf(ReaderUiState())
        private set

    init {
        viewModelScope.launch {
            val episode = episodeStore.episodeWithUri(episodeUri).first()
            val feed = feedStore.feedWithUri(episode.episodeUri).first()
            uiState = ReaderUiState(
                title = episode.title,
                duration = episode.duration,
                feedName = feed.title,
                summary = episode.summary ?: "",
                podcastImageUrl = feed.imageUrl ?: "",
                content = episode.content
            )
        }
    }

    companion object {
        fun provideFactory(
            episodeStore: EpisodeStore = Graph.episodeStore,
            feedStore: FeedStore = Graph.feedStore,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return ReaderViewModel(episodeStore, feedStore, handle) as T
                }
            }
    }

}