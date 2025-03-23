package id.anison.feature.anime.model

import id.anison.data.anime.AnimeDetailsDto
import id.anison.feature.search.model.DubersUI
import id.anison.feature.search.model.GenreUI
import id.anison.feature.search.model.toDubersDto
import id.anison.feature.search.model.toDubersUI
import id.anison.feature.search.model.toGenreDto
import id.anison.feature.search.model.toGenreUI


data class AnimeDetailsUI(
    val animeUI: AnimeUI,
    val duration: String = "",
    val company: String,
    val director: String,
    val description: String,
    val botId: Int,
    val liked: Int = 0,
    val disliked: Int = 0,
    val isMovie: Boolean = false,
    val isDubed: Boolean = false,
    val isHaveSubs: Boolean = false,
    val torrentUrl: String? = null,
    val torrent4kUrl: String? = null,
    val posterMaker: String = "",
    val poster: String = "",
    val screensF: List<List<String>> = emptyList(),
    val genres: List<GenreUI>,
    val dubers: DubersUI,
    val groupF: List<AnimeUI>? = emptyList(),
    val botUrl: String,
    val rating: Int = 0,
)

internal fun AnimeDetailsDto.toAnimeUI() = AnimeDetailsUI(
    animeUI = animeDto.toAnimeUI(),
    duration = duration,
    company = company,
    director = director,
    description = description,
    botId = botId,
    liked = liked,
    disliked = disliked,
    isMovie = isMovie,
    isDubed = isDubed,
    isHaveSubs = isHaveSubs,
    torrentUrl = torrentUrl,
    torrent4kUrl = torrent4kUrl,
    posterMaker = posterMaker,
    poster = poster,
    screensF = screensF,
    genres = genres.map { it.toGenreUI() },
    dubers = dubers.toDubersUI(),
    groupF = groupF?.map { it.toAnimeUI() },
    botUrl = botUrl,
    rating = rating,
)

internal fun AnimeDetailsUI.toAnimeDetailsDto() = AnimeDetailsDto(
    animeDto = animeUI.toAnimeDto(),
    duration = duration,
    company = company,
    director = director,
    description = description,
    botId = botId,
    liked = liked,
    disliked = disliked,
    isMovie = isMovie,
    isDubed = isDubed,
    isHaveSubs = isHaveSubs,
    torrentUrl = torrentUrl,
    torrent4kUrl = torrent4kUrl,
    posterMaker = posterMaker,
    poster = poster,
    screensF = screensF,
    genres = genres.map { it.toGenreDto() },
    dubers = dubers.toDubersDto(),
    groupF = groupF?.map { it.toAnimeDto() },
    botUrl = botUrl,
    rating = rating,
)