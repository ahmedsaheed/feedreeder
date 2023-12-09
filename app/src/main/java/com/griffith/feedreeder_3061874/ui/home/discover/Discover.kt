package com.griffith.feedreeder_3061874.ui.home.discover

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.griffith.feedreeder_3061874.data.Category
import com.griffith.feedreeder_3061874.ui.home.category.FeedCategory
import com.griffith.feedreeder_3061874.ui.theme.keyline1
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Discover(
    navigateToReader: (String) -> Unit,
    modifier: Modifier
) {
    val viewModel: DiscoverViewModel = viewModel()
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val selectedCategory = viewState.selectedCategory
    Log.w("Discover", "Discover: ${viewState.categories} $selectedCategory")
    if (viewState.categories.isNotEmpty() && selectedCategory != null) {
        Column(modifier) {
            if (showBottomSheet){
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colors.surface,


                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(MaterialTheme.colors.surface)
                    ) {
                        Text(
                            text = "Bottom Sheet",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
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
                Spacer(Modifier.height(8.dp))

            }
                AddFeedFab(
                    onClick = { showBottomSheet = true },
                    modifier = Modifier
                )
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
@Composable
fun AddFeedFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
        Box (
            modifier = Modifier
                .fillMaxWidth()
                //give transparent background to the box
                .background(Color.Transparent)
        ){
            FloatingActionButton(
                modifier = modifier
                    .padding(16.dp)
                    .align(alignment = Alignment.BottomEnd),
                onClick = { onClick() },
                containerColor = MaterialTheme.colors.primary.copy(alpha = 0.08f),
                contentColor = MaterialTheme.colors.onSurface,
            ) {
                Icon(Icons.Filled.Add, "Add Feed")
            }
        }
}