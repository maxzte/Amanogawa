package id.anison.amanogawa.core.network.model.anime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResultDto(

    @SerialName("id")
    val animeId: Long,

    val name: String,

    @SerialName("en_jp_name")
    val enJpName: String,

    val year: Short,

    val season: Byte,

    val part: Byte,

    @SerialName("poster_mini")
    val posterMini: String,

    )
