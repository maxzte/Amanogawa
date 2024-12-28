package id.anison.api

import id.anison.api.model.Anime
import id.anison.api.model.AnimeEpisodes
import id.anison.api.model.AnimeIDs
import id.anison.api.model.AnimeTitles
import id.anison.api.model.CurrentTitle
import id.anison.api.model.Episode
import id.anison.api.model.Filter
import id.anison.api.model.FoundAnime
import id.anison.api.model.TeamItem
import id.anison.api.model.Title

interface AmanogawaApi {
    suspend fun getAll(): Result<List<Title>>
    suspend fun getTitles(page: Int): Result<AnimeTitles>
    suspend fun getCurrentTitles(): Result<List<CurrentTitle>>
    suspend fun getAnime(id: Long): Result<Anime>
    suspend fun getAnimeEpisodes(id: Long): Result<AnimeEpisodes<Episode>>
    suspend fun getFilter(): Result<Filter>
    suspend fun getFilteredTitles(
        page: Int,
        years: List<Int>?,
        genres: List<Int>?,
        voices: List<Int>?,
        sounds: List<Int>?,
        translators: List<Int>?,
    ): Result<AnimeTitles>

    suspend fun getRandomAnime(): Result<Long>
    suspend fun getCountAnimeAndTheirIDs(): Result<AnimeIDs>
    suspend fun searchAnimeByName(name: String): Result<List<FoundAnime>>
    suspend fun getTeam(): Result<List<TeamItem>>
}
