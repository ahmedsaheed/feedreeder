package com.griffith.feedreeder_3061874.ui.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griffith.feedreeder_3061874.Graph
import com.griffith.feedreeder_3061874.data.FeedFollowedEntry
import com.griffith.feedreeder_3061874.data.FeedRepository
import com.griffith.feedreeder_3061874.data.FeedStore
import com.griffith.feedreeder_3061874.data.FeedsExtraInfo
import com.griffith.feedreeder_3061874.data.SampleFeeds
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(
    private val feedRepository: FeedRepository = Graph.feedRepository,
    private val feedStore: FeedStore = Graph.feedStore
) : ViewModel() {
    private val selectedCategory = MutableStateFlow(HomeCategory.Discover)
    private val categories = MutableStateFlow(HomeCategory.values().asList())
    private val _state = MutableStateFlow(HomeViewState())
    private val refreshing = MutableStateFlow(false)
    private val _followedFeed = MutableLiveData<List<FeedFollowedEntry>>()
    val followedFeed: LiveData<List<FeedFollowedEntry>> get() = _followedFeed
    val state: StateFlow<HomeViewState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(
                categories,
                selectedCategory,
                feedStore.followedFeedSortedByLastEpisode(),
                refreshing,
                feedStore.getFollowedFeed()
            ) { categories, selectedCategories, feeds, refreshing, following ->
                _followedFeed.value = following
                HomeViewState(
                    homeCategories = categories,
                    selectedHomeCategory = selectedCategories,
                    featuredFeeds = feeds.toPersistentList(),
                    refreshing = refreshing,
                    errorMessage = null /*TODO*/
                )
            }.catch { throwable -> throw throwable }
                .collect { _state.value = it }
        }

        followedFeed.observeForever {
            val allFeed = SampleFeeds.plus(it.map { feed -> feed.feedUri })
            refresh(allFeed, force = true)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
     fun refresh(feedUris: List<String>, force: Boolean) {
        viewModelScope.launch {
            kotlin.runCatching {
                refreshing.value = true
                feedRepository.updateFeeds(feedUris, force)
            }
            // TODO: check response of runCatching and show log errors
            refreshing.value = false
        }
    }

    fun onHomeCategorySelected(category: HomeCategory) {
        selectedCategory.value = category
    }

    fun onFeedUnFollowed(feedUri: String) {
        viewModelScope.launch {
            feedStore.unfollowedFeed(feedUri)
        }
    }
}


enum class HomeCategory {
    Library, Discover
}

data class HomeViewState(
    val featuredFeeds: PersistentList<FeedsExtraInfo> = persistentListOf(),
    val refreshing: Boolean = false,
    val selectedHomeCategory: HomeCategory = HomeCategory.Discover,
    val homeCategories: List<HomeCategory> = emptyList(),
    val errorMessage: String? = null
)