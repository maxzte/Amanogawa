package id.anison.amanogawa.core.ui.common.component

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat

@Composable
fun FullScreenImage(
    photo: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val window = (view.context as Activity).window
    val insetsController = WindowCompat.getInsetsController(window, view)
    var scale by remember { mutableFloatStateOf(1f) }
    var zoomOffset by remember { mutableStateOf(Offset.Zero) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    Dialog(
        onDismissRequest = {
//            if (isLandscape) insetsController.hide(WindowInsetsCompat.Type.navigationBars())
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
        content = {
            BoxWithConstraints(
                modifier = modifier
                    .fillMaxSize()
                    .clickable {
//                        if (isLandscape) insetsController.hide(WindowInsetsCompat.Type.navigationBars())
                        onDismiss()
                    },
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
                    AnimeImageLoader(
                        titleImage = photo,
                        modifier = Modifier
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