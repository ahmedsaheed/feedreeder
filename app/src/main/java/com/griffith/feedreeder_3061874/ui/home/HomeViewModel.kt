package com.griffith.feedreeder_3061874.ui.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griffith.feedreeder_3061874.Graph
import com.griffith.feedreeder_3061874.data.FeedRepository
import com.griffith.feedreeder_3061874.data.FeedStore
import com.griffith.feedreeder_3061874.data.FeedsExtraInfo
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

    val state: StateFlow<HomeViewState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(
                categories,
                selectedCategory,
                feedStore.followedFeedSortedByLastEpisode(),
                refreshing
            ) { categories, selectedCategories, feeds, refreshing ->

                Log.w("HomeViewModel", "HomeViewState: ${HomeViewState(
                    homeCategories = categories,
                    selectedHomeCategory = selectedCategories,
                    featuredFeeds = feeds.toPersistentList(),
                    refreshing = refreshing,
                    errorMessage = null /*TODO*/
                )}")
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
        refresh(force = true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun refresh(force : Boolean) {
        viewModelScope.launch {
            kotlin.runCatching {
                refreshing.value = true
                feedRepository.updateFeeds(force)
            }
            // TODO: check response of runCatching and show log errors
            refreshing.value = false
        }
    }

    fun onHomeCategorySelected(category: HomeCategory) {
        selectedCategory.value = category
    }

    fun onFeedUnFollowed(feedUri : String) {
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