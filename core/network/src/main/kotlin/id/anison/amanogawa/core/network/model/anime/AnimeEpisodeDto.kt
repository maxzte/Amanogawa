package id.anison.amanogawa.core.network.model.anime

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AnimeEpisodesDto(

    val pages: Int = 0,

    val data: List<AnimeEpisodeDto> = emptyList(),

    @SerialName("bot_url")
    val botUrl: String = "",

)

@Serializable
data class AnimeEpisodeDto(

    @SerialName("id")
    val episodeId: Long,

    @SerialName("title_id")
    val animeId: Long,

    @SerialName("bot_id")
    val botId: Long,

    @SerialName("is_extra")
    val isExtra: Boolean = false,

    @SerialName("is_ova")
    val isOva: Boolean = false,

    val name: String = "",

    val number: Int = 0,

    @SerialName("post_date")
    val postDate: String = "",

    @SerialName("post_update")
    val postUpdate: String = "",

    val screen: String = "",

    @SerialName("screen_original")
    val screenOriginal: String = "",

)
