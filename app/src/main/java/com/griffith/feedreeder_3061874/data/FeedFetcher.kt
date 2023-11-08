package com.griffith.feedreeder_3061874.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import coil.network.HttpException
import com.rometools.modules.itunes.EntryInformation
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndFeed
import java.util.concurrent.TimeUnit
import com.rometools.rome.io.SyndFeedInput
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Duration
import java.time.ZoneOffset

class FeedFetcher (
    private val okHttpClient: OkHttpClient,
    private val syndFeedInput: SyndFeedInput,
    private val ioDispatcher: CoroutineDispatcher
) {
    private val cacheControl by lazy {
        CacheControl.Builder().maxStale(8, TimeUnit.HOURS).build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(feedUrls: List<String>): Flow<FeedRssResponse> {
        return feedUrls.asFlow()
            .flatMapMerge { feedUrl ->
                flow {
                    emit(fetchFeed(feedUrl))
                }.catch { e ->
                    emit(FeedRssResponse.Error(e))
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun fetchFeed(url: String): FeedRssResponse {
        val request =  Request.Builder()
            .url(url)
            .cacheControl(cacheControl)
            .build()

        val response = okHttpClient.newCall(request).await()

        if (!response.isSuccessful) throw HttpException(response)
        return withContext(ioDispatcher) {
            response.body!!.use {
                body ->
                syndFeedInput.build(body.charStream()).toFeedResponse(url)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
private fun SyndFeed.toFeedResponse(feedUrl: String): FeedRssResponse {
    val podcastUri = uri ?: feedUrl
    val episodes = entries.map { it.toEpisode(podcastUri) }
    Log.w("feedUrl", episodes.toString());
//    val rssFaviconUrl = removeUriFromUrl(podcastUri) + "/favicon.ico"
//    Log.w("rss_favicon", rssFaviconUrl);
    val imagePath = icon.url ?: image.url
    val podcast = FeedCollection(
        uri = podcastUri,
        title = title,
        description =  description,
        author = author,
        copyright = copyright,
        imageUrl =  imagePath
    )
    val categories = entries.map { it.categories }.flatten().map { Category(0, it.name) }.toSet()
    Log.w("categories", categories.toString());
    Log.w("essays", podcast.toString());
    return FeedRssResponse.Success(podcast, episodes, categories)
}

/**
 * Map a Rome [SyndEntry] instance to our own [Episode] data class.
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun SyndEntry.toEpisode(EpisodeUri: String): Episode {
    val content = contents.firstOrNull()
    val entryInformation = getModule(PodcastModuleDtd) as? EntryInformation
    val ep =  Episode(
        uri = uri,
        episodeUri = EpisodeUri,
        title = title,
        author = author,
        summary = entryInformation?.summary ?: description?.value ?: "Hi Mom",
        subtitle = entryInformation?.subtitle,
        published = publishedDate.toInstant().atOffset(ZoneOffset.UTC),
        duration = entryInformation?.duration?.milliseconds?.let { Duration.ofMillis(it) },
        content = content?.value

    )


    Log.w("episodes", ep.toString());
    Log.w("entry", entryInformation.toString());
    Log.w("content availability", content.toString());
    return ep
}
sealed class FeedRssResponse {
    data class Error(
        val throwable: Throwable?,
    ) : FeedRssResponse()

    data class Success(
        val podcast: FeedCollection,
        val episodes: List<Episode>,
        val categories: Set<Category>
    ) : FeedRssResponse()
}


/**
 * Most feeds use the following DTD to include extra information related to
 * their podcast. Info such as images, summaries, duration, categories is sometimes only available
 * via this attributes in this DTD.
 */
private const val PodcastModuleDtd = "http://www.itunes.com/dtds/podcast-1.0.dtd"
private const val ContentModuleDtd = "http://purl.org/rss/1.0/modules/content/"