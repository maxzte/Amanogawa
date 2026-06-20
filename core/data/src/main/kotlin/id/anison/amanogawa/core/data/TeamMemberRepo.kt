package id.anison.amanogawa.core.data

import id.anison.amanogawa.core.common.Log
import id.anison.amanogawa.core.domain.model.Team
import id.anison.amanogawa.core.domain.repository.TeamMemberRepo
import id.anison.amanogawa.core.network.AmanogawaApi
import id.anison.amanogawa.core.network.model.TeamDto
import org.koin.core.annotation.Single


@Single
class TeamMemberRepoImpl(
    private val api: AmanogawaApi,
) : TeamMemberRepo {

    override suspend fun getTeam() = withDispatcherBlock {
        Log.d(tag = TAG) { "getTeam" }
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