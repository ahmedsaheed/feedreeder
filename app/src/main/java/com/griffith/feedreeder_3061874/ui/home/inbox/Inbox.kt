package com.griffith.feedreeder_3061874.ui.home.inbox

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.griffith.feedreeder_3061874.LocalStorage
import com.griffith.feedreeder_3061874.R
import com.griffith.feedreeder_3061874.data.Episode
import com.griffith.feedreeder_3061874.data.FeedCollection
import com.griffith.feedreeder_3061874.data.feedIcon
import com.griffith.feedreeder_3061874.ui.home.category.EpisodeListItem
import com.griffith.feedreeder_3061874.ui.home.category.MediumDateFormatter
import com.griffith.feedreeder_3061874.ui.theme.keyline1
import java.net.URL

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Inbox(
    navigateToReader : (String) -> Unit,
    modifier : Modifier
) {
    val viewModel : InboxViewModel = viewModel()
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        InboxList(episodes = viewState.inboxEpisodes, navigateToReader = navigateToReader)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InboxList(
    episodes : List<Episode>,
    navigateToReader : (String) -> Unit,
    ){
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.Center
        ) {
        items(episodes, key = { it.uri }) { item ->
            InboxListItem(
                episode = item,
                onClick = navigateToReader,
                modifier = Modifier.fillParentMaxWidth()
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InboxListItem(
    episode: Episode,
    onClick: (String) -> Unit,
    modifier: Modifier
) {

    val imageUrl = feedIcon(episode.episodeUri)
    val ctx = LocalContext.current
    val localStorage = LocalStorage(ctx)
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
                .data(imageUrl)
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
                text = episode.title,
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

            IconButton(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false, radius = 18.dp)
                    ) { /* TODO */ }
                    .constrainAs(playIcon) {
                        start.linkTo(parent.start, keyline1)
                        top.linkTo(titleImageBarrier, margin = 10.dp)
                        bottom.linkTo(parent.bottom, 10.dp)
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = stringResource(R.string.cd_more)
                )

            }

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {

                val progress = localStorage.getReadingProgress(episode.uri)
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
                        else -> if (progress != null) {
                            MediumDateFormatter.format(episode.published) + " • " + progress + "% read"
                        } else {
                            MediumDateFormatter.format(episode.published)
                        }
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
