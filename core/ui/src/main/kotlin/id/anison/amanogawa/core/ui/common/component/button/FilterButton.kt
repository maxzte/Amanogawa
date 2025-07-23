package id.anison.amanogawa.core.ui.common.component.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun FilterButton(
    name: String,
    onClick: () -> Unit,
    paddingValues: PaddingValues = PaddingValues(8.dp),
    maxWidth: Dp = Dp.Unspecified,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = {
            onClick()
        },
        shape = RoundedCornerShape(4.dp),
        modifier = modifier,
        contentPadding = paddingValues,
    ) {
        Text(
            text = name,
            textAlign = TextAlign.Start,
            modifier = Modifier.width(maxWidth)
        )
    }
}