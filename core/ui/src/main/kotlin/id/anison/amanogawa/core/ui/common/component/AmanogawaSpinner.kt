package id.anison.amanogawa.core.ui.common.component

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.request.ImageRequest
import coil3.size.Size
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.spacing

@Composable
fun AmanogawaSpinner(
    modifier: Modifier = Modifier,
    spinnerSize: Dp = 100.dp,
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) add(AnimatedImageDecoder.Factory())
            else add(GifDecoder.Factory())
        }
        .build()
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.small),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data(data = R.drawable.amanogawa_load_webp)
                    .apply { size(Size.ORIGINAL) }
                    .build(),
                imageLoader = imageLoader,
                filterQuality = FilterQuality.High
            ),
            contentDescription = "amanogawa load gif",
            modifier = Modifier.size(spinnerSize),
        )
    }
}
