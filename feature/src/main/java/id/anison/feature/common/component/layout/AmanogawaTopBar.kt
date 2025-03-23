package id.anison.feature.common.component.layout

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import id.anison.feature.R
import id.anison.feature.notification.NotificationsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun AmanogawaTopBar(
    title: String = "",
    isLogoVisible: Boolean = false,
    popNavigate: (() -> Unit)? = null,
    additionalActionBtnContent: @Composable (() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    windowInsets: WindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top),
    containerColor: Color = Color.Transparent,
    modifier: Modifier = Modifier,
) {
    val navigator = LocalNavigator.currentOrThrow

    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier,
            )
        },
        navigationIcon = {
            if (popNavigate != null) {
                IconButton(onClick = { popNavigate() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.arrow_back),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            if (isLogoVisible) {
                IconButton(onClick = {}, enabled = false) {
                    Icon(
                        painter = painterResource(R.drawable.amanogawa_logo),
                        contentDescription = stringResource(R.string.amanogawa),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        },
        actions = {
            if (additionalActionBtnContent != null) {
                additionalActionBtnContent()
            } else {
                IconButton(
                    onClick = {
                        navigator.push(NotificationsScreen())
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                    )
                }
            }
        },
        windowInsets = windowInsets,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = containerColor
        ),
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}
