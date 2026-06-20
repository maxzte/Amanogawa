package id.anison.amanogawa.core.data

import id.anison.amanogawa.core.common.Log
import id.anison.amanogawa.core.data.team.toTeam
import id.anison.amanogawa.core.domain.repository.TeamMemberRepo
import id.anison.amanogawa.core.network.AmanogawaApi
import org.koin.core.annotation.Single


@Single
class TeamMemberRepoImpl(
    private val api: AmanogawaApi,
) : TeamMemberRepo {

    override suspend fun getTeam() = runDataState {
        Log.d(tag = TAG) { "getTeam" }
        api.getTeam().getOrThrow().map { it.toTeam() }
    }

    companion object {
        private const val TAG = "TeamMemberRepo"
    }

}
