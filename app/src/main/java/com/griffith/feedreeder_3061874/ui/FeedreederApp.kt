package com.griffith.feedreeder_3061874.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.window.layout.DisplayFeature
import com.griffith.feedreeder_3061874.R
import com.griffith.feedreeder_3061874.ui.home.Home
import com.griffith.feedreeder_3061874.ui.reader.ReaderScreen
import com.griffith.feedreeder_3061874.ui.reader.ReaderViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FeedreederApp(
    windowSizeClass: WindowSizeClass,
    displayFeature: List<DisplayFeature>,
    appState: FeedReederAppState = rememberFeedReederAppState()
) {
    if(appState.isOnline) {
        NavHost(
            navController = appState.navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) { backStackEntry ->
                Home(
                    navigateToReader = { episodeUri ->
                        appState.navigateToReader(episodeUri, backStackEntry)
                    }
                )
            }
            composable(Screen.Reader.route) { backStackEntry ->
                val readerViewModel: ReaderViewModel = viewModel(
                    factory = ReaderViewModel.provideFactory(
                        owner = backStackEntry,
                        defaultArgs = backStackEntry.arguments
                    )
                )

                ReaderScreen(
                    readerViewModel,
                    windowSizeClass,
                    displayFeature,
                    onBackPress = appState::navigateBack
                )
            }
        }
    }else{
        OfflineMode {appState.refreshOnline()}
    }
}

@Composable
fun OfflineMode(onRetry: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = stringResource(R.string.connection_error_title)) },
        text = { Text(text = stringResource(R.string.connection_error_message)) },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.retry_label))
            }
        }
    )
}
