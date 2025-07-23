package id.anison.amanogawa.core.data.filter

import id.anison.amanogawa.core.database.teammember.TeamMemberTypeDbo
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

//internal fun DubberDbo.toTeamMember() = TeamMember(
//    teamMemberId = dubberId,
//    name = name,
//    teamMemberType = t
//)

internal fun TeamMemberTypeDbo.toTeamMemberType() = TeamMemberType.valueOf(this.name)

//internal fun TeamMember.toDubberDbo() = DubberDbo(
//    dubberId = dubberId,
//    name = name,
//    type = type.toDubberTypeDbo()
//)

internal fun TeamMemberType.toDubberTypeDbo() = TeamMemberTypeDbo.valueOf(this.name)
