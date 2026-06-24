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
import android.graphics.BitmapFactory
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.transformations
import androidx.palette.graphics.Palette
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.BlurTransformation
import java.net.URL


@Composable
fun CoilImageLoader(
    titleImage: String,
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
                .transformations(BlurTransformation(radius))
                .build()
        } else imageUrl,
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
    radius: Int = 0,
    showAmanogawaSpinner: Boolean = false,
) {
    CoilImageLoader(
        titleImage = titleImage,
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
        try {
            val bitmap = BitmapFactory.decodeStream(URL(baseUrl + imageUrl).openStream())
            bitmap?.let {
                val palette = Palette.from(it).generate()
                color = Color(palette.getDominantColor(Color.DarkGray.toArgb()))
            }
        } catch (_: Exception) { }
    }

    return color
}