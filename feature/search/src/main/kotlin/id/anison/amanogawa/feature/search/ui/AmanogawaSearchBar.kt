@file:OptIn(ExperimentalMaterial3Api::class)

package id.anison.amanogawa.feature.search.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaIcon
import id.anison.amanogawa.core.ui.spacing
import id.anison.amanogawa.core.ui.common.LineSide
import id.anison.amanogawa.core.ui.common.grayLine
import id.anison.amanogawa.feature.search.screenmodel.SearchScreenEvents


@Composable
internal fun AmanogawaSearchBar(
    searchQuery: TextFieldValue,
    onEvents: (SearchScreenEvents) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .windowInsetsPadding(SearchBarDefaults.windowInsets)
            .fillMaxWidth()
            .grayLine(side = LineSide.Bottom)
            .padding(
                start = MaterialTheme.spacing.small,
                top = MaterialTheme.spacing.medium,
                end = MaterialTheme.spacing.small,
                bottom = MaterialTheme.spacing.small
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            Icon(
                imageVector = AmanogawaIcon.search,
                contentDescription = "Search icon",
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                    .padding(horizontal = MaterialTheme.spacing.small)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.text.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.search_anime),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        BasicTextField(
                            value = searchQuery,
                            onValueChange = {
                                onEvents(SearchScreenEvents.OnSearchQueryChange(it))
                            },
                            singleLine = true,
                            modifier = modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                        )
                    }

                    if (searchQuery.text.isNotBlank()) {
                        IconButton(
                            onClick = {
                                onEvents(SearchScreenEvents.OnSearchQueryChange(TextFieldValue("")))
                            },
                            content = {
                                Icon(
                                    imageVector = AmanogawaIcon.clear,
                                    contentDescription = stringResource(R.string.clear),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        )
                    }
                }
            }
        }

        Row {
            IconButton(
                onClick = { onEvents(SearchScreenEvents.OnRandomButtonClick) },
                content = {
                    Icon(
                        imageVector = AmanogawaIcon.radar,
                        contentDescription = stringResource(R.string.filter_icon)
                    )
                }
            )

            IconButton(
                onClick = { onEvents(SearchScreenEvents.OnExtendedButtonClick) },
                content = {
                    Icon(
                        imageVector = AmanogawaIcon.filter,
                        contentDescription = stringResource(R.string.filter_icon)
                    )
                }
            )
        }
    }
}
