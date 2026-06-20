@file:OptIn(ExperimentalMaterial3Api::class)

package id.anison.amanogawa.feature.search.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import id.anison.amanogawa.core.model.FilterUI
import id.anison.amanogawa.core.model.TeamMemberTypeUI
import id.anison.amanogawa.core.navigation.NavigationHandler
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaConst
import id.anison.amanogawa.core.ui.common.AmanogawaIcon
import id.anison.amanogawa.core.ui.common.DataStateContent
import id.anison.amanogawa.core.ui.common.component.ErrorMessage
import id.anison.amanogawa.core.ui.common.component.button.ArrowBackButton
import id.anison.amanogawa.core.ui.common.component.layout.AmanogawaTopBar
import id.anison.amanogawa.core.ui.common.ignoreHorizontalParentPadding
import id.anison.amanogawa.core.ui.common.isLandscape
import id.anison.amanogawa.feature.search.screenmodel.FilterScreenEvents
import id.anison.amanogawa.feature.search.screenmodel.FilterScreenModel
import id.anison.amanogawa.feature.search.screenmodel.toScreen

class FilterScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<FilterScreenModel>()
        val state by screenModel.state.collectAsState()

        NavigationHandler(
            navigationState = screenModel.navEventState,
            toScreen = { toScreen() }
        )

        DataStateContent(
            state = state.filterOptions,
            errorContent = {
                ErrorMessage(
                    onUpdate = {
                        screenModel.onEvents(FilterScreenEvents.Refresh)
                    }
                )
            },
            content = { filterOptions ->
                FilterScreenContent(
                    filter = state.filter,
                    searchOptions = filterOptions,
                    onEvents = { screenModel.onEvents(it) },
                )
            }
        )
    }

    @Composable
    private fun FilterScreenContent(
        filter: FilterUI,
        searchOptions: FilterUI,
        onEvents: (FilterScreenEvents) -> Unit,
    ) {
        val lazyListState = rememberSaveable(saver = LazyListState.Saver) {
            LazyListState()
        }

        Scaffold(
            topBar = {
                AmanogawaTopBar(
                    title = stringResource(id = R.string.filters),
                    navigationIconContent = {
                        ArrowBackButton(onClick = { onEvents(FilterScreenEvents.PopBack) })
                    },
                    additionalActionBtnContent = {
                        TextButton(
                            onClick = { onEvents(FilterScreenEvents.ResetFilter) },
                            enabled = filter != FilterUI(),
                            content = {
                                Text(
                                    text = stringResource(id = R.string.reset),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        )
                    },
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 8.dp,
                    top = paddingValues.calculateTopPadding(),
                    end = 8.dp,
                    bottom = if (!isLandscape()) paddingValues.calculateBottomPadding() + AmanogawaConst.bottomBarInsets
                    else paddingValues.calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item(key = "genresText") {
                    Text(
                        text = stringResource(id = R.string.genres),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }

                item(key = "genresFlow") {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        searchOptions.genres.forEach { genre ->
                            val isSelected = genre in filter.genres

                            ElevatedFilterChip(
                                onClick = {
                                    onEvents(FilterScreenEvents.AddGenre(genre))
                                },
                                label = {
                                    Text(
                                        text = genre.name,
                                        style = MaterialTheme.typography.labelLarge,
                                    )
                                },
                                selected = isSelected,
                                leadingIcon = {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = AmanogawaIcon.done,
                                            contentDescription = "Localized Description",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                item(key = "teamMemberFlow") {
                    val groupedDubbers =
                        searchOptions.teamMembers.groupBy { teamMember -> teamMember.teamMemberType }

                    groupedDubbers.forEach { (type, teamMembersOfType) ->
                        val label = when (type) {
                            TeamMemberTypeUI.TRANSLATOR -> stringResource(id = R.string.translators)
                            TeamMemberTypeUI.VOICE -> stringResource(id = R.string.voice_actors)
                            TeamMemberTypeUI.SOUND -> stringResource(id = R.string.sound_engineers)
                        }

                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            teamMembersOfType.forEach { teamMemberUI ->
                                val isSelected = teamMemberUI in filter.teamMembers

                                ElevatedFilterChip(
                                    onClick = {
                                        onEvents(FilterScreenEvents.AddTeamMember(teamMember = teamMemberUI))
                                    },
                                    label = {
                                        Text(
                                            text = teamMemberUI.name,
                                            style = MaterialTheme.typography.labelLarge,
                                        )
                                    },
                                    selected = isSelected,
                                    leadingIcon = {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = AmanogawaIcon.done,
                                                contentDescription = "Localized Description",
                                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                item(key = "yearsText") {
                    Text(
                        text = stringResource(id = R.string.years),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }

                item(key = "yearsFlow") {
                    LazyRow(
                        modifier = Modifier.ignoreHorizontalParentPadding(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            items = searchOptions.years,
                            key = { key -> key }
                        ) { year ->
                            val isSelected = year in filter.years

                            ElevatedFilterChip(
                                onClick = {
                                    onEvents(FilterScreenEvents.ToggleYear(year))
                                },
                                label = {
                                    Text(
                                        text = year.toString(),
                                        style = MaterialTheme.typography.labelLarge,
                                    )
                                },
                                selected = isSelected,
                                leadingIcon = {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = AmanogawaIcon.done,
                                            contentDescription = "Localized Description",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                item(key = "searchButton") {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilledTonalButton(
                            onClick = { onEvents(FilterScreenEvents.Search) },
                            content = {
                                Icon(
                                    imageVector = AmanogawaIcon.search,
                                    contentDescription = stringResource(id = R.string.search_icon),
                                )

                                Text(
                                    text = stringResource(id = R.string.search),
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}