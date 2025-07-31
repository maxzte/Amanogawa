package id.anison.amanogawa.core.domain

import id.anison.amanogawa.core.domain.model.Team
import id.anison.amanogawa.core.model.TeamUI

fun Team.toTeamUI() = TeamUI(
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
    voice = voice
)