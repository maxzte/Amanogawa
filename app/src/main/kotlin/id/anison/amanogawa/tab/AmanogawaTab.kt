package id.anison.amanogawa.tab

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

interface AmanogawaTab : Tab {
    val amanogawaTabOptions: AmanogawaTabOptions
        @Composable get

    override val options: TabOptions
        @Composable get() {
            val index = amanogawaTabOptions.index
            val title = amanogawaTabOptions.title

            return remember {
                TabOptions(
                    index = index,
                    title = title,
                    icon = null
                )
            }
        }
}

sealed interface IconSource {
    data class PainterIcon(val painter: Painter) : IconSource
    data class VectorIcon(val imageVector: ImageVector) : IconSource
}

data class AmanogawaTabOptions(
    val index: UShort,
    val title: String,
    val selectedIcon: IconSource? = null,
    val unselectedIcon: IconSource? = null,
)

@Composable
fun AmanogawaTabIcon(
    icon: IconSource,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    when (icon) {
        is IconSource.PainterIcon -> Icon(
            painter = icon.painter,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint
        )

        is IconSource.VectorIcon -> Icon(
            imageVector = icon.imageVector,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint
        )
    }
}
