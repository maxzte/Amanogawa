package id.anison.amanogawa.core.network.model.anime

import id.anison.amanogawa.core.network.model.GenreDto
import id.anison.amanogawa.core.network.model.GenresSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * class that represent [anime details](https://amanogawa.space/anime/133/Dandadan)
 */
@Serializable
data class AnimeDetailsDto(

    @SerialName("id")
    val animeId: Long,

    val name: String,

    @SerialName("en_jp_name")
    val enJpName: String,

    val year: Short,

    val duration: String? = "",

    val company: String,

    val director: String,

    @SerialName("descrition")
    val description: String,

    @SerialName("bot_id")
    val botId: Int,

    val season: Byte,

    val part: Byte,

    val views: Int,

    @SerialName("post_date")
    val postDate: String? = "",

    @SerialName("post_update")
    val postUpdate: String? = "",

    @SerialName("episodes_total")
    val episodesTotal: String? = "",

    @SerialName("episodes_custom")
    val episodesCustom: String? = "",

    @SerialName("dubed_episodes")
    val dubbedEpisodes: Int,

    @SerialName("extra_total")
    val extraTotal: Int,

    @SerialName("ova_total")
    val ovaTotal: Int,

    val liked: Int = 0,

    val disliked: Int = 0,

    @SerialName("is_current")
    val isCurrent: Boolean,

    @SerialName("is_movie")
    val isMovie: Boolean = false,

    @SerialName("is_dubed")
    val isDubbed: Boolean = false,

    @SerialName("is_haveSubs")
    val isHaveSubs: Boolean = false,

    @SerialName("torrent_url")
    val torrentUrl: String? = "",

    @SerialName("torrent_4k_url")
    val torrent4kUrl: String? = "",

    @SerialName("poster_maker")
    val posterMaker: String? = "",

    val poster: String? = "",

    @SerialName("poster_thumb")
    val posterThumb: String,

    @SerialName("poster_mini")
    val posterMini: String,

    @SerialName("screens_f")
    val screensF: List<List<String>> = emptyList(),

    @SerialName("voices_f")
    val voicesF: List<List<String>> = emptyList(),

    @SerialName("sounds_f")
    val soundsF: List<List<String>> = emptyList(),

    @SerialName("transl_f")
    val translF: List<List<String>> = emptyList(),

    @Serializable(with = GenresSerializer::class)
    @SerialName("genres_f")
    val genres: List<GenreDto> = emptyList(),

    @SerialName("group_f")
    val groupF: List<AnimeDto>? = emptyList(),

    @SerialName("episode_count")
    val episodeCount: String,

    @SerialName("bot_url")
    val botUrl: String,

)
