package id.anison.amanogawa.core.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp


fun Modifier.grayLine(side: LineSide) = drawWithContent {
    drawContent()
    val (start, end) = when (side) {
        LineSide.Top -> {
            Offset(x = 0f, y = 0f) to Offset(x = size.width, y = 0f)
        }

        LineSide.Bottom -> {
            Offset(x = 0f, y = size.height) to Offset(x = size.width, y = size.height)
        }

        LineSide.Left -> {
            Offset(x = 0f, y = size.width) to Offset(x = size.width, y = size.height)
        }

        LineSide.Right -> {
            Offset(x = size.width, y = 0f) to Offset(x = size.width, y = size.height)
        }
    }
    drawLine(
        color = Color.Gray,
        start = start,
        end = end,
        strokeWidth = Stroke.HairlineWidth
    )
}

enum class LineSide {
    Top, Bottom, Left, Right
}

fun Modifier.ignoreHorizontalParentPadding(horizontal: Dp): Modifier {
    return this.layout { measurable, constraints ->
        val overriddenWidth = constraints.maxWidth + 2 * horizontal.roundToPx()
        val placeable =
            measurable.measure(constraints = constraints.copy(maxWidth = overriddenWidth))
        layout(width = placeable.width, height = placeable.height) {
            placeable.place(x = 0, y = 0)
        }
    }
}

fun Modifier.bounceClick() = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(
        targetValue = if (buttonState == ButtonState.Pressed) {
            0.95f
        } else {
            1f
        },
        label = "onPressScaleValue"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { }
        )
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }
}

enum class ButtonState { Pressed, Idle }
