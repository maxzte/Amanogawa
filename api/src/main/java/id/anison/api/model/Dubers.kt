package id.anison.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Dubers(
    val s: Map<Long, String> = emptyMap(),
    val t: Map<Long, String> = emptyMap(),
    val v: Map<Long, String> = emptyMap(),
)