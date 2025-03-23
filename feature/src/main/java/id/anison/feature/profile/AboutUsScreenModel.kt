package id.anison.feature.profile

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.data.ReqRes
import id.anison.data.search.SearchRepo
import id.anison.feature.common.DataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class AboutUsScreenModel(
    private val searchRepo: SearchRepo
) : StateScreenModel<AboutUsState>(AboutUsState()) {

    private val _teamState = MutableStateFlow(AboutUsState())
    val teamState: StateFlow<AboutUsState> = _teamState.asStateFlow()

    init {
        getTeam()
    }

    private fun getTeam() {
        val ukrainian = Locale("uk")
        //if (Locale.getDefault().language == ukrainian.language) {
        screenModelScope.launch {
            when (val res = searchRepo.getTeam()) {
                is ReqRes.Error -> {
                    _teamState.update { state ->
                        state.copy(team = DataState.Error(res.error))
                    }
                }

                is ReqRes.InProgress -> {
                    _teamState.update { it.copy(team = DataState.Loading) }
                }

                is ReqRes.Success -> {
                    _teamState.update { state ->
                        state.copy(team = DataState.Success(res.data.map { it.toTeamItemUI() }))
                    }
                }
            }
        }
        //}
    }
}

data class AboutUsState(
    val team: DataState<List<TeamMemberUI>> = DataState.Loading,
    val isLoading: Boolean = true,
)