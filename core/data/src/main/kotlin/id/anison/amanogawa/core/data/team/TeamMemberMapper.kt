package id.anison.amanogawa.core.data.team

import id.anison.amanogawa.core.domain.model.Team
import id.anison.amanogawa.core.network.model.TeamDto

internal fun TeamDto.toTeam() = Team(
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
