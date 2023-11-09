package com.griffith.feedreeder_3061874.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver


const val MinContrastOfPrimaryVsSurface = 3f

@Composable
fun Colors.compositedOnSurface(alpha: Float): Color {
    return onSurface.copy(alpha = alpha).compositeOver(surface)
}

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
val Yellow800 = Color(0xFFF29F05)
val Red300 = Color(0xFFEA6D7E)
val FeedreederColors  = darkColors(
    primary = Yellow800,
    onPrimary = Color.Black,
    primaryVariant = Yellow800,
    onSecondary = Color.Black,
    error = Red300,
    onError = Color.Black
)