package id.anison.amanogawa.core.data.filter

import id.anison.amanogawa.core.network.model.TeamMemberDto
import id.anison.amanogawa.core.network.model.TeamMemberTypeDto


data class TeamMember(
    val teamMemberId: Long,
    val name: String = "",
    val teamMemberType: TeamMemberType,
)

enum class TeamMemberType {
    VOICE,
    SOUND,
    TRANSLATOR,
}

internal fun TeamMemberDto.toTeamMember() = TeamMember(
    teamMemberId = teamMemberId,
    name = name,
    teamMemberType = teamMemberType.toTeamMemberType()
)

internal fun TeamMemberTypeDto.toTeamMemberType() = TeamMemberType.valueOf(this.name)
