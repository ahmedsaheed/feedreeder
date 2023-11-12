package com.griffith.feedreeder_3061874.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.google.accompanist.adaptive.calculateDisplayFeatures
import com.griffith.feedreeder_3061874.Graph
import com.griffith.feedreeder_3061874.ui.FeedreederApp
import com.griffith.feedreeder_3061874.ui.theme.Feedreeder_3061874Theme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Graph.provide(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val displayFeatures = calculateDisplayFeatures(this)

            Feedreeder_3061874Theme {
                // A surface container using the 'background' color from the theme
                FeedreederApp(windowSizeClass, displayFeatures)
            }
        }
    }
}
