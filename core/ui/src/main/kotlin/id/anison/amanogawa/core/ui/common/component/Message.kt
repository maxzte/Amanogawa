package id.anison.amanogawa.core.ui.common.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import id.anison.amanogawa.core.ui.spacing


@Composable
fun Message(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.medium),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text)
    }
}
