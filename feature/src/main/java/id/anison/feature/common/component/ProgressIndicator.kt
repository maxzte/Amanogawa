package id.anison.feature.common.component

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import id.anison.feature.R
import kotlinx.coroutines.delay


@Composable
internal fun ProgressIndicator(
    modifier: Modifier = Modifier,
    minDurationMs: Long = 1000L
) {
    var isVisible by remember { mutableStateOf(true) }

    /*LaunchedEffect(Unit) {
        delay(minDurationMs)
        isVisible = false
    }*/

    if (isVisible) {
        Column {
            Box(
                modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                val context = LocalContext.current
                val imageLoader = ImageLoader.Builder(context)
                    .components {
                        if (SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
                        else add(GifDecoder.Factory())
                    }
                    .build()

                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context)
                            .data(data = R.drawable.amanogawa_load_gif)
                            .apply { size(Size.ORIGINAL) }
                            .build(),
                        imageLoader = imageLoader
                    ),
                    contentDescription = "amanogawa load gif",
                    modifier = modifier.fillMaxWidth(),
                )
            }
        }
    }
}
