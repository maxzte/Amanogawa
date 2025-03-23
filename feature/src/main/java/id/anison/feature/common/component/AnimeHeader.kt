package id.anison.feature.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import id.anison.feature.R
import id.anison.feature.anime.AnimeDetailsScreenEvents
import id.anison.feature.anime.model.AnimeDetailsUI
import id.anison.feature.common.component.button.CircleButton
import id.anison.feature.common.component.button.FavoriteButton

@Composable
fun AnimeHeader(
    anime: AnimeDetailsUI,
    isLandscape: Boolean,
    isFavorite: Boolean,
    onEvents: (AnimeDetailsScreenEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    var sizeImage by remember { mutableStateOf(IntSize.Zero) }
    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
        startY = sizeImage.height.toFloat() / 1.9f,
        endY = sizeImage.height.toFloat()
    )
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            MaterialTheme.colorScheme.background
        ),
    )
    Box(
        modifier = Modifier
            .heightIn(
                max = if (isLandscape) {
                    LocalConfiguration.current.screenHeightDp.dp + 32.dp
                } else {
                    Dp.Unspecified
                }
            )
            .fillMaxWidth()
    ) {
        if (!isLandscape) {
            CoilImageLoader(
                titleImage = anime.poster.toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(7f / 10f)
                    .onGloballyPositioned { sizeImage = it.size },
            )
            TitleActions(
                onBackClick = { onEvents(AnimeDetailsScreenEvents.PopBack) },
                actionIconContent = {
                    CircleButton(
                        onClick = {
                            onEvents(AnimeDetailsScreenEvents.ToggleFavorite(anime))
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            tint = if (isFavorite) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary,
                            contentDescription = if (isFavorite) stringResource(id = R.string.unliked)
                            else stringResource(id = R.string.liked),
                        )
                    }
                },
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(gradient)
            )
            Column(
                modifier = modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AnimeHeaderDetails(
                    anime = anime,
                    onEvents = onEvents,
                )
            }
        } else {
            CoilImageLoader(
                titleImage = anime.poster.toString(),
                radius = 30,
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(backgroundGradient)
            )
            TitleActions(
                onBackClick = { onEvents(AnimeDetailsScreenEvents.PopBack) },
                actionIconContent = {
                    FavoriteButton(
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            onEvents(AnimeDetailsScreenEvents.ToggleFavorite(anime))
                        }
                    )
                },
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
            ) {
                val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
                val isCompact =
                    windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
                val configuration = LocalConfiguration.current
                val componentHeight =
                    if (isCompact) (configuration.screenHeightDp / 3.5).toInt()
                    else (configuration.screenHeightDp / 1.3).toInt()
                var isActivePhoto by rememberSaveable { mutableStateOf<String?>(null) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .heightIn(max = componentHeight.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    ElevatedCard(
                        modifier = Modifier.clickable { isActivePhoto = anime.poster }
                    ) {
                        CoilImageLoader(
                            titleImage = anime.poster.toString(),
                            modifier = Modifier.aspectRatio(7f / 10f)
                        )
                    }
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        AnimeHeaderDetails(
                            anime = anime,
                            onEvents = onEvents,
                            isLandscape = isLandscape,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}