package id.anison.amanogawa.core.ui.common.component.button

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import id.anison.amanogawa.core.ui.R


@Composable
fun FilledTopBarIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
    backgroundColor: Color = Color.Unspecified
) {
    FilledIconButton(
        onClick = {
            onClick()
        },
        content = {
            content()
        },
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = backgroundColor
        )
    )
}

@Preview
@Composable
fun FilledTopBarIconPreview(modifier: Modifier = Modifier) {
    FilledTopBarIcon(
      onClick = {},
        content = {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = stringResource(id = R.string.liked)
            )
        }
    )
}