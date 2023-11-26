package com.griffith.feedreeder_3061874.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import coil.network.HttpException
import com.rometools.modules.cc.CreativeCommons
import com.rometools.modules.itunes.EntryInformation
import com.rometools.rome.feed.synd.SyndCategory
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndFeed
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
import java.net.URL
import java.time.Duration
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

class FeedFetcher(
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
        val request = Request.Builder()
            .url(url)
            .cacheControl(cacheControl)
            .build()

        val response = okHttpClient.newCall(request).await()
        Log.w("responseFromHttpReq", response.toString())
        if (!response.isSuccessful) throw HttpException(response)
        return withContext(ioDispatcher) {
            response.body!!.use { body ->
                syndFeedInput.build(body.charStream()).toFeedResponse(url)
            }
        }
    }
}

fun feedIcon(link: String): String = "https://icon.horse/icon/${URL(link).host!!}"
private fun toCategories(name : String): List<Category> =
    listOf(Category(name = name))

@RequiresApi(Build.VERSION_CODES.O)
private fun SyndFeed.toFeedResponse(feedUrl: String): FeedRssResponse {
    val feedurl = uri ?: feedUrl
    val podcast = FeedCollection(
        uri = feedurl,
        title = title,
        description = description,
        author = author,
        copyright = copyright,
        imageUrl = feedIcon(feedurl)
    )
    Log.d("Categories", categories.toString())
    val episodes = entries.map { it.toEpisode(feedurl) }
    val categories = toCategories(title).toSet()

    return FeedRssResponse.Success(podcast, episodes, categories)
}

/**
 * Map a Rome [SyndEntry] instance to our own [Episode] data class.
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun SyndEntry.toEpisode(episodeUri: String): Episode {
    val content = contents.firstOrNull()
    val entryInformation = getModule(CreativeCommons.URI) as EntryInformation?
    val ep = Episode(
        uri = uri,
        episodeUri = episodeUri,
        title = title,
        author = author,
        summary = entryInformation?.summary ?: description?.value ?: "Hi Mom",
        subtitle = entryInformation?.subtitle,
        published = publishedDate.toInstant().atOffset(ZoneOffset.UTC),
        duration = entryInformation?.duration?.milliseconds?.let { Duration.ofMillis(it) },
        content = content?.value
    )
    Log.w("episodes", ep.toString())
    Log.w("entry", entryInformation.toString())
    Log.w("content availability", content.toString())
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

