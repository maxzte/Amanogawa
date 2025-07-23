package id.anison.amanogawa.feature.search.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.amanogawa.core.ui.common.component.AnimeCard
import id.anison.amanogawa.core.ui.common.component.AmanogawaSpinner
import id.anison.amanogawa.core.ui.common.isLandscape
import id.anison.amanogawa.feature.search.screenmodel.RandomAnimeScreenModel


class RandomAnimeCarouselScreen : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint(
        "StateFlowValueCalledInComposition", "SuspiciousIndentation",
        "UnusedCrossfadeTargetStateParameter"
    )
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<RandomAnimeScreenModel>()
        val state by screenModel.state.collectAsState()
        val randomTitles = state.titles.collectAsLazyPagingItems()
        val savedPageIndex = rememberSaveable { mutableIntStateOf(0) }
        val pagerState = rememberPagerState(
            initialPage = savedPageIndex.intValue,
            pageCount = {
                randomTitles.itemCount
            }
        )

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                savedPageIndex.intValue = page
            }
        }

        when (randomTitles.loadState.refresh) {
            is LoadState.Loading -> {
                AmanogawaSpinner()
            }

            is LoadState.Error -> {
            }

            is LoadState.NotLoading -> {
                val isLandscape = isLandscape()
                if (!isLandscape) {
                    VerticalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { index ->
                        val animeDetailsUI = randomTitles[index]
                        if (animeDetailsUI != null) {
                            AnimeCard(
                                anime = animeDetailsUI.animeUI,
                                isCurrent = false,
                                onClick = {},
                            )
                        }
                    }
                } else {

                }
            }
        }
//            is DataState.Error -> {
//                ErrorMessage(onUpdate = { screenModel.refresh() })
//            }
//
//            is DataState.Loading -> {
////                SceletonTest()
//                //ProgressIndicator()
//            }
//
//            is DataState.Success -> {
//                when (val randomAnime = state.randomAnime) {
//                    is DataState.Loading -> {
//                        Crossfade(
//                            targetState = randomAnime
//                        ) {
//                            SceletonTest()
//                        }
//                        //ProgressIndicator()
//                    }
//
//                    is DataState.Error -> {
//                        ErrorMessage(onUpdate = { screenModel.refresh() })
//                    }
//
//                    is DataState.Success -> {
//                        Crossfade(
//                            targetState = randomAnime
//                        ) {
//                            Test(
//                                anime = randomAnime.data,
//                                isFavorite = true,
//                                isLast = screenModel.isLast(),
//                                onEvents = { event ->
//                                    when (event) {
//                                        is RandomScreenEvents.PopBack -> {
//                                            navigator.pop()
//                                        }
//
//                                        is RandomScreenEvents.Next -> {
//                                            screenModel.getRandomAnime(ids.data)
//                                        }
//
//                                        is RandomScreenEvents.IsLast -> {
//                                            screenModel.isLast()
//                                        }
//
//                                        is RandomScreenEvents.OnLinkClick -> {
////                                            searchScreenModel.search(event.filter)
////                                            navigator.push(FilterResultScreen(filterName = event.filterName, filterType = event.filterName))
//                                        }
//                                    }
//                                },
//                            )
//                        }
        /*CarouselTitlesContent(
            anime = randomAnime.data,
            navigator = navigator,
            screenModel = screenModel,
            onEvents = { event ->
                when (event) {
                    is RandomScreenEvents.PopBack -> navigator.pop()
                    is RandomScreenEvents.Next -> screenModel.getRandomAnime()
                    is RandomScreenEvents.IsLast -> screenModel.isLast()
                    is RandomScreenEvents.OnLinkClick -> {
                        searchScreenModel.search(event.filter)
                        navigator.push(ExtendedSearchTitlesScreen(event.filterName))
                    }
                }
            },
        )*/
    }
}
//            }
//        }
//    }
//}
