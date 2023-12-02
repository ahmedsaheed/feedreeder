package com.griffith.feedreeder_3061874.ui.reader

import androidx.compose.foundation.clickable
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.res.ResourcesCompat
import androidx.window.layout.DisplayFeature
import com.griffith.feedreeder_3061874.R
import com.griffith.feedreeder_3061874.ui.theme.verticalGradientScrim
import org.sufficientlysecure.htmltextview.HtmlFormatter
import org.sufficientlysecure.htmltextview.HtmlFormatterBuilder
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter
import org.sufficientlysecure.htmltextview.HtmlTextView
import org.sufficientlysecure.htmltextview.OnClickATagListener
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel,
    windowSizeClass: WindowSizeClass,
    displayFeature: List<DisplayFeature>,
    onBackPress: () -> Unit,
) {
    val uiState = viewModel.uiState
    ReaderScreen(
        uiState = uiState,
        windowSizeClass = windowSizeClass,
        displayFeature = displayFeature,
        onBackPress = onBackPress
    )
}

@Composable
fun HtmlRender(
    modifier: Modifier = Modifier,
    html: String,
    textStyle: TextStyle = MaterialTheme.typography.subtitle1,
    onLink1Clicked: (() -> Unit)? = null,
    onLink2Clicked: (() -> Unit)? = null
) {

    val ctx = LocalContext.current
    val fontResId = when (textStyle.fontWeight) {
        FontWeight.Medium -> R.font.montserrat_medium
        else -> R.font.montserrat_regular
    }
    val font = ResourcesCompat.getFont(ctx, fontResId)
    AndroidView(
        factory = { ctx ->
            val gravity = when (textStyle.textAlign) {
                TextAlign.Center -> Gravity.CENTER
                TextAlign.End -> Gravity.END
                else -> Gravity.START
            }
            val fontResId = when (textStyle.fontWeight) {
                FontWeight.Medium -> R.font.montserrat_medium
                else -> R.font.montserrat_regular
            }
            val font = ResourcesCompat.getFont(ctx, fontResId)

            HtmlTextView(ctx).apply {
                textSize = textStyle.fontSize.value
                setLineSpacing(5f, 1f)
                setTextColor(Color.White.toArgb())
                setLinkTextColor(Color.Magenta.toArgb())
                setGravity(gravity)
                setOnClickATagListener(OnClickATagListener { widget, spannedText, href ->
                    Log.w("LinkClicked", "Link: $href")
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(href)

                    if (!href?.contains("http")!!) {
                        Toast.makeText(ctx, "This is not a valid link", Toast.LENGTH_SHORT).show()
                        false
                    } else {
                        startActivity(ctx, intent, null)
                        true
                    }
                })
                typeface = font
                val imageGetter = HtmlHttpImageGetter(this)
                val formattedHtml = HtmlFormatter.formatHtml(
                    HtmlFormatterBuilder().setHtml(html)
                        .setImageGetter(imageGetter)
                )
                text = formattedHtml
            }
        },

        )

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ReaderScreen(
    uiState: ReaderUiState,
    windowSizeClass: WindowSizeClass,
    displayFeature: List<DisplayFeature>,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier) {
        if (uiState.feedName.isNotEmpty()) {
            ReaderContent(
                uiState = uiState,
                windowSizeClass = windowSizeClass,
                displayFeature = displayFeature,
                onBackPress = onBackPress
            )
        } else {
            FullScreenLoading()
        }
    }
}

@Composable
fun FullScreenLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReaderContent(
    uiState: ReaderUiState,
    windowSizeClass: WindowSizeClass,
    displayFeature: List<DisplayFeature>,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    ReaderForContent(uiState, onBackPress, modifier)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReaderForContent(uiState: ReaderUiState, onBackPress: () -> Unit, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalGradientScrim(
                color = Color.Black,
                startYPercentage = 1f,
                endYPercentage = 0f
            )
            .systemBarsPadding()
            .padding(horizontal = 8.dp)
    ) {
        TopAppBar(onBackPress = onBackPress, contentUri = uiState.contentUri)
        Column(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            FeedDescription(uiState.title, uiState.publishedDate!!, uiState.feedName, uiState.contentUri)
            Spacer(modifier = Modifier.height(12.dp))
            Column {
                FeedContent(uiState.content)
            }
        }
    }
}

@Composable
fun FeedContent(content: String?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState())
            // make the bg color the same as the surface color
            .clip(MaterialTheme.shapes.large)
            .background(Color.Transparent)
        // make text color white
    ) {
        content?.let { FeedContentDisplay(html = it) }
    }
}

@Composable
fun FeedContentDisplay(html: String, modifier: Modifier = Modifier) {
    HtmlRender(html = html)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FeedDescription(
    title: String,
    publishedDate: OffsetDateTime,
    feedName: String,
    contentUri: String,
    titleTextStyle: TextStyle = MaterialTheme.typography.h5
) {
    val ctx = LocalContext.current
    Box(
        modifier = Modifier
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(contentUri)
                startActivity(ctx, intent, null)
            }
            .padding(8.dp)
            .clip(MaterialTheme.shapes.medium)
    ) {
        Column {

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(publishedDate)
                        .uppercase(),
                    style = MaterialTheme.typography.overline,
                    modifier = Modifier.padding(start = 8.dp),
                    maxLines = 1
                )
            }
            Text(
                text = title,
                style = titleTextStyle,
                modifier = Modifier.padding(start = 8.dp)

            )
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = feedName.uppercase(),
                    style = MaterialTheme.typography.overline,
                    modifier = Modifier.padding(start = 8.dp),
                    maxLines = 1
                )

            }
        }
    }
}


@Composable
private fun TopAppBar(onBackPress: () -> Unit, contentUri: String) {
    val ctx = LocalContext.current
    Row(Modifier.fillMaxWidth()) {
        IconButton(onClick = onBackPress) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(R.string.cd_back)
            )
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Default.PlaylistAdd,
                contentDescription = stringResource(R.string.cd_add)
            )
        }
        IconButton(onClick = {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Hey, check out this article: $contentUri")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(ctx, shareIntent, null)
        }) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = stringResource(R.string.cd_share)
            )
        }
    }
}
