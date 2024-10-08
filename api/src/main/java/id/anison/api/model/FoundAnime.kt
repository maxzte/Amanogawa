package id.anison.api.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
data class FoundAnime(
    val id: Long,
    val name: String,
    val en_jp_name: String,
    val year: Short,
    val season: Byte,
    val part: Byte,
    val poster_mini: String,
    @Transient val timestamp: Long = 0
)