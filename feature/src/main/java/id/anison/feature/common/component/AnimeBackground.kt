package id.anison.feature.common.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp


@Composable
fun AnimeBackground(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    var currentImage by remember { mutableStateOf(imageUrl) }

    Crossfade(
        targetState = currentImage,
        label = "Crossfade background",
        animationSpec = tween(durationMillis = 1000)
    ) { targetImage ->
        if (targetImage == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background)
                    .blur(50.dp)
            )
        } else {
            CoilImageLoader(
                titleImage = targetImage,
                radius = 50,
                modifier = modifier.fillMaxSize()
            )
        }
    }

    LaunchedEffect(imageUrl) {
        if (imageUrl != currentImage) {
            currentImage = imageUrl
        }
    }
}