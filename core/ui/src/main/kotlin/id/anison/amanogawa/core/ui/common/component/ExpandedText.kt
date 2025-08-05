package id.anison.amanogawa.core.ui.common.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import id.anison.amanogawa.core.ui.R


@Composable
fun ExpandedText(
    description: String,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val textToShow = if (expanded) {
        buildAnnotatedString {
            append(description)
            if (description.length > 100) {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                ) {
                    append(" ${stringResource(R.string.less)}")
                }
            }
        }
    } else {
        buildAnnotatedString {
            append(description.take(100))
            if (description.length > 100) {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    ),
                ) {
                    append(stringResource(R.string.more))
                }
            }
        }
    }

    Text(
        text = textToShow,
        maxLines = if (expanded) Int.MAX_VALUE else 3,
        modifier = Modifier
            .animateContentSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                expanded = !expanded
            },
    )
}