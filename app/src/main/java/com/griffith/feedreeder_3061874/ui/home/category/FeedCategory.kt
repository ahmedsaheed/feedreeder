package com.griffith.feedreeder_3061874.ui.home.category

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.griffith.feedreeder_3061874.R
import com.griffith.feedreeder_3061874.data.Episode
import com.griffith.feedreeder_3061874.data.EpisodeToFeed
import com.griffith.feedreeder_3061874.data.FeedCollection
import com.griffith.feedreeder_3061874.data.FeedsExtraInfo
import com.griffith.feedreeder_3061874.helper.viewModelProviderFactoryOf
import com.griffith.feedreeder_3061874.ui.theme.keyline1
import com.griffith.feedreeder_3061874.ui.components.ToggleFollowFeedsIconButton
import com.griffith.feedreeder_3061874.ui.home.PreviewEpisodes
import com.griffith.feedreeder_3061874.ui.home.PreviewPodcasts
import com.griffith.feedreeder_3061874.ui.theme.Feedreeder_3061874Theme
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FeedCategory(
    categoryId: Long,
    navigateToReader: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: FeedCategoryViewModel = viewModel(
        key = "category_list_$categoryId",
        factory = viewModelProviderFactoryOf { FeedCategoryViewModel(categoryId) }
    )

    val viewState by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        CategoryFeeds(viewState.topFeeds, viewModel)
        EpisodeList(viewState.episode, navigateToReader)
    }
}
@Composable
fun CategoryFeeds(topFeeds: List<FeedsExtraInfo>, viewModel: FeedCategoryViewModel) {
    CategoryFeedsRow(
        feeds = topFeeds,
        onToggleFeedFollowed = viewModel::onTogglePodcastFollowed,
        modifier = Modifier.fillMaxWidth()
    )
}
@Composable
fun CategoryFeedsRow(
    feeds: List<FeedsExtraInfo>,
    onToggleFeedFollowed: (String) -> Unit,
    modifier: Modifier
) {
    val lastIdx = feeds.size - 1
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(start = keyline1, top = 8.dp, end = keyline1, bottom = 24.dp)
    ) {
        itemsIndexed(items = feeds) { index: Int,
                                      (feed, _, isFollowed): FeedsExtraInfo ->
            TopFeedRowItem(
                feedTitle = feed.title,
                feedImageUrl = feed.imageUrl,
                isFollowed = isFollowed,
                onToggleFollowClicked = { onToggleFeedFollowed(feed.uri) },
                modifier = Modifier.width(128.dp)
            )
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EpisodeList(
    episodes: List<EpisodeToFeed>,
    navigateToReader: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.Center
    ) {
        items(episodes, key = { it.episode.uri }) { item ->
            EpisodeListItem(
                episode = item.episode,
                feed = item.feed,
                onClick = navigateToReader,
                modifier = Modifier.fillParentMaxWidth()
            )
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EpisodeListItem(
    episode: Episode,
    feed: FeedCollection,
    onClick: (String) -> Unit,
    modifier: Modifier
) {
    ConstraintLayout(modifier = modifier.clickable { onClick(episode.uri) }) {

        val (
            divider, episodeTitle, feedTitle, image, playIcon,
            date, addPlaylist, overflow
        ) = createRefs()
        Divider(
            Modifier.constrainAs(divider) {
                top.linkTo(parent.top)
                centerHorizontallyTo(parent)

                width = Dimension.fillToConstraints
            }
        )

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(feed.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.medium)
                .constrainAs(image) {
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(parent.top, 16.dp)
                },
        )


        Text(
            text = episode.title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.constrainAs(episodeTitle) {
                linkTo(
                    start = parent.start,
                    end = image.start,
                    startMargin = keyline1,
                    endMargin = 16.dp,
                    bias = 0f
                )
                top.linkTo(parent.top, 16.dp)
                height = Dimension.preferredWrapContent
                width = Dimension.preferredWrapContent
            }
        )

        val titleImageBarrier = createBottomBarrier(feedTitle, image)
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = feed.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.constrainAs(feedTitle) {
                    linkTo(
                        start = parent.start,
                        end = image.start,
                        startMargin = keyline1,
                        endMargin = 16.dp,
                        bias = 0f
                    )
                    top.linkTo(episodeTitle.bottom, 6.dp)
                    height = Dimension.preferredWrapContent
                    width = Dimension.preferredWrapContent
                }
            )

            Image(
                imageVector = Icons.Rounded.AutoStories,
                contentDescription = stringResource(R.string.cd_read),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(LocalContentColor.current),
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false, radius = 24.dp)
                    ) { /* TODO */ }
                    .size(32.dp)
                    .padding(6.dp)
                    .semantics { role = Role.Button }
                    .constrainAs(playIcon) {
                        start.linkTo(parent.start, keyline1)
                        top.linkTo(titleImageBarrier, margin = 10.dp)
                        bottom.linkTo(parent.bottom, 10.dp)
                    }
            )

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = when {
                        episode.duration != null -> {
                            //TODO add icon
                            stringResource(
                                R.string.episode_date_duration,
                                MediumDateFormatter.format(episode.published),
                                episode.duration.toMinutes().toInt()
                            )
                        }
                        // Otherwise we just use the date
                        else -> MediumDateFormatter.format(episode.published)
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.constrainAs(date) {
                        centerVerticallyTo(playIcon)
                        linkTo(
                            start = playIcon.end,
                            startMargin = 12.dp,
                            end = addPlaylist.start,
                            endMargin = 16.dp,
                            bias = 0f // float this towards the start
                        )
                        width = Dimension.preferredWrapContent
                    }
                )


                IconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.constrainAs(addPlaylist) {
                        end.linkTo(overflow.start)
                        centerVerticallyTo(playIcon)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = stringResource(R.string.cd_bookmark)
                    )
                }

                IconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.constrainAs(overflow) {
                        end.linkTo(parent.end, 8.dp)
                        centerVerticallyTo(playIcon)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.cd_more)
                    )

                }

            }
        }

    }

}

    @Composable
    fun TopFeedRowItem(
        feedTitle: String,
        feedImageUrl: String?,
        isFollowed: Boolean,
        onToggleFollowClicked: () -> Unit,
        modifier: Modifier
    ) {
        Column(
            modifier.semantics(mergeDescendants = true) {}
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .align(Alignment.CenterHorizontally)
            ) {
                if (feedImageUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(feedImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.medium)
                    )
                }
                ToggleFollowFeedsIconButton(
                    onClick = onToggleFollowClicked,
                    isFollowed = isFollowed,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
            Text(
                text = feedTitle,
                style = MaterialTheme.typography.body2,
                maxLines = 2,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            )
        }
    }


public val MediumDateFormatter by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    } else {
        TODO("VERSION.SDK_INT < O")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun PreviewEpisodeListItem() {
    Feedreeder_3061874Theme {
        EpisodeListItem(
            episode = PreviewEpisodes[0],
            feed = PreviewPodcasts[0],
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
