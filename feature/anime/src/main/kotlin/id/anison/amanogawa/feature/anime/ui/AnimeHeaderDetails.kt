package id.anison.amanogawa.feature.anime.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import id.anison.amanogawa.core.model.AnimeDetailsUI
import id.anison.amanogawa.core.model.FilterTypeUI
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaIcon
import id.anison.amanogawa.core.ui.common.component.UserRatingBar
import id.anison.amanogawa.core.ui.common.component.layout.showWebPage
import id.anison.amanogawa.core.ui.common.ignoreHorizontalParentPadding
import id.anison.amanogawa.feature.anime.AnimeDetailsScreenEvents

@Composable
fun AnimeHeaderDetails(
    anime: AnimeDetailsUI,
    onEvents: (AnimeDetailsScreenEvents) -> Unit,
    isLandscape: Boolean = false,
) {
//    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.surface.toArgb()

    Column(
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        UserRatingBar(rating = anime.rating)

        Text(
            text = anime.animeUI.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = anime.animeUI.enJpName,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        LazyRow(
            modifier = Modifier.ignoreHorizontalParentPadding(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item(
                key = "yearButton"
            ) {
                GenreButton(text = anime.animeUI.year) {
                    onEvents(
                        AnimeDetailsScreenEvents.OnLinkClick(
                            filterId = null,
                            filterName = anime.animeUI.year,
                            filterType = FilterTypeUI.YEAR
                        )
                    )
                }
            }

            items(items = anime.genres, key = { it.genreId }) { genre ->
                GenreButton(text = genre.name) {
                    onEvents(
                        AnimeDetailsScreenEvents.OnLinkClick(
                            filterId = genre.genreId,
                            filterName = genre.name,
                            filterType = FilterTypeUI.GENRE
                        )
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButton(
                onClick = {
                    showWebPage(
                        context = context,
                        uri = "${anime.botUrl}?start=sttl_${anime.botId}".toUri(),
                        toolbarColor = toolbarColor
                    )
//                    uriHandler.openUri("${anime.botUrl}?start=sttl_${anime.botId}")
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = AmanogawaIcon.playArrow,
                    contentDescription = stringResource(id = R.string.play_icon)
                )
                Text(text = stringResource(id = R.string.watch_btn))
            }

            Row(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedIconButton(
                    icon = painterResource(id = R.drawable.ic_download_24),
                    text = if (isLandscape) {
                        stringResource(id = R.string.download_btn)
                    } else null
                ) {
                    showWebPage(
                        context = context,
                        uri = anime.torrentUrl.toString().toUri(),
                        toolbarColor = toolbarColor
                    )
//                    uriHandler.openUri(anime.torrentUrl.toString())
                }

                if (anime.duration.isNotEmpty()) {
                    TextButton(onClick = {}, contentPadding = PaddingValues(0.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_access_time_24),
                            contentDescription = stringResource(id = R.string.duration)
                        )
                        Text(text = " ${anime.duration}")
                    }
                }
            }
        }
    }
}

@Composable
private fun GenreButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun OutlinedIconButton(
    icon: Painter,
    text: String? = null,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f)
        )
    ) {
        Icon(painter = icon, contentDescription = null)
        if (!text.isNullOrBlank()) {
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}
