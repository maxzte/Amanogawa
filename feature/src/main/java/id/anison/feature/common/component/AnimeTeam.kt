package id.anison.feature.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import id.anison.feature.R
import id.anison.feature.search.model.DubersUI
import id.anison.feature.search.model.FilterUI


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimeTeam(
    dubers: DubersUI,
    onWordClick: (filter: FilterUI, filterName: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = stringResource(R.string.translated)
    val v = stringResource(R.string.voiced)
    val s = stringResource(R.string.work_with_sound)
    val contentColor = LocalContentColor.current

    Column(modifier = modifier.fillMaxWidth()) {
        listOf(
            t to dubers.t,
            v to dubers.v,
            s to dubers.s,
        ).forEach { (label, data) ->
            val last = data.entries.last()
            if (data.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    TextButton(
                        onClick = {},
                        colors = ButtonDefaults.textButtonColors(disabledContentColor = contentColor),
                        contentPadding = PaddingValues(0.dp),
                        enabled = false,
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    data.forEach { entry ->
                        val (id, name) = entry
                        TextButton(
                            onClick = {
                                onWordClick(
                                    FilterUI(
                                        dubers = DubersUI(
                                            t = if (label == t) mapOf(id to name) else emptyMap(),
                                            v = if (label == v) mapOf(id to name) else emptyMap(),
                                            s = if (label == s) mapOf(id to name) else emptyMap(),
                                        )
                                    ),
                                    name
                                )
                            },
                            contentPadding = PaddingValues(0.dp),
                            shape = RectangleShape,
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(
                                text = if(last != entry) "$name," else name,
                                style = MaterialTheme.typography.bodyLarge,
                                textDecoration = TextDecoration.Underline,
                                color = contentColor,
                            )
                        }
                    }
                }
            }
        }
    }
}
