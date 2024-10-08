package id.anison.feature.common.component

import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.request.ImageRequest
import id.anison.feature.R
import id.anison.feature.common.BlurTransformation
import id.anison.feature.model.DubersUI
import id.anison.feature.model.FilterUI
import id.anison.feature.model.GenreUI
import id.anison.feature.model.SUI
import id.anison.feature.model.TUI
import id.anison.feature.model.VUI


@Composable
fun Message(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text)
    }
}

@Composable
internal fun ErrorMessage(
    onUpdate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_rounded_language_24),
                contentDescription = "No internet connection icon",
                modifier = modifier.size(50.dp)
            )
            Text(text = stringResource(R.string.something_wrong))
            Text(
                text = stringResource(R.string.check_your_internet_connection_and_try_again),
                textAlign = TextAlign.Center,
            )
            OutlinedButton(onClick = { onUpdate() }) {
                Text(text = "Refresh")
            }
        }
    }
}

@Composable
fun CoilImageLoader(
    titleImage: String,
    modifier: Modifier = Modifier,
    contentCrop: ContentScale = ContentScale.Crop,
    radius: Int = 0,
    onPaletteGenerated: (Palette?) -> Unit = {}
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(stringResource(R.string.image_loader_link, titleImage))
            .allowHardware(false)
            .crossfade(true)
            .transformations(
                listOf(
                    BlurTransformation(radius = radius)
                )
            )
            .listener { request, result ->
                val drawable = result.drawable
                if (drawable is BitmapDrawable) {
                    val bitmap = drawable.bitmap
                    Palette.Builder(bitmap).generate { palette ->
                        onPaletteGenerated(palette)
                    }
                }
            }
            .build(),
        contentDescription = stringResource(R.string.image_loader),
        contentScale = contentCrop,
        modifier = modifier.fillMaxSize(),
    )
}

@Composable
fun FullScreenImage(
    photo: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var zoomOffset by remember { mutableStateOf(Offset.Zero) }
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
        content = {
            BoxWithConstraints(
                modifier = modifier
                    .fillMaxSize()
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                if (photo != null) {
                    val state =
                        rememberTransformableState { zoomChange: Float, panChange: Offset, _: Float ->
                            scale = (scale * zoomChange).coerceIn(1f, 5f)
                            val extraWidth = (scale - 1) * constraints.maxWidth
                            val extraHeight = (scale - 1) * constraints.maxHeight
                            val maxX = extraWidth / 2
                            val maxY = extraHeight / 2
                            zoomOffset = Offset(
                                x = (zoomOffset.x + scale * panChange.x).coerceIn(-maxX, maxX),
                                y = (zoomOffset.y + scale * panChange.y).coerceIn(-maxY, maxY)
                            )
                            zoomOffset += panChange
                        }
                    CoilImageLoader(
                        contentCrop = ContentScale.Fit,
                        titleImage = photo,
                        modifier = modifier
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = zoomOffset.x
                                translationY = zoomOffset.y
                            }
                            .transformable(state)
                    )
                }
            }
        }
    )
}

@Composable
fun UserRatingBar(
    modifier: Modifier = Modifier,
    rating: Int,
    ratingIconPainter: Painter = painterResource(id = R.drawable.ic_circle_24),
) {
    Row(modifier = modifier.wrapContentSize()) {
        repeat(rating) {
            StarIcon(painter = ratingIconPainter, isUnRating = false)
        }
        repeat(5 - rating) {
            StarIcon(painter = ratingIconPainter, isUnRating = true)
        }
    }
}

