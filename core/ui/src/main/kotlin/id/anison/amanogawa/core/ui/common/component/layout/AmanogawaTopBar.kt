package id.anison.amanogawa.core.ui.common.component.layout

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import id.anison.amanogawa.core.ui.common.LineSide
import id.anison.amanogawa.core.ui.common.grayLine


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmanogawaTopBar(
    title: String = "",
    titleContent: @Composable () -> Unit = {},
    navigationIconContent: @Composable () -> Unit = {},
    additionalActionBtnContent: @Composable () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    containerColor: Color = Color.Unspecified,
    scrollContainerColor: Color = Color.Unspecified,
    showBottomLine: Boolean = true,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            if (title != "") {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier,
                )
            } else {
                titleContent()
            }
        },
        navigationIcon = {
            navigationIconContent()
        },
        actions = {
            additionalActionBtnContent()
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = scrollContainerColor,
        ),
        modifier = modifier
            .then(if (showBottomLine) Modifier.grayLine(side = LineSide.Bottom) else Modifier),
    )
}
