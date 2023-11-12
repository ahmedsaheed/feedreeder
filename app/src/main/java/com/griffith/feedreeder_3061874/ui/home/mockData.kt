package com.griffith.feedreeder_3061874.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import com.griffith.feedreeder_3061874.data.Category
import com.griffith.feedreeder_3061874.data.Episode
import com.griffith.feedreeder_3061874.data.FeedCollection
import com.griffith.feedreeder_3061874.data.FeedsExtraInfo
import java.time.OffsetDateTime
import java.time.ZoneOffset


val PreviewCategories = listOf(
    Category(name = "Crime"),
    Category(name = "News"),
    Category(name = "Comedy")
)

val PreviewPodcasts = listOf(
    FeedCollection(
        uri = "fakeUri://podcast/1",
        title = "Android Developers Backstage",
        author = "Android Developers"
    ),
    FeedCollection(
        uri = "fakeUri://podcast/2",
        title = "Google Developers podcast",
        author = "Google Developers"
    )
)

@RequiresApi(Build.VERSION_CODES.O)
val PreviewPodcastsWithExtraInfo = PreviewPodcasts.mapIndexed { index, podcast ->
    FeedsExtraInfo().apply {
        this.feed = podcast
        this.lastEpisodeDate = OffsetDateTime.now()
        this.isFollowed = index % 2 == 0
    }
}

@RequiresApi(Build.VERSION_CODES.O)
val PreviewEpisodes = listOf(
    Episode(
        uri = "fakeUri://episode/1",
        episodeUri = PreviewPodcasts[0].uri,
        title = "Episode 140: Bubbles!",
        summary = "In this episode, Romain, Chet and Tor talked with Mady Melor and Artur " +
                "Tsurkan from the System UI team about... Bubbles!",
        published = OffsetDateTime.of(
            2020, 6, 2, 9,
            27, 0, 0, ZoneOffset.of("-0800")
        )
    )
)
