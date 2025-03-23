package id.anison.feature.common.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.Coil
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import id.anison.feature.R
import id.anison.feature.common.BlurTransformation



@Composable
fun CoilImageLoader(
    titleImage: String,
    modifier: Modifier = Modifier,
    contentCrop: ContentScale = ContentScale.Crop,
    radius: Int = 0,
    showAnimatePlaceHolder: Boolean = false,
) {
    val context = LocalContext.current
    val model = ImageRequest.Builder(context)
        .data(stringResource(R.string.image_loader_link, titleImage))
        .allowHardware(false)
//        .crossfade(true)
        .transformations(listOf(BlurTransformation(radius = radius)))
        .build()

    val painter = rememberAsyncImagePainter(model)
    val imageState = painter.state

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val skeletonAlpha by infiniteTransition.animateFloat(
        initialValue = 1f, // skeleton max alpha
        targetValue = 0.5f, // skeleton min alpha
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val transition by animateFloatAsState(
        targetValue = if (imageState is AsyncImagePainter.State.Success) 1f else 0f
    )

    Box(
        modifier = modifier
            .alpha(transition)
            .fillMaxSize()
    ) {
        /*if (imageState is AsyncImagePainter.State.Loading) {
            LoadingAnimation()
        }*/
        if (showAnimatePlaceHolder) {
            if (imageState is AsyncImagePainter.State.Loading) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
//                        .background(Color.Gray)
                        .alpha(skeletonAlpha)
                )
            }
        }

        Image(
            painter = painter,
            contentDescription = stringResource(R.string.image_loader),
            contentScale = contentCrop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
internal fun LoadingAnimation(modifier: Modifier = Modifier) {

}

@Composable
fun getDominantColor(imageUrl: String): Color {
    val context = LocalContext.current
    var color by remember { mutableStateOf(Color.DarkGray) }
    val baseUrl = stringResource(R.string.base_link)

    LaunchedEffect(imageUrl) {
        val bitmap = Coil.imageLoader(context)
            .execute(
                ImageRequest.Builder(context)
                    .data(baseUrl + imageUrl)
                    .allowHardware(false)
                    .build()
            ).drawable?.toBitmap()

        bitmap?.let {
            val palette = Palette.from(it).generate()
            color = Color(palette.getDominantColor(Color.DarkGray.toArgb()))
        }
    }

    return color
}