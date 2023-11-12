package com.griffith.feedreeder_3061874.ui.home.category

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FeedCategory(
    categoryId : Long,
    navigateToReader: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: FeedCategoryViewModel
}