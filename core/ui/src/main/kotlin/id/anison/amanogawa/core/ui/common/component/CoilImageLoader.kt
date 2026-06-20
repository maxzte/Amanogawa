package id.anison.amanogawa.core.ui.common.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.Coil
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.BlurTransformation
import org.koin.compose.koinInject
import org.koin.core.qualifier.qualifier


@Composable
fun CoilImageLoader(
    titleImage: String,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
    radius: Int = 0,
    showAmanogawaSpinner: Boolean = false,
) {
    val imageUrl = stringResource(id = R.string.image_loader_link, titleImage)
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    val painter = rememberAsyncImagePainter(
        model = if (radius > 0) {
            ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .transformations(BlurTransformation(radius = radius))
                .build()
        } else imageUrl,
        imageLoader = imageLoader,
        onState = { state ->
            isLoading = state is AsyncImagePainter.State.Loading
            isError = state is AsyncImagePainter.State.Error
        }
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        if (showAmanogawaSpinner && isLoading) {
            AmanogawaSpinner()
        }
        Image(
            painter = painter,
            contentDescription = stringResource(R.string.image_loader),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun AnimeImageLoader(
    modifier: Modifier = Modifier,
    titleImage: String,
    useCache: Boolean = false,
    radius: Int = 0,
    showAmanogawaSpinner: Boolean = false,
) {
    val qualifier = if (useCache) qualifier("default") else qualifier("noCache")
    val imageLoader = koinInject<ImageLoader>()

    CoilImageLoader(
        titleImage = titleImage,
        imageLoader = imageLoader,
        radius = radius,
        showAmanogawaSpinner = showAmanogawaSpinner,
        modifier = modifier,
    )
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