package id.anison.data

import id.anison.api.model.TeamMember

data class TeamMemberDto(
    val teamMemberId: Int = 0,
    val isCurrent: Boolean = false,
    val isOther: Boolean = false,
    val isSound: Boolean = false,
    val isTransl: Boolean = false,
    val isVoice: Boolean = false,
    val name: String = "",
    val roleText: String = "",
    val sound: String? = "",
    val titles: List<Int> = listOf(),
    val translator: String? = "",
    val userOrder: Int = 0,
    val voice: String? = "",
)

internal fun TeamMember.toTeamMemberDto() = TeamMemberDto(
    teamMemberId = teamMemberId,
    isCurrent = isCurrent,
    isOther = isOther,
    isSound = isSound,
    isTransl = isTransl,
    isVoice = isVoice,
    name = name,
    roleText = roleText,
    sound = sound,
    titles = titles,
    translator = translator,
    userOrder = userOrder,
    voice = voice,
)