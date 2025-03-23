package id.anison.feature.common.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


@Composable
fun DetailText(
    modifier: Modifier = Modifier,
    teammateName: AnnotatedString? = null,
    label: String,
    value: String,
) {
    Text(
        text = buildAnnotatedString {
            if (teammateName == null) append(
                AnnotatedString(
                    text = label,
                    spanStyle = SpanStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                    )
                )
            )
            else append(teammateName)
            append(
                AnnotatedString(
                    text = value,
                    spanStyle = SpanStyle(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                    )
                )
            )
        },
        modifier = modifier,
    )
}