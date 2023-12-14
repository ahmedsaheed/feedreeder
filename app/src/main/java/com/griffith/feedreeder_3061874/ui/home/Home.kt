package com.griffith.feedreeder_3061874.ui.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.griffith.feedreeder_3061874.R
import com.griffith.feedreeder_3061874.data.FeedsExtraInfo
import com.griffith.feedreeder_3061874.ui.components.DynamicThemePrimaryColorsFromImage
import com.griffith.feedreeder_3061874.ui.components.rememberDominantColorState
import com.griffith.feedreeder_3061874.ui.home.discover.Discover
import com.griffith.feedreeder_3061874.ui.home.inbox.Inbox
import com.griffith.feedreeder_3061874.ui.theme.Feedreeder_3061874Theme
import com.griffith.feedreeder_3061874.ui.theme.MinContrastOfPrimaryVsSurface
import com.griffith.feedreeder_3061874.ui.theme.contrastAgainst
import com.griffith.feedreeder_3061874.ui.theme.keyline1
import com.griffith.feedreeder_3061874.ui.theme.quantityStringResource
import com.griffith.feedreeder_3061874.ui.theme.verticalGradientScrim
import kotlinx.collections.immutable.PersistentList
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Home(
    navigateToReader: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    Surface(Modifier.fillMaxWidth()) {
        HomeContent(
            featuredFeeds = viewState.featuredFeeds,
            isRefreshing = viewState.refreshing,
            homeCategories = viewState.homeCategories,
            selectedHomeCategory = viewState.selectedHomeCategory,
            onCategorySelected = viewModel::onHomeCategorySelected,
            onFeedUnfollowed = viewModel::onFeedUnFollowed,
            navigateToReader = navigateToReader,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    featuredFeeds: PersistentList<FeedsExtraInfo>,
    isRefreshing: Boolean,
    homeCategories: List<HomeCategory>,
    selectedHomeCategory: HomeCategory,
    onCategorySelected: (HomeCategory) -> Unit,
    onFeedUnfollowed: (String) -> Unit,
    navigateToReader: (String) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier.windowInsetsPadding(
            WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
        )
    ) {
        //  dynamically theme this sub-section of the layout to match the selected
        // 'top feeds'
        Log.w("HomeContent", "featuredFeeds: $featuredFeeds")
        val surfaceColor = MaterialTheme.colors.surface
        val appBarColor = surfaceColor.copy(alpha = 0.87f)
        val dominantColorState = rememberDominantColorState { color ->
            color.contrastAgainst(surfaceColor) >= MinContrastOfPrimaryVsSurface
        }
        DynamicThemePrimaryColorsFromImage(dominantColorState) {
            val pagerState = rememberPagerState { featuredFeeds.size }

            val selectedImageUrl = featuredFeeds.getOrNull(pagerState.currentPage)
                ?.feed?.imageUrl

            LaunchedEffect(selectedImageUrl) {
                if (selectedImageUrl != null) {
                    dominantColorState.updateColorsFromImageUrl(selectedImageUrl)
                } else {
                    dominantColorState.reset()
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalGradientScrim(
                        color = MaterialTheme.colors.primary.copy(alpha = 0.38f),
                        startYPercentage = 1f,
                        endYPercentage = 0f
                    )
            ) {
                Spacer(
                    Modifier
                        .background(appBarColor)
                        .fillMaxWidth()
                        .windowInsetsTopHeight(WindowInsets.statusBars)
                )
                    HomeAppBar(
                        backgroundColor = appBarColor,
                        modifier = Modifier.fillMaxWidth()
                    )

                /*

                    if (!featuredFeeds.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))

                        FollowedFeeds(
                            items = featuredFeeds,
                            pagerState = pagerState,
                            onFeedUnfollowed = onFeedUnfollowed,
                            modifier = Modifier
                                .padding(start = keyline1, top = 16.dp, end = keyline1)
                                .fillMaxWidth()
                                .height(200.dp)
                        )




                        Spacer(Modifier.height(16.dp))
                    }

                 */




            }
        }
        if (isRefreshing) {
            // TODO show a progress indicator loader
        }

        if (homeCategories.isNotEmpty()) {
            HomeCategoryTabs(
                categories = homeCategories,
                selectedCategory = selectedHomeCategory,
                onCategorySelected = onCategorySelected
            )
        }

        when (selectedHomeCategory) {
            HomeCategory.Library -> {
                Inbox(
                    navigateToReader = navigateToReader, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            HomeCategory.Discover -> {
                Discover(
                    navigateToReader = navigateToReader, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }

    }

}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FollowedFeeds(
    items: PersistentList<FeedsExtraInfo>,
    pagerState: PagerState,
    onFeedUnfollowed: (String) -> Unit,
    modifier: Modifier
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        val (feed, lastEpisodeDate) = items[page]
        FollowedFeedCarouselItem(
            feedImageUrl = feed.imageUrl,
            feedTitle = feed.title,
            onUnfollowedClick = { onFeedUnfollowed(feed.uri) },
            lastEpisodeDateText = lastEpisodeDate?.let { lastUpdated(it) },
            modifier = Modifier
                .padding(4.dp)
                .fillMaxHeight()
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun lastUpdated(updated: OffsetDateTime): String {
    val duration = Duration.between(updated.toLocalDateTime(), LocalDateTime.now())
    val days = duration.toDays().toInt()

    return when {
        days > 28 -> stringResource(R.string.updated_longer)
        days >= 7 -> {
            val weeks = days / 7
            quantityStringResource(R.plurals.updated_weeks_ago, weeks, weeks)
        }

        days > 0 -> quantityStringResource(R.plurals.updated_days_ago, days, days)
        else -> stringResource(R.string.updated_today)
    }
}


@Composable
fun FollowedFeedCarouselItem(
    feedImageUrl: String? = null,
    feedTitle: String? = null,
    onUnfollowedClick: () -> Unit,
    lastEpisodeDateText: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Box(
            Modifier
                .weight(1f)
                .align(Alignment.CenterHorizontally)
                .aspectRatio(1f)
        ) {
            if (feedImageUrl != null) {
                AsyncImage(
                    model = feedImageUrl,
                    contentDescription = feedTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium),
                )
            }
            ToggleFollowPodcastIconButton(
                onClick = onUnfollowedClick,
                isFollowed = true, /* All podcasts are followed in this feed */
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
        if (lastEpisodeDateText != null) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = lastEpisodeDateText,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun ToggleFollowPodcastIconButton(onClick: () -> Unit, isFollowed: Boolean, modifier: Modifier) {

}

@Composable
fun HomeCategoryTabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface
) {
    Spacer(
        modifier
            .padding(horizontal = 24.dp)
            .height(4.dp)
            .background(color, RoundedCornerShape(topStartPercent = 100, topEndPercent = 100))
    )
}


@Composable
fun HomeCategoryTabs(
    categories: List<HomeCategory>,
    selectedCategory: HomeCategory,
    onCategorySelected: (HomeCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = categories.indexOfFirst { it == selectedCategory }
    val indicator = @Composable { tabPositions: List<TabPosition> ->
        HomeCategoryTabIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedIndex])
        )
    }

    TabRow(
        selectedTabIndex = selectedIndex,
        indicator = indicator,
        modifier = modifier
    ) {
        categories.forEachIndexed { index, category ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onCategorySelected(category) },
                text = {
                    Text(
                        text = when (category) {
                            HomeCategory.Library -> stringResource(R.string.home_inbox)
                            HomeCategory.Discover -> stringResource(R.string.home_discover)
                        },
                        style = MaterialTheme.typography.body2
                    )
                }
            )
        }
    }
}

@Composable
fun HomeAppBar(
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row {

                Icon(
                    painter = painterResource(R.drawable.feedreeder),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .heightIn(max = 24.dp)
                )

                Image(
                    painter = painterResource(R.drawable.baseline_rss_feed_24),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 4.dp)
                )
            }
        },
        backgroundColor = backgroundColor,
        actions = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                IconButton(
                    onClick = { /* TODO: Open search */ }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(R.string.cd_search)
                    )
                }
                IconButton(
                    onClick = { /* TODO: Open account? */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = stringResource(R.string.cd_account)
                    )
                }
            }
        },
        modifier = modifier
    )

}


@Composable
@Preview
fun PreviewPodcastCard() {
    Feedreeder_3061874Theme {
        FollowedFeedCarouselItem(
            modifier = Modifier.size(128.dp),
            onUnfollowedClick = {},
            feedImageUrl = "https://mitadmissions.org/wp-content/uploads/2023/11/IMG_3971-800x797.jpg",
            feedTitle = "The Daily",
            lastEpisodeDateText = "1 day ago"

        )
    }
}
