package id.anison.feature.profile

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import id.anison.data.repository.AmanogawaRepository
import id.anison.data.ReqRes
import id.anison.feature.model.toTeamItemUI
import id.anison.feature.common.DataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class AboutUsScreenModel(
    private val repository: AmanogawaRepository
) : StateScreenModel<AboutUsState>(AboutUsState.None) {
    private val _teamState = MutableStateFlow(AboutUsState.None)
    val teamState: StateFlow<AboutUsState> = _teamState.asStateFlow()

    init {
        getTeam()
    }

    private fun getTeam() {
        val ukrainian = Locale("uk")
        //if (Locale.getDefault().language == ukrainian.language) {
            screenModelScope.launch {
                when (val res = repository.getTeam()) {
                    is ReqRes.Error -> _teamState.update { state ->
                        state.copy(team = res.error?.cause?.let { DataState.Error(it) })
                    }

                    is ReqRes.InProgress -> _teamState.update { it.copy(team = DataState.Loading) }

                    is ReqRes.Success -> _teamState.update { state ->
                        state.copy(team = DataState.Success(res.data.map { it.toTeamItemUI() }))
                    }
                }
            }
        //}
    }
}