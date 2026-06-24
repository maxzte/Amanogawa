package id.anison.amanogawa.feature.search.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import id.anison.amanogawa.core.model.SearchResultUI
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaConst
import id.anison.amanogawa.core.ui.spacing
import id.anison.amanogawa.core.ui.common.AmanogawaIcon
import id.anison.amanogawa.core.ui.common.isLandscape
import id.anison.amanogawa.feature.search.screenmodel.SearchScreenEvents

@Composable
internal fun SearchHistory(
    searchHistory: List<SearchResultUI>,
    onEvents: (SearchScreenEvents) -> Unit,
    lazyListState: LazyListState,
) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = if (!isLandscape()) AmanogawaConst.bottomBarInsets else AmanogawaConst.bottomBarPaddings
        ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        item(
            key = "searchHistoryHeader",
            content = {
                SearchHistoryHeader(
                    isSearchHistoryExist = searchHistory.isNotEmpty(),
                    onClearHistoryClick = {
                        onEvents(SearchScreenEvents.OnDeleteAllButtonClick)
                    }
                )
            }
        )

        items(
            items = searchHistory,
            key = { it.animeId },
            itemContent = { anime ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            onClick = {
                                onEvents(
                                    SearchScreenEvents.OnAnimeClick(
                                        animeId = anime.animeId,
                                        anime = anime
                                    )
                                )
                            }
                        )
                ) {
                    SearchHistoryItem(
                        anime = anime,
                        onDeleteItemClick = {
                            onEvents(SearchScreenEvents.OnDeleteAnimeFromHistory(anime))
                        }
                    )
                }
            }
        )
    }
}

@Composable
private fun SearchHistoryHeader(
    onClearHistoryClick: () -> Unit,
    isSearchHistoryExist: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.search_history),
            style = MaterialTheme.typography.titleMedium
        )
        TextButton(
            onClick = { onClearHistoryClick() },
            enabled = isSearchHistoryExist,
            content = {
                Text(
                    text = stringResource(id = R.string.clear_all),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        )
    }
}

@Composable
private fun SearchHistoryItem(
    anime: SearchResultUI,
    onDeleteItemClick: () -> Unit,
) {
    val seasonText = anime.season.takeIf { it != 0.toByte() }?.let { "Season $it" }
    val partText = anime.part.takeIf { it != 0.toByte() }?.let { "Part $it" }

    val subtitle = buildString {
        when {
            !seasonText.isNullOrBlank() && !partText.isNullOrBlank() -> append("$seasonText · $partText")
            !seasonText.isNullOrBlank() -> append(seasonText)
            !partText.isNullOrBlank() -> append(partText)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = AmanogawaIcon.history,
            contentDescription = stringResource(id = R.string.history_icon),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
        ) {
            Text(
                text = anime.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = anime.enJpName,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                color = Color.Gray,
            )
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray,
                )
            }
        }
        IconButton(
            onClick = { onDeleteItemClick() },
            content = {
                Icon(
                    imageVector = AmanogawaIcon.clear,
                    contentDescription = stringResource(id = R.string.clear_icon),
                )
            }
        )
    }

}