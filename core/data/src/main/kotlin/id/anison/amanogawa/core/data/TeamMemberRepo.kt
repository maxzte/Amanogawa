package id.anison.amanogawa.core.data

import id.anison.amanogawa.core.common.Logger
import id.anison.amanogawa.core.domain.model.Team
import id.anison.amanogawa.core.domain.repository.TeamMemberRepo
import id.anison.amanogawa.core.network.AmanogawaApi
import id.anison.amanogawa.core.network.model.TeamDto


class TeamMemberRepoImpl(
    private val api: AmanogawaApi,
    private val log: Logger,
) : TeamMemberRepo {

    override suspend fun getTeam() = withDispatcherBlock {
        log.d(TAG, "getTeam")
        api.getTeam().getOrThrow().map { it.toTeam() }
    }

    companion object {
        private const val TAG = "TeamMemberRepo"
    }

}

private fun TeamDto.toTeam() = Team(
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