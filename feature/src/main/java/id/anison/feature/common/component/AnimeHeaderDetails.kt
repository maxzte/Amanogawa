package id.anison.feature.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.anison.feature.R
import id.anison.feature.anime.AnimeDetailsScreenEvents
import id.anison.feature.anime.model.AnimeDetailsUI
import id.anison.feature.search.model.FilterUI

@Composable
fun AnimeHeaderDetails(
    anime: AnimeDetailsUI,
    onEvents: (AnimeDetailsScreenEvents) -> Unit,
    isLandscape: Boolean = false,
    modifier: Modifier = Modifier
) {
    val regex = Regex("\\d{1,2}:\\d{2}:\\d{2}|\\d+")
    val duration = regex.find(anime.duration.toString())?.value

    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        UserRatingBar(rating = anime.rating)
        Text(
            text = anime.animeUI.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = anime.animeUI.enJpName,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
        )
    }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item { }
        item {
            OutlinedButton(
                onClick = {
                    onEvents(
                        AnimeDetailsScreenEvents.OnLinkClick(
                            filter = FilterUI(years = listOf(anime.animeUI.year)),
                            filterName = anime.animeUI.year.toString()
                        )
                    )
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(
                        alpha = 0.35f
                    )
                )
            ) {
                Text(text = anime.animeUI.year.toString())
            }
        }
        items(
            count = anime.genres.size,
            key = { index -> anime.genres[index].genreId }
        ) { index ->
            val genre = anime.genres[index]
            OutlinedButton(
                onClick = {
                    onEvents(
                        AnimeDetailsScreenEvents.OnLinkClick(
                            filter = FilterUI(genres = listOf(genre)),
                            filterName = genre.name
                        )
                    )
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(
                        alpha = 0.35f
                    )
                )
            ) {
                Text(text = genre.name)
            }
        }
        item { }
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val localUriHandler: UriHandler = LocalUriHandler.current
        FilledTonalButton(
            onClick = {
                localUriHandler.openUri(anime.botUrl + "?start=sttl_" + anime.botId)
            },
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = stringResource(R.string.play_icon),
            )
            Text(
                text = stringResource(R.string.watch_btn),
            )
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            OutlinedButton(
                onClick = {
                    localUriHandler.openUri(anime.torrentUrl.toString())
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(
                        alpha = 0.35f
                    )
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_download_24),
                    contentDescription = stringResource(R.string.download_icon),
                )
                if (isLandscape) {
                    Text(text = stringResource(R.string.download_btn))
                }
            }
            if (duration != null) {
                TextButton(
                    onClick = {},
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_access_time_24),
                        contentDescription = stringResource(R.string.duration),
                    )
                    Text(text = " $duration")
                }
            }
        }
    }
}