package id.anison.amanogawa.core.network.model

import kotlinx.serialization.Serializable


@Serializable
data class AnimeIDsDto(
    val id: List<Long>,
    val count: Int
)

