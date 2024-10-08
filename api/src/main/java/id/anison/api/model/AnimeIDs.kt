package id.anison.api.model

import kotlinx.serialization.Serializable


@Serializable
data class AnimeIDs(
    val id: List<Long>,
    val count: Int
)

