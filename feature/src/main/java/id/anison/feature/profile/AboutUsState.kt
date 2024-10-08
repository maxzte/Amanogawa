package id.anison.feature.profile

import id.anison.feature.model.TeamItemUI
import id.anison.feature.common.DataState


data class AboutUsState(
    val team: DataState<List<TeamItemUI>>? = null,
    val isLoading: Boolean = true,
){
    companion object {
        val None = AboutUsState()
    }
}