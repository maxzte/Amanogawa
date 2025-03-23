package id.anison.feature.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.feature.R
import id.anison.feature.anime.AnimeDetailsScreen
import id.anison.feature.anime.model.AnimeDetailsUI
import id.anison.feature.common.component.button.RectangleButton

@Composable
fun TitleHead(
    anime: AnimeDetailsUI,
    modifier: Modifier = Modifier,
) {
    val navigator = LocalNavigator.currentOrThrow
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isCompact = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    val configuration = LocalConfiguration.current
    val componentHeight = if (isCompact) (configuration.screenHeightDp / 3.5).toInt()
    else (configuration.screenHeightDp / 1.3).toInt()
    val localUriHandler: UriHandler = LocalUriHandler.current
    var isActivePhoto by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
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
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                /*Text(
                    text = anime.animeUI.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onBackground,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))*/
                Text(
                    text = anime.animeUI.enJpName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    maxLines = 2,
                    color = Color.LightGray,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = anime.duration.toString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Icon(
                        painterResource(R.drawable.ic_access_time_24),
                        contentDescription = stringResource(R.string.duration_icon)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = anime.animeUI.views.toString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Icon(
                        painterResource(R.drawable.ic_watch_eye),
                        contentDescription = stringResource(id = R.string.views_icon)
                    )
                    UserRatingBar(rating = anime.rating)
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (navigator != null) {
                    OutlinedButton(
                        onClick = {
                            navigator.push(AnimeDetailsScreen(anime.animeUI.animeId))
                        },
                    ) {
                        Text(text = stringResource(R.string.watch_btn))
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = stringResource(R.string.play_icon)
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        RectangleButton(
                            title = stringResource(R.string.watch_btn),
                            iconContent = {
                                Icon(
                                    imageVector = Icons.Rounded.PlayArrow,
                                    contentDescription = stringResource(R.string.play_icon)
                                )
                            },
                            onClick = {
                                localUriHandler.openUri(anime.botUrl + "?start=sttl_" + anime.botId)
                            },
                        )
                        RectangleButton(
                            title = stringResource(id = R.string.download_btn),
                            iconContent = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_download_24),
                                    contentDescription = stringResource(id = R.string.download_icon)
                                )
                            },
                            onClick = { anime.torrentUrl?.let { localUriHandler.openUri(it) } },
                        )
                    }
                }
            }
        }
    }
    if (isActivePhoto != null) {
        FullScreenImage(
            photo = isActivePhoto,
            onDismiss = { isActivePhoto = null },
            modifier = Modifier.aspectRatio(7f / 10f)
        )
    }
}

@Composable
fun TitleActions(
    modifier: Modifier = Modifier,
    title: String = "",
    isArrowDown: Boolean = false,
    actionIconContent: @Composable () -> Unit,
    onBackClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .fillMaxWidth()
            .heightIn(max = 64.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                IconButton(
                    onClick = { onBackClick() },
                    modifier = modifier,
                ) {
                    if (isArrowDown) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_downward_24dp),
                            contentDescription = stringResource(R.string.arrow_back),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.arrow_back),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                Text(text = title)
            }
            actionIconContent()
        }
    }
}

@Composable
fun AnimeImages(
    images: List<String>,
    modifier: Modifier = Modifier
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isCompact = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    val configuration = LocalConfiguration.current
    val itemWidth = (configuration.screenWidthDp / 1.1).toInt()
    val itemHeight = (configuration.screenHeightDp / 1.1).toInt()
    val itemSize = if (isCompact) itemWidth else itemHeight
    var isActivePhoto by rememberSaveable { mutableStateOf<String?>(null) }
    val pagerState = rememberPagerState(pageCount = { images.size })
    HorizontalPager(
        state = pagerState,
        pageSize = PageSize.Fixed(itemSize.dp),
    ) {
        val image = images[it]
        ElevatedCard(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .clickable { isActivePhoto = image },
        ) {
            CoilImageLoader(
                titleImage = image,
                modifier = modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )
        }
    }
    if (isActivePhoto != null) {
        FullScreenImage(
            photo = isActivePhoto,
            onDismiss = { isActivePhoto = null },
            modifier = Modifier.aspectRatio(16f / 9f)
        )
    }
}
