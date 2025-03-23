package id.anison.feature.search.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.feature.R
import id.anison.feature.common.DataState
import id.anison.feature.common.component.layout.AmanogawaTopBar
import id.anison.feature.common.component.ErrorMessage
import id.anison.feature.common.component.ProgressIndicator
import id.anison.feature.search.event.ExtendedSearchScreenEvents
import id.anison.feature.search.model.FilterUI
import id.anison.feature.search.screenmodel.ExtendedSearchScreenModel

class ExtendedSearchScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    override fun Content() {
        val navigator: Navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ExtendedSearchScreenModel>()
        val state by screenModel.extendedSearchState.collectAsState()
        LaunchedEffect(Unit) {
            screenModel.getFilters()
        }
        when (val filterOptionsUI = state.filterOptions) {
            is DataState.Error -> {
                ErrorMessage(onUpdate = { })
            }

            is DataState.Loading -> {
                ProgressIndicator()
            }

            is DataState.Success -> {
                ExtendedSearchScreenContent(
                    searchOptions = filterOptionsUI.data,
                    onEvents = { event ->
                        when (event) {
                            is ExtendedSearchScreenEvents.PopBack -> {
                                navigator.pop()
                            }

                            is ExtendedSearchScreenEvents.AddGenre -> {
                                screenModel.addGenre(event.genre)
                            }

                            is ExtendedSearchScreenEvents.AddTranslator -> {
                                screenModel.addTranslator(t = event.t)
                            }

                            is ExtendedSearchScreenEvents.AddVoice -> {
                                screenModel.addVoice(v = event.v)
                            }

                            is ExtendedSearchScreenEvents.AddSound -> {
                                screenModel.addSound(s = event.s)
                            }

                            is ExtendedSearchScreenEvents.AddYear -> {
                                screenModel.addYear(event.year)
                            }

                            is ExtendedSearchScreenEvents.RemoveYear -> {
                                screenModel.removeYear(event.year)
                            }

                            is ExtendedSearchScreenEvents.Search -> {
                                screenModel.search()
                                navigator.push(ExtendedSearchTitlesScreen())
                            }

                            is ExtendedSearchScreenEvents.Refresh -> {
                                screenModel.refresh()
                            }
                        }
                    },
                )
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RememberReturnType")
    @OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun ExtendedSearchScreenContent(
        searchOptions: FilterUI,
        onEvents: (ExtendedSearchScreenEvents) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

        Scaffold(
            topBar = {
                AmanogawaTopBar(
                    title = stringResource(R.string.extended_search),
                    popNavigate = { onEvents(ExtendedSearchScreenEvents.PopBack) },
                    scrollBehavior = scrollBehavior,
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(connection = scrollBehavior.nestedScrollConnection)
        ) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding())
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    Text(text = stringResource(id = R.string.genres))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        searchOptions.genres.forEach { genre ->
                            var isSelected by rememberSaveable { mutableStateOf(false) }
                            val backgroundColor =
                                if (isSelected) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.secondaryContainer
                            val contentColor = if (!isSelected) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.secondaryContainer
                            FilledTonalButton(
                                onClick = {
                                    onEvents(ExtendedSearchScreenEvents.AddGenre(genre))
                                    isSelected = !isSelected
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = backgroundColor,
                                    contentColor = contentColor,
                                ),
                            ) {
                                Text(text = genre.name)
                            }
                        }
                    }
                }
                item {
                    Text(text = stringResource(id = R.string.translators_two_dots))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        searchOptions.dubers.t.forEach { t ->
                            var isSelected by rememberSaveable { mutableStateOf(false) }
                            val backgroundColor =
                                if (isSelected) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.secondaryContainer
                            val contentColor = if (!isSelected) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.secondaryContainer
                            FilledTonalButton(
                                onClick = {
                                    onEvents(ExtendedSearchScreenEvents.AddTranslator(t))
                                    isSelected = !isSelected
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = backgroundColor,
                                    contentColor = contentColor
                                ),
                            ) {
                                Text(text = t.value)
                            }
                        }
                    }
                }
                item {
                    Text(text = stringResource(id = R.string.voice_actors_two_dots))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        searchOptions.dubers.v.forEach { v ->
                            var isSelected by rememberSaveable { mutableStateOf(false) }
                            val backgroundColor =
                                if (isSelected) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.secondaryContainer
                            val contentColor = if (!isSelected) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.secondaryContainer
                            FilledTonalButton(
                                onClick = {
                                    onEvents(ExtendedSearchScreenEvents.AddVoice(v))
                                    isSelected = !isSelected
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = backgroundColor,
                                    contentColor = contentColor,
                                ),
                            ) {
                                Text(text = v.value)
                            }
                        }
                    }
                }
                item {
                    Text(text = stringResource(id = R.string.sound_two_dots))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        searchOptions.dubers.s.forEach { s ->
                            var isSelected by rememberSaveable { mutableStateOf(false) }
                            val backgroundColor =
                                if (isSelected) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.secondaryContainer
                            val contentColor = if (!isSelected) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.secondaryContainer
                            FilledTonalButton(
                                onClick = {
                                    onEvents(ExtendedSearchScreenEvents.AddSound(s))
                                    isSelected = !isSelected
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = backgroundColor,
                                    contentColor = contentColor,
                                ),
                            ) {
                                Text(text = s.value)
                            }
                        }
                    }
                }
                item {
                    Text(text = stringResource(id = R.string.year))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(searchOptions.years) { year ->
                            var isSelected by rememberSaveable { mutableStateOf(false) }
                            val backgroundColor =
                                if (isSelected) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.secondaryContainer
                            val contentColor = if (!isSelected) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.secondaryContainer
                            FilledTonalButton(
                                onClick = {
                                    if (isSelected) onEvents(
                                        ExtendedSearchScreenEvents.RemoveYear(
                                            year
                                        )
                                    )
                                    else onEvents(ExtendedSearchScreenEvents.AddYear(year))
                                    isSelected = !isSelected
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = backgroundColor,
                                    contentColor = contentColor,
                                ),
                            ) {
                                Text(text = year.toString())
                            }
                        }
                    }
                }
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = modifier.fillMaxWidth()
                    ) {
                        FilledTonalButton(
                            onClick = {
                                onEvents(
                                    ExtendedSearchScreenEvents.Search(
                                        filter = FilterUI(
                                            years = searchOptions.years,
                                            genres = searchOptions.genres,
                                            dubers = searchOptions.dubers,
                                        )
                                    )
                                )
                            },
                            modifier = modifier
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search_icon)
                            )
                            Text(
                                text = stringResource(R.string.search),
                                style = TextStyle(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                )
                            )
                        }
                    }
                }
                item { }
            }
        }
    }
}