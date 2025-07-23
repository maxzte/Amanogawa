package id.anison.amanogawa.core.network

import id.anison.amanogawa.core.network.model.AnimeIDsDto
import id.anison.amanogawa.core.network.model.FilterDto
import id.anison.amanogawa.core.network.model.TeamDto
import id.anison.amanogawa.core.network.model.anime.AnimeDetailsDto
import id.anison.amanogawa.core.network.model.anime.AnimeDto
import id.anison.amanogawa.core.network.model.anime.AnimeEpisodesDto
import id.anison.amanogawa.core.network.model.anime.AnimeTitlesDto
import id.anison.amanogawa.core.network.model.anime.SearchResultDto
import okhttp3.HttpUrl

interface AmanogawaApi {

    fun baseUrl(): HttpUrl.Builder

    suspend fun getAll(): Result<AnimeTitlesDto<AnimeDto>>

    suspend fun getTitlesByPage(page: Int): Result<AnimeTitlesDto<AnimeDto>>

    suspend fun getCurrentTitles(): Result<List<AnimeDto>>

    suspend fun getAnime(id: Long): Result<AnimeDetailsDto>

    suspend fun getAnimeEpisodes(id: Long): Result<AnimeEpisodesDto>

    suspend fun getFilter(): Result<FilterDto>

    suspend fun getFilteredTitles(
        page: Int,
        years: List<Short>?,
        genres: List<Long>?,
        voices: List<Long>?,
        sounds: List<Long>?,
        translators: List<Long>?,
    ): Result<AnimeTitlesDto<AnimeDto>>

    suspend fun getRandomAnime(): Result<Long>

    suspend fun getCountAnimeAndTheirIDs(): Result<AnimeIDsDto>

    suspend fun searchAnimeByName(name: String): Result<List<SearchResultDto>>

    suspend fun getTeam(): Result<List<TeamDto>>
}
