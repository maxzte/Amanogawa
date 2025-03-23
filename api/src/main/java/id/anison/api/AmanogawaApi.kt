package id.anison.api

import id.anison.api.model.AnimeIDs
import id.anison.api.model.Filter
import id.anison.api.model.TeamMember
import id.anison.api.model.anime.Anime
import id.anison.api.model.anime.AnimeDetails
import id.anison.api.model.anime.AnimeTitles
import id.anison.api.model.anime.Episodes
import id.anison.api.model.anime.FoundAnime
import okhttp3.HttpUrl

interface AmanogawaApi {

    fun baseUrl(): HttpUrl.Builder
    suspend fun getAll(): Result<AnimeTitles<Anime>>
    suspend fun getTitlesByPage(page: Int): Result<AnimeTitles<Anime>>
    suspend fun getCurrentTitles(): Result<List<Anime>>
    suspend fun getAnime(id: Long): Result<AnimeDetails>
    suspend fun getAnimeEpisodes(id: Long): Result<Episodes>
    suspend fun getFilter(): Result<Filter>
    suspend fun getFilteredTitles(
        page: Int,
        years: List<Short>?,
        genres: List<Long>?,
        voices: List<Long>?,
        sounds: List<Long>?,
        translators: List<Long>?,
    ): Result<AnimeTitles<Anime>>

    suspend fun getRandomAnime(): Result<Long>
    suspend fun getCountAnimeAndTheirIDs(): Result<AnimeIDs>
    suspend fun searchAnimeByName(name: String): Result<List<FoundAnime>>
    suspend fun getTeam(): Result<List<TeamMember>>
}
