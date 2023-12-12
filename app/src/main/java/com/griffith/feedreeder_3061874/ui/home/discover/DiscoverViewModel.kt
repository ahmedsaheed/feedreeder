package com.griffith.feedreeder_3061874.ui.home.discover

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.griffith.feedreeder_3061874.Graph
import com.griffith.feedreeder_3061874.data.Category
import com.griffith.feedreeder_3061874.data.CategoryStore
import com.griffith.feedreeder_3061874.data.FeedStore
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class DiscoverViewModel(
    private val categoryStore: CategoryStore = Graph.categoryStore,
    private val feedStore: FeedStore = Graph.feedStore,
) : ViewModel() {
    private val _selectedCategory = MutableStateFlow<Category?>(null)
    private val _state = MutableStateFlow(DiscoverViewState())

    val state: StateFlow<DiscoverViewState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(
                categoryStore.categoriesSortedByFeedCount(limit = 10)
                    .onEach { categories ->
                        Log.w("DiscoverViewModel", "DiscoverViewModel: categories: $categories")
                        if (categories.isNotEmpty() && _selectedCategory.value == null) {
                            _selectedCategory.value = categories[0]
                        }
                    },
                _selectedCategory
            ) { categories, selectedCategories ->
                DiscoverViewState(
                    categories = categories,
                    selectedCategory = selectedCategories
                )
            }.collect { _state.value = it }
        }
    }

    fun validateRssUrl(address: String?): Boolean {
        var ok = false
        try {
            val url = URL(address)
            val httpcon = url.openConnection() as HttpURLConnection
            val input = SyndFeedInput()
            val feed = input.build(XmlReader(url))
            ok = true
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
        return ok
    }

    fun addNewFollowedFeed(feedUri: String) {
        viewModelScope.launch {
            feedStore.toggleFeedFollowed(feedUri)
        }
    }

    fun onCategorySelected(category: Category) {
        _selectedCategory.value = category
    }
}

data class DiscoverViewState(
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null
)
