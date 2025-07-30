package id.anison.amanogawa.core.ui.common.component.button

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import id.anison.amanogawa.core.ui.R
import id.anison.amanogawa.core.ui.common.AmanogawaIcon


@Composable
fun ArrowBackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IconButton(onClick = { onClick() }) {
        Icon(
            imageVector = AmanogawaIcon.arrowBack,
            contentDescription = stringResource(id = R.string.arrow_back),
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}