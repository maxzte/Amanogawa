package id.anison.amanogawa.feature.anime.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import id.anison.amanogawa.core.model.AnimeDetailsUI
import id.anison.amanogawa.core.ui.common.component.AnimeImageLoader
import id.anison.amanogawa.core.ui.common.isLandscape
import id.anison.amanogawa.core.ui.spacing
import id.anison.amanogawa.feature.anime.AnimeDetailsScreenEvents

@Composable
fun AnimeHeader(
    anime: AnimeDetailsUI,
    onEvents: (AnimeDetailsScreenEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val screenHeight = with(density) { windowInfo.containerSize.height.toDp() }
    var imageSize by remember { mutableStateOf(IntSize.Zero) }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
        startY = imageSize.height.toFloat() / 1.9f,
        endY = imageSize.height.toFloat()
    )

    val background = Brush.verticalGradient(
        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
    )

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
    ) {
        if (isLandscape()) {
            PosterImage(
                modifier = Modifier
                    .height(screenHeight + MaterialTheme.spacing.large)
                    .onGloballyPositioned { imageSize = it.size },
                imageUrl = anime.animeUI.posterMini,
                radius = 10,
                backgroundContent = {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(background)
                    )
                }
            )

            LandscapeContent(anime, onEvents)
        } else {
            PosterImage(
                imageUrl = anime.poster,
                onSize = { imageSize = it },
                backgroundContent = {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(gradient)
                    )
                },
                modifier = Modifier.aspectRatio(7f / 10f)
            )

            AnimeHeaderDetails(anime = anime, onEvents = onEvents)
        }
    }
}

@Composable
private fun PosterImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    radius: Int = 0,
    onSize: ((IntSize) -> Unit)? = null,
    backgroundContent: @Composable (() -> Unit)? = null
) {
    Box(modifier = modifier) {
        AnimeImageLoader(
            titleImage = imageUrl,
            radius = radius,
            modifier = Modifier
                .then(
                    if (onSize != null) Modifier.onGloballyPositioned {
                        onSize(it.size)
                    } else Modifier
                )
        )
        backgroundContent?.invoke()
    }
}


@Composable
private fun LandscapeContent(
    anime: AnimeDetailsUI,
    onEvents: (AnimeDetailsScreenEvents) -> Unit
) {
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val screenHeight = with(density) { windowInfo.containerSize.height.toDp() } - 96.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = MaterialTheme.spacing.medium)
            .heightIn(max = screenHeight),
        verticalAlignment = Alignment.Bottom
    ) {
        ElevatedCard(
            modifier = Modifier
                .aspectRatio(7f / 10f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                       onEvents(AnimeDetailsScreenEvents.OnImageClick(0, listOf(anime.poster)))
                    }
                )
        ) {
            AnimeImageLoader(
                titleImage = anime.poster,
            )
        }

        AnimeHeaderDetails(anime = anime, onEvents = onEvents, isLandscape = true)
    }
}
