package id.anison.data.anime

import id.anison.api.model.anime.AnimeDetails
import id.anison.data.search.DubersDto
import id.anison.data.search.toDubersDto
import id.anison.data.genre.GenreDto
import id.anison.data.genre.toGenreDbo
import id.anison.data.genre.toGenreDto
import id.anison.data.search.toDubersDbo
import id.anison.database.anime.entity.AnimeDbo
import id.anison.database.anime.entity.AnimeDetailsDbo


data class AnimeDetailsDto(
    val animeDto: AnimeDto,
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
    val genres: List<GenreDto>,
    val dubers: DubersDto,
    val groupF: List<AnimeDto>? = emptyList(),
    val botUrl: String,
    val rating: Int = 0,
)

internal fun AnimeDetailsDto.isSame(other: AnimeDetailsDto): Boolean {
    return this.animeDto.animeId == other.animeDto.animeId &&
            this.animeDto.name == other.animeDto.name &&
            this.animeDto.enJpName == other.animeDto.enJpName &&
            this.animeDto.season == other.animeDto.season &&
            this.animeDto.isCurrent == other.animeDto.isCurrent &&
            this.animeDto.year == other.animeDto.year &&
            this.animeDto.season == other.animeDto.season &&
            this.animeDto.part == other.animeDto.part &&
            this.animeDto.views == other.animeDto.views &&
            this.animeDto.isOva == other.animeDto.isOva &&
            this.animeDto.isExtra == other.animeDto.isExtra &&
            this.animeDto.ovaTotal == other.animeDto.ovaTotal &&
            this.animeDto.extraTotal == other.animeDto.extraTotal &&
            this.animeDto.dubedEpisodes == other.animeDto.dubedEpisodes &&
            this.animeDto.episodesCustom == other.animeDto.episodesCustom &&
            this.animeDto.dubedEpisodes == other.animeDto.dubedEpisodes &&
            this.animeDto.episodesTotal == other.animeDto.episodesTotal &&
            this.animeDto.posterThumb == other.animeDto.posterThumb &&
            this.animeDto.posterThumb == other.animeDto.posterThumb &&
            this.animeDto.posterMini == other.animeDto.posterMini &&
            this.animeDto.schedule == other.animeDto.schedule &&
            this.animeDto.episodeCount == other.animeDto.episodeCount &&
            this.animeDto.newEpisode == other.animeDto.newEpisode &&
            this.animeDto.time == other.animeDto.time &&
            this.animeDto.timeStr == other.animeDto.timeStr &&
            this.description == other.description &&
            this.rating == other.rating &&
            this.isMovie == other.isMovie &&
            this.duration == other.duration &&
            this.company == other.company &&
            this.botId == other.botId &&
            this.botUrl == other.botUrl &&
            this.liked == other.liked &&
            this.disliked == other.disliked &&
            this.isMovie == other.isMovie &&
            this.isDubed == other.isDubed &&
            this.isHaveSubs == other.isHaveSubs &&
            this.torrentUrl == other.torrentUrl &&
            this.torrentUrl == other.torrentUrl &&
            this.posterMaker == other.posterMaker &&
            this.poster == other.poster &&
            this.poster == other.poster &&
            this.screensF == other.screensF &&
            this.genres == other.genres &&
            this.dubers == other.dubers &&
            this.groupF == other.groupF
}

internal fun AnimeDetails.toAnimeDetailsDto(): AnimeDetailsDto {
    val animeDto = this.toAnimeDto()
    val dubers = DubersDto(
        s = soundsF.associate { it[0].toLong() to it[1] },
        t = translF.associate { it[0].toLong() to it[1] },
        v = voicesF.associate { it[0].toLong() to it[1] },
    )
    return AnimeDetailsDto(
        animeDto = animeDto,
        duration = duration.toString(),
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
        posterMaker = posterMaker.toString(),
        poster = poster.toString(),
        screensF = screensF,
        genres = genres.map { it.toGenreDto() },
        dubers = dubers,
        groupF = groupF?.map { it.toAnimeDto() },
        botUrl = botUrl,
    )
}

internal fun AnimeDetailsDbo.toAnimeDetailsDto(animeDto: AnimeDto): AnimeDetailsDto {
    return AnimeDetailsDto(
        animeDto = animeDto,
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
}

internal fun AnimeDetails.toAnimeDbo(): AnimeDbo {
    return this.toAnimeDbo()
}

internal fun AnimeDetailsDto.toAnimeDetailsDbo() = AnimeDetailsDbo(
    animeId = animeDto.animeId,
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
    genres = genres.map { it.toGenreDbo() },
    dubers = dubers.toDubersDbo(),
    groupF = groupF?.map { it.toAnimeDbo() },
    botUrl = botUrl,
    rating = rating,
)