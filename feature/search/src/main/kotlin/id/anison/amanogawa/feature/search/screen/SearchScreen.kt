package id.anison.amanogawa.feature.search.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.amanogawa.core.model.SearchResultUI
import id.anison.amanogawa.core.ui.common.DataStateContent
import id.anison.amanogawa.core.ui.common.component.Message
import id.anison.amanogawa.feature.search.screenmodel.RequiresKeyboardDismiss
import id.anison.amanogawa.feature.search.screenmodel.SearchScreenEvents
import id.anison.amanogawa.feature.search.screenmodel.SearchScreenModel
import id.anison.amanogawa.feature.search.screenmodel.toScreen
import id.anison.amanogawa.feature.search.ui.AmanogawaSearchBar
import id.anison.amanogawa.feature.search.ui.SearchHistory
import id.anison.amanogawa.feature.search.ui.SearchResultList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

class SearchScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<SearchScreenModel>()
        val query by screenModel.query.collectAsState()
        val searchResultState by screenModel.searchResultState.collectAsState()
        val searchHistoryState by screenModel.searchHistoryState.collectAsState()
        val isSearching = query.text.trim().length >= 2

        val isNavigating = remember { mutableStateOf(false) }
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(Unit) {
            screenModel.navEventState.collectLatest { event ->
                if (isNavigating.value) return@collectLatest
                isNavigating.value = true

                if (event is RequiresKeyboardDismiss) {
                    keyboardController?.hide()
                }
                navigator.push(event.toScreen())

                delay(250)
                isNavigating.value = false
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            AmanogawaSearchBar(
                searchQuery = query,
                onEvents = { screenModel.onEvent(it) },
                modifier = Modifier.focusRequester(focusRequester)
            )

            if (isSearching) {
                DataStateContent(
                    state = searchResultState,
                    content = { searchResult ->
                        if (searchResult.isEmpty()) {
                            Message(text = "Not found!")
                        }
                        SearchResultList(
                            searchResult = searchResult,
                            onAnimeClick = { anime: SearchResultUI ->
                                screenModel.onEvent(
                                    event = SearchScreenEvents.OnAnimeClick(
                                        animeId = anime.animeId, anime = anime
                                    )
                                )
                            },
                        )
                    }
                )
            } else {
                DataStateContent(
                    state = searchHistoryState,
                    content = { searchResultList ->
                        SearchHistory(
                            searchHistory = searchResultList,
                            onEvents = { screenModel.onEvent(it) },
                        )
                    },
                    loadingContent = {}
                )
            }
        }

        LaunchedEffect(Unit) {
            delay(200)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }
}
