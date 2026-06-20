package id.anison.amanogawa.core.network.model.anime

import kotlinx.serialization.Serializable


@Serializable
data class AnimeTitlesDto<E>(
    val pages: Int,
    val data: List<E>,
    val currentPage: Int = 1,
)
