package id.anison.amanogawa.core.domain.repository

import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.domain.model.Team


interface TeamMemberRepo {

    suspend fun getTeam(): DataState<List<Team>>

}
