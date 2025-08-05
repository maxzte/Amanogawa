package id.anison.amanogawa.feature.anime.ui

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
import id.anison.amanogawa.core.model.FilterTypeUI
import id.anison.amanogawa.core.model.TeamMemberTypeUI
import id.anison.amanogawa.core.model.TeamMemberUI
import id.anison.amanogawa.core.ui.R


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimeTeam(
    teamMembers: List<TeamMemberUI>,
    onWordClick: (Long, String, FilterTypeUI) -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = LocalContentColor.current
    val groupedTeamMembers = teamMembers.groupBy { it.teamMemberType }

    Column(modifier = modifier.fillMaxWidth()) {
        groupedTeamMembers.forEach { (type, teamMembersOfType) ->
            val filterType = when (type) {
                TeamMemberTypeUI.VOICE -> FilterTypeUI.DUBBER
                TeamMemberTypeUI.SOUND -> FilterTypeUI.SOUND_ENGINEER
                TeamMemberTypeUI.TRANSLATOR -> FilterTypeUI.TRANSLATOR
            }
            val label = when (type) {
                TeamMemberTypeUI.TRANSLATOR -> stringResource(id = R.string.translated)
                TeamMemberTypeUI.VOICE -> stringResource(id = R.string.voiced)
                TeamMemberTypeUI.SOUND -> stringResource(id = R.string.work_with_sound)
            }
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

                teamMembersOfType.forEachIndexed { index, teamMember ->
                    TextButton(
                        onClick = { onWordClick(teamMember.teamMemberId, teamMember.name, filterType) },
                        contentPadding = PaddingValues(0.dp),
                        shape = RectangleShape,
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(
                            text = if (index != teamMembersOfType.lastIndex) "${teamMember.name}," else teamMember.name,
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
