package id.anison.feature.search.screen

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowWidthSizeClass
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.feature.R
import id.anison.feature.anime.model.AnimeDetailsUI
import id.anison.feature.common.DataState
import id.anison.feature.MainScreenModel
import id.anison.feature.common.component.CoilImageLoader
import id.anison.feature.common.component.DetailText
import id.anison.feature.common.component.ErrorMessage
import id.anison.feature.common.component.ExpandedText
import id.anison.feature.common.component.ProgressIndicator
import id.anison.feature.common.component.TitleActions
import id.anison.feature.common.component.TitleHead
import id.anison.feature.common.component.AnimeImages
import id.anison.feature.common.component.SceletonTest
import id.anison.feature.common.component.Test
import id.anison.feature.search.event.RandomScreenEvents
import id.anison.feature.search.model.FilterUI
import id.anison.feature.search.screenmodel.ExtendedSearchScreenModel
import id.anison.feature.search.screenmodel.RandomTitleScreenModel
import kotlinx.coroutines.delay


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
        val mainScreenModel = navigator.koinNavigatorScreenModel<MainScreenModel>()
        val screenModel = koinScreenModel<RandomTitleScreenModel>()
        val searchScreenModel = koinScreenModel<ExtendedSearchScreenModel>()
        val state = screenModel.randomTitleState.collectAsState().value
        LaunchedEffect(Unit) {
            mainScreenModel.setTopBarVisible(false)
            mainScreenModel.setBottomBarVisible(false)
        }
        when (val ids = state.animeIDs) {
            is DataState.Error -> {
                ErrorMessage(onUpdate = { screenModel.refresh() })
            }

            is DataState.Loading -> {
                SceletonTest()
                //ProgressIndicator()
            }

            is DataState.Success -> {
                when (val randomAnime = state.randomAnime) {
                    is DataState.Loading -> {
                        Crossfade(
                            targetState = randomAnime
                        ) {
                            SceletonTest()
                        }
                        //ProgressIndicator()
                    }

                    is DataState.Error -> {
                        ErrorMessage(onUpdate = { screenModel.refresh() })
                    }

                    is DataState.Success -> {
                        Crossfade(
                            targetState = randomAnime
                        ) {
                            Test(
                                anime = randomAnime.data,
                                isFavorite = true,
                                isLast = screenModel.isLast(),
                                onEvents = { event ->
                                    when (event) {
                                        is RandomScreenEvents.PopBack -> {
                                            navigator.pop()
                                        }

                                        is RandomScreenEvents.Next -> {
                                            screenModel.getRandomAnime(ids.data)
                                        }

                                        is RandomScreenEvents.IsLast -> {
                                            screenModel.isLast()
                                        }

                                        is RandomScreenEvents.OnLinkClick -> {
                                            searchScreenModel.search(event.filter)
                                            navigator.push(ExtendedSearchTitlesScreen(event.filterName))
                                        }
                                    }
                                },
                            )
                        }
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
            }
        }
    }
}
