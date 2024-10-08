package id.anison.feature.common.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import id.anison.feature.R
import id.anison.feature.ui.LocalGradientColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    modifier: Modifier = Modifier,
    popNavigate: (() -> Unit)? = null,
    title: String,
    actionBtnContent: @Composable (() -> Unit)? = null,
    containerColor: Color = LocalGradientColors.current.top,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge,
                modifier = modifier,
            )
        },
        navigationIcon = {
            if (popNavigate != null)
                IconButton(onClick = { popNavigate() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.arrow_back)
                    )
                }
        },
        actions = { if (actionBtnContent != null) actionBtnContent() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
        ),
        scrollBehavior = scrollBehavior
    )
}