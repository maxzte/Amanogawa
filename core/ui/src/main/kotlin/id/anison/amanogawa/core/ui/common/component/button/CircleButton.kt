package id.anison.amanogawa.core.ui.common.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import id.anison.amanogawa.core.ui.R


@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
) {
    IconButton(
        onClick = { onFavoriteClick() },
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = backgroundColor ?: Color.Unspecified
        ),
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            tint = if (isFavorite) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            },
            contentDescription = if (isFavorite) {
                stringResource(id = R.string.unliked)
            } else {
                stringResource(id = R.string.liked)
            },
        )
    }
}

@Composable
fun CircleButton(
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    onClick: () -> Unit,
    iconContent: @Composable () -> Unit
) {
    IconButton(
        modifier = modifier
            .clip(CircleShape)
            .background(color = containerColor),
        onClick = { onClick() },
    ) {
        iconContent()
    }
}