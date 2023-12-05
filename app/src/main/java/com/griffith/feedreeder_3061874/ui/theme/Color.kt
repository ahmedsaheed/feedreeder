package com.griffith.feedreeder_3061874.ui.theme

import androidx.annotation.FloatRange
import androidx.annotation.PluralsRes
import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalContext
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow


const val MinContrastOfPrimaryVsSurface = 3f

@Composable
fun Colors.compositedOnSurface(alpha: Float): Color {
    return onSurface.copy(alpha = alpha).compositeOver(surface)
}


fun Color.contrastAgainst(background: Color): Float {
    val fg = if (alpha < 1f) compositeOver(background) else this

    val fgLuminance = fg.luminance() + 0.05f
    val bgLuminance = background.luminance() + 0.05f

    return max(fgLuminance, bgLuminance) / min(fgLuminance, bgLuminance)
}


val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
val Yellow800 = Color(0xFFF29F05)
val Red300 = Color(0xFFEA6D7E)
val FeedreederColors = darkColors(
    primary = Yellow800,
    onPrimary = Color.Black,
    primaryVariant = Yellow800,
    onSecondary = Color.Black,
    error = Red300,
    onError = Color.Black
)

fun Modifier.verticalGradientScrim(
    color: Color,
    @FloatRange(from = 0.0, to = 1.0) startYPercentage: Float = 0f,
    @FloatRange(from = 0.0, to = 1.0) endYPercentage: Float = 1f,
    decay: Float = 1.0f,
    numStops: Int = 16
) = this then VerticalGradientElement(color, startYPercentage, endYPercentage, decay, numStops)

private data class VerticalGradientElement(
    var color: Color,
    var startYPercentage: Float = 0f,
    var endYPercentage: Float = 1f,
    var decay: Float = 1.0f,
    var numStops: Int = 16
) : ModifierNodeElement<VerticalGradientModifier>() {
    fun createOnDraw(): DrawScope.() -> Unit {
        val colors = if (decay != 1f) {
            // If we have a non-linear decay, we need to create the color gradient steps
            // manually
            val baseAlpha = color.alpha
            List(numStops) { i ->
                val x = i * 1f / (numStops - 1)
                val opacity = x.pow(decay)
                color.copy(alpha = baseAlpha * opacity)
            }
        } else {
            // If we have a linear decay, we just create a simple list of start + end colors
            listOf(color.copy(alpha = 0f), color)
        }

        val brush =
            // Reverse the gradient if decaying downwards
            Brush.verticalGradient(
                colors = if (startYPercentage < endYPercentage) colors else colors.reversed(),
            )

        return {
            val topLeft = Offset(0f, size.height * min(startYPercentage, endYPercentage))
            val bottomRight =
                Offset(size.width, size.height * max(startYPercentage, endYPercentage))

            drawRect(
                topLeft = topLeft,
                size = Rect(topLeft, bottomRight).size,
                brush = brush
            )
        }
    }

    override fun create() = VerticalGradientModifier(createOnDraw())

    override fun update(node: VerticalGradientModifier) {
        node.onDraw = createOnDraw()
    }

    /**
     * Allow this custom modifier to be inspected in the layout inspector
     **/
    override fun InspectorInfo.inspectableProperties() {
        name = "verticalGradientScrim"
        properties["color"] = color
        properties["startYPercentage"] = startYPercentage
        properties["endYPercentage"] = endYPercentage
        properties["decay"] = decay
        properties["numStops"] = numStops
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private class VerticalGradientModifier(
    var onDraw: DrawScope.() -> Unit
) : Modifier.Node(), DrawModifierNode {

    override fun ContentDrawScope.draw() {
        onDraw()
        drawContent()
    }
}

@Composable
fun quantityStringResource(@PluralsRes id: Int, quantity: Int): String {
    val context = LocalContext.current
    return context.resources.getQuantityString(id, quantity)
}


@Composable
fun quantityStringResource(@PluralsRes id: Int, quantity: Int, vararg formatArgs: Any): String {
    val context = LocalContext.current
    return context.resources.getQuantityString(id, quantity, *formatArgs)
}
