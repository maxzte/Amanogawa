package id.anison.amanogawa.core.ui.common.component.button

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun RectangleButton(
    modifier: Modifier = Modifier,
    title: String,
    iconContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        modifier = modifier,
        onClick = { onClick() },
        shape = RoundedCornerShape(8),
    ) {
        if (iconContent != null) iconContent()
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}