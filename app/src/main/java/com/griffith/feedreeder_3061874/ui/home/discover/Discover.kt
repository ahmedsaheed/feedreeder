package com.griffith.feedreeder_3061874.ui.home.discover

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.griffith.feedreeder_3061874.data.Category
import com.griffith.feedreeder_3061874.ui.home.category.FeedCategory
import com.griffith.feedreeder_3061874.ui.theme.keyline1

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Discover(
    navigateToReader: (String) -> Unit,
    modifier: Modifier
) {
    val viewModel: DiscoverViewModel = viewModel()
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    val selectedCategory = viewState.selectedCategory
    Log.w("Discover", "Discover: ${viewState.categories} $selectedCategory")
    if (viewState.categories.isNotEmpty() && selectedCategory != null) {
        Column(modifier) {
            Spacer(Modifier.height(8.dp))
            FeedCategoryTabs(
               categories = viewState.categories,
                selectedCategory = selectedCategory,
                onCategorySelected = viewModel::onCategorySelected,
                modifier = Modifier.fillMaxWidth()
            )
            Crossfade(
                targetState = selectedCategory,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), label = ""
            ) { category ->

                    FeedCategory(
                        categoryId = category.id,
                        navigateToReader = navigateToReader,
                        modifier = Modifier.fillMaxSize()
                    )
                }
        }
    }
}

private val emptyTabIndicator: @Composable (List<TabPosition>) -> Unit = {}

@Composable
private fun FeedCategoryTabs(
    categories: List<Category>,
    selectedCategory: Category,
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = categories.indexOfFirst { it == selectedCategory }
    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        divider = {}, /* Disable the built-in divider */
        edgePadding = keyline1,
        indicator = emptyTabIndicator,
        modifier = modifier
    ) {
        categories.forEachIndexed { index, category ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onCategorySelected(category) }
            ) {
                ChoiceChipContent(
                    text = category.name,
                    selected = index == selectedIndex,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun ChoiceChipContent(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = when {
            selected -> MaterialTheme.colors.primary.copy(alpha = 0.08f)
            else -> MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
        },
        contentColor = when {
            selected -> MaterialTheme.colors.primary
            else -> MaterialTheme.colors.onSurface
        },
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
