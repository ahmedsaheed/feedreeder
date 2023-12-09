package com.griffith.feedreeder_3061874.ui.home.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griffith.feedreeder_3061874.Graph
import com.griffith.feedreeder_3061874.data.CategoryStore
import com.griffith.feedreeder_3061874.data.EpisodeToFeed
import com.griffith.feedreeder_3061874.data.FeedStore
import com.griffith.feedreeder_3061874.data.FeedsExtraInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class FeedCategoryViewModel(
    private val categoryId: Long,
    private val categoryStore: CategoryStore = Graph.categoryStore,
    private val feedStore: FeedStore = Graph.feedStore
) : ViewModel() {
    private val _state = MutableStateFlow(FeedCategoryViewState())

    val state: StateFlow<FeedCategoryViewState>
        get() = _state

    init {
        viewModelScope.launch {
            val recentFeedFlow = categoryStore.feedInCategorySortedByFeedCount(
                categoryId,
                limit = 10
            )

            val episodesFlow = categoryStore.episodeFromFeedsInCategory(
                categoryId,
                limit = 20
            )

            combine(recentFeedFlow, episodesFlow) { topFeeds, episodes ->
                FeedCategoryViewState(
                    topFeeds = topFeeds,
                    episode = episodes
                )
            }.collect { _state.value = it }
        }
    }

    fun onToggleFeedFollowed(feedUri: String) {
        viewModelScope.launch {
            feedStore.toggleFeedFollowed(feedUri)
        }
    }

}

data class FeedCategoryViewState(
    val topFeeds: List<FeedsExtraInfo> = emptyList(),
    val episode: List<EpisodeToFeed> = emptyList()
)