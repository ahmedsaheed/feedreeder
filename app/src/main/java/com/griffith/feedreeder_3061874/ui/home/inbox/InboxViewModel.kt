package com.griffith.feedreeder_3061874.ui.home.inbox

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griffith.feedreeder_3061874.Graph
import com.griffith.feedreeder_3061874.data.CategoryStore
import com.griffith.feedreeder_3061874.data.Episode
import com.griffith.feedreeder_3061874.data.EpisodeStore
import com.griffith.feedreeder_3061874.data.EpisodeToFeed
import com.griffith.feedreeder_3061874.data.FeedFollowedEntry
import com.griffith.feedreeder_3061874.data.FeedRepository
import com.griffith.feedreeder_3061874.data.FeedStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class InboxViewModel (
    private val episodeStore: EpisodeStore = Graph.episodeStore
    ) : ViewModel() {
    private val _state = MutableStateFlow(InboxViewState())

    val state: MutableStateFlow<InboxViewState>
        get() = _state

    init {
        viewModelScope.launch {
          combine(
              episodeStore.episodesOfFollowedFeed(),
          ) {
              InboxViewState(
                  inboxEpisodes = it.toList().flatten()
              )
          }.collect {
                _state.value = it
          }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            combine(
                episodeStore.episodesOfFollowedFeed(),
            ) {
                InboxViewState(
                    inboxEpisodes = it.toList().flatten()
                )
            }.collect {
                _state.value = it
            }
        }
    }
}



data class InboxViewState (
    val inboxEpisodes: List<Episode> = emptyList()
)