@Composable
fun StarIcon(
    painter: Painter,
    isUnRating: Boolean,
    modifier: Modifier = Modifier,
) {
    Icon(
        painter = painter,
        contentDescription = null,
        modifier = modifier.size(20.dp),
        tint = if (!isUnRating) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
fun DetailText(
    modifier: Modifier = Modifier,
    teammateName: AnnotatedString? = null,
    label: String,
    value: String,
) {
    Text(
        text = buildAnnotatedString {
            if (teammateName == null) append(
                AnnotatedString(
                    text = label,
                    spanStyle = SpanStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                    )
                )
            )
            else append(teammateName)
            append(
                AnnotatedString(
                    text = value,
                    spanStyle = SpanStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                    )
                )
            )
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailTextWithLinks(
    modifier: Modifier = Modifier,
    label: String,
    year: Int = 0,
    genres: List<GenreUI> = emptyList(),
    v: List<VUI> = emptyList(),
    s: List<SUI> = emptyList(),
    t: List<TUI> = emptyList(),
    onWordClick: (FilterUI) -> Unit,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(1f),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        TextButton(
            onClick = {},
            colors = ButtonDefaults.textButtonColors(disabledContentColor = LocalContentColor.current),
            contentPadding = PaddingValues(0.dp),
            enabled = false,
            modifier = modifier.height(30.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
        if (genres.isNotEmpty()) {
            genres.forEachIndexed { index, genre ->
                TextButton(
                    onClick = { onWordClick(FilterUI(genres = listOf(genre))) },
                    contentPadding = PaddingValues(0.dp),
                    shape = RectangleShape,
                    modifier = modifier.height(30.dp)
                ) {
                    if (genres.lastIndex == index) Text(
                        text = genre.name,
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = TextDecoration.Underline,
                    )
                    else Text(
                        text = genre.name + ",",
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = TextDecoration.Underline,
                    )
                }
            }
        } else if (v.isNotEmpty()) {
            v.forEachIndexed { index, voiceActor ->
                TextButton(
                    onClick = { onWordClick(FilterUI(dubers = DubersUI(v = listOf(voiceActor)))) },
                    contentPadding = PaddingValues(0.dp),
                    shape = RectangleShape,
                    modifier = modifier.height(30.dp)
                ) {
                    if (v.lastIndex == index) Text(
                        text = voiceActor.name,
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = TextDecoration.Underline,
                    )
                    else Text(
                        text = voiceActor.name + ",",
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = TextDecoration.Underline,
                    )
                }
            }
        } else if (t.isNotEmpty()) {
            t.forEachIndexed { index, translator ->
                TextButton(
                    onClick = { onWordClick(FilterUI(dubers = DubersUI(t = listOf((translator))))) },
                    contentPadding = PaddingValues(0.dp),
                    shape = RectangleShape,
                    modifier = modifier.height(30.dp)
                ) {
                    if (t.lastIndex == index) Text(
                        text = translator.name,
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = TextDecoration.Underline,
                    )
                    else Text(
                        text = translator.name + ",",
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = TextDecoration.Underline,
                    )
                }
            }
        } else if (s.isNotEmpty()) {
            s.forEachIndexed { index, soundMaker ->
                TextButton(
                    onClick = { onWordClick(FilterUI(dubers = DubersUI(s = listOf((soundMaker))))) },
                    contentPadding = PaddingValues(0.dp),
                    shape = RectangleShape,
                    modifier = modifier.height(30.dp)
                ) {
                    if (s.lastIndex == index) Text(
                        text = soundMaker.name,
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = TextDecoration.Underline,
                    )
                    else Text(
                        text = soundMaker.name + ",",
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = TextDecoration.Underline,
                    )
                }
            }
        } else if (year != 0) {
            TextButton(
                onClick = { onWordClick(FilterUI(years = listOf(year))) },
                contentPadding = PaddingValues(0.dp),
                shape = RectangleShape,
                modifier = modifier
                    .padding(0.dp)
                    .height(30.dp)
            ) {
                Text(
                    text = "$year",
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = TextDecoration.Underline,
                )
            }
        }
    }
}

@Composable
fun ExpandedText(
    description: String,
) {
    var expanded by remember { mutableStateOf(false) }
    val textToShow = if (expanded) {
        buildAnnotatedString {
            append(description)
            if (description.length > 100) {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                ) {
                    append(" ${stringResource(R.string.less)}")
                }
            }
        }
    } else {
        buildAnnotatedString {
            append(description.take(100))
            if (description.length > 100) {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                ) {
                    append(stringResource(R.string.more))
                }
            }
        }
    }
    Text(
        text = textToShow,
        maxLines = if (expanded) Int.MAX_VALUE else 3,
        modifier = Modifier
            .animateContentSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                expanded = !expanded
            },
    )
}

@Composable
fun SocialRow(
    icon: Int,
    desc: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = desc,
            modifier = modifier.size(24.dp),
        )
        Text(text = label)
    }
}





