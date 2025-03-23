package id.anison.feature.common.component

import android.content.res.Configuration
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.anison.feature.R
import id.anison.feature.anime.model.AnimeDetailsUI
import id.anison.feature.common.component.layout.CarouselTitlesContent
import id.anison.feature.search.event.RandomScreenEvents
import id.anison.feature.search.model.FilterUI
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@ExperimentalMaterial3Api
@Composable
fun Test(
    anime: AnimeDetailsUI,
    isFavorite: Boolean,
    isLast: Boolean,
    onEvents: (RandomScreenEvents) -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var showBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            MaterialTheme.colorScheme.background
        ),
    )
    val overlayColor = Color.Black.copy(alpha = 0.5f)
    val localUriHandler: UriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()
    val dominantColor = getDominantColor(anime.poster.toString())

    //background
    Box(
        modifier = Modifier
            .background(backgroundGradient)
            .fillMaxSize()
    ) {
        AnimeBackground(imageUrl = anime.poster)
    }
    // black filter for bg
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(overlayColor)
    )

    if (isLandscape) {
        Box(Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Start)).fillMaxSize()) {
            CarouselTitlesContent(
                anime = anime,
                isFavorite = isFavorite,
                isLast = isLast,
                onEvents = onEvents,
            )
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TitleActions(
                isArrowDown = true,
                actionIconContent = {},
                onBackClick = {
                    onEvents(RandomScreenEvents.PopBack)
                },
            )
            ElevatedCard(
                onClick = { },
                modifier = Modifier.fillMaxHeight(0.5f)
            ) {
                CoilImageLoader(
                    titleImage = anime.poster.toString(),
                    modifier = Modifier.aspectRatio(7f / 10f)
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = anime.animeUI.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
                Text(
                    text = anime.animeUI.enJpName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.LightGray,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item { }
                item {
                    OutlinedButton(
                        onClick = {},
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_outline_thumb_up_24),
                            contentDescription = null,
                            tint = Color.White,
                        )
                        VerticalDivider(
                            modifier = Modifier
                                .height(16.dp)
                                .padding(horizontal = 4.dp)
                        )
                        Text(text = anime.liked.toString(), color = Color.White)
                        Text(text = " · ${anime.disliked}", color = Color.White)
                        VerticalDivider(
                            modifier = Modifier
                                .height(16.dp)
                                .padding(horizontal = 4.dp)
                        )
                        Icon(
                            painter = painterResource(R.drawable.ic_outline_thumb_down_24),
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                }
                item {
                    OutlinedButton(
                        onClick = {
                            onEvents(
                                RandomScreenEvents.OnLinkClick(
                                    filter = FilterUI(years = listOf(anime.animeUI.year)),
                                    filterName = anime.animeUI.year.toString()
                                )
                            )
                        },
                    ) {
                        Text(text = anime.animeUI.year.toString(), color = Color.White)
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
                                RandomScreenEvents.OnLinkClick(
                                    filter = FilterUI(genres = listOf(genre)),
                                    filterName = genre.name
                                )
                            )
                        },
                    ) {
                        Text(text = genre.name, color = Color.White)
                    }
                }
                item { }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                IconButton(
                    onClick = {

                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.arrow_back),
                        tint = Color.White,
                        modifier = Modifier.sizeIn(48.dp),
                    )
                }
                OutlinedButton(
                    onClick = {
                        localUriHandler.openUri(anime.botUrl + "?start=sttl_" + anime.botId)
                    },
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = stringResource(R.string.play_icon),
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
                IconButton(
                    onClick = {
                        onEvents(RandomScreenEvents.Next)
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = stringResource(R.string.arrow_forward),
                        tint = Color.White,
                        modifier = Modifier.sizeIn(48.dp),
                    )
                }
            }
            TextButton(
                onClick = {
                    showBottomSheet = !showBottomSheet
                }
            ) {
                Text(text = "More info")
            }
            if (showBottomSheet) {
                val darkenedColor = dominantColor.copy(
                    red = dominantColor.red * 0.7f,
                    green = dominantColor.green * 0.7f,
                    blue = dominantColor.blue * 0.7f
                )
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = bottomSheetState,
                    containerColor = darkenedColor,
                    modifier = Modifier//.background(overlayColor),
                ) {
                    Box(
                        modifier = Modifier
//                        .background(dominantColor/*backgroundGradient*/)
                            .fillMaxHeight(0.9f)
                    ) {
                        val sheetScrollState = rememberScrollState()
                        //AnimeBackground(imageUrl = anime.poster)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
//                            .background(overlayColor)
                        )
                        Column(
                            modifier = Modifier
                                .verticalScroll(state = sheetScrollState)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White
                            )
                            HorizontalDivider()
                            Text(text = anime.description, color = Color.White)
                            Text(
                                text = "Team",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White
                            )
                            HorizontalDivider()
                            CompositionLocalProvider(LocalContentColor provides Color.White) {
                                AnimeTeam(
                                    dubers = anime.dubers,
                                    onWordClick = { filter, filterName ->
                                        coroutineScope.launch {
                                            bottomSheetState.hide()
                                            delay(300)
                                            showBottomSheet = false
                                            onEvents(
                                                RandomScreenEvents.OnLinkClick(
                                                    filter = filter,
                                                    filterName = filterName
                                                )
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SceletonTest(
//    animePoster: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val skeletonAlpha by infiniteTransition.animateFloat(
        // between these states, the skeleton will flicker
        initialValue = 1f, // skeleton max alpha
        targetValue = 0.5f, // skeleton min alpha
        animationSpec = infiniteRepeatable(
            // every 500ms, but you can change this time
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            MaterialTheme.colorScheme.background
        ),
    )
    val overlayColor = Color.Black.copy(alpha = 0.5f)

    //background
    Box(
        modifier = Modifier
            .background(backgroundGradient)
            .fillMaxSize()
    ) {
//        AnimeBackground(imageUrl = animePoster)
    }
    // black filter for bg
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(overlayColor)
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TitleActions(
            actionIconContent = {},
            onBackClick = {},
        )
        ElevatedCard(
            onClick = { },
            modifier = Modifier
                .alpha(skeletonAlpha)
                .fillMaxHeight(0.5f)
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .aspectRatio(7f / 10f)
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {},
                modifier = Modifier
                    .height(35.dp)
                    .fillMaxWidth(0.7f)
                    .alpha(skeletonAlpha),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.onBackground)
            ) {
            }
            OutlinedButton(
                onClick = {},
                modifier = Modifier
                    .height(25.dp)
                    .fillMaxWidth(0.5f)
                    .alpha(skeletonAlpha),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.onBackground)
            ) {
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            OutlinedButton(
                onClick = {},
                modifier = Modifier
                    .width(120.dp)
                    .alpha(skeletonAlpha),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.onBackground)
            ) {
            }
            OutlinedButton(
                onClick = {},
                modifier = Modifier
                    .width(90.dp)
                    .alpha(skeletonAlpha),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.onBackground)
            ) {
            }
            repeat(5) {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .width(90.dp)
                        .alpha(skeletonAlpha),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.onBackground)
                ) {
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            IconButton(
                onClick = {},
                enabled = false,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.arrow_back),
                    tint = Color.White,
                    modifier = Modifier.sizeIn(48.dp),
                )
            }
            OutlinedButton(
                onClick = {},
                enabled = false,
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = stringResource(R.string.play_icon),
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            IconButton(
                onClick = {},
                enabled = false
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = stringResource(R.string.arrow_forward),
                    tint = Color.White,
                    modifier = Modifier.sizeIn(48.dp),
                )
            }
        }
        TextButton(
            onClick = { },
            enabled = false
        ) {
            Text(text = "More info")
        }
    }
}
