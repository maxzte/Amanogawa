package id.anison.amanogawa.core.network

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import id.anison.amanogawa.core.network.model.FilterDto
import id.anison.amanogawa.core.network.model.TeamDto
import id.anison.amanogawa.core.network.model.anime.AnimeDetailsDto
import id.anison.amanogawa.core.network.model.anime.AnimeDto
import id.anison.amanogawa.core.network.model.anime.AnimeEpisodesDto
import id.anison.amanogawa.core.network.model.anime.AnimeTitlesDto
import id.anison.amanogawa.core.network.model.anime.SearchResultDto

interface AmanogawaService {

    @GET("titles")
    suspend fun getTitlesByPage(@Query("page") page: Int): AnimeTitlesDto<AnimeDto>

    @GET("titles/current")
    suspend fun getCurrentTitles(): List<AnimeDto>

    @GET("title/{id}")
    suspend fun getAnime(@Path("id") id: Long): AnimeDetailsDto

    @GET("episodes/{id}")
    suspend fun getAnimeEpisodes(
        @Path("id") id: Long,
        @Query("page") page: Int
    ): AnimeEpisodesDto

    @GET("filters")
    suspend fun getFilter(): FilterDto

    @GET("titles")
    suspend fun getFilteredTitles(
        @Query("page") page: Int,
        @Query("y") years: String?,
        @Query("g") genres: String?,
        @Query("v") voices: String?,
        @Query("s") sounds: String?,
        @Query("t") translators: String?,
    ): AnimeTitlesDto<AnimeDto>

    @GET("random")
    suspend fun getRandomAnime(): List<String>

    @GET("search")
    suspend fun searchAnimeByName(@Query("s") name: String): List<SearchResultDto>

    @GET("team")
    suspend fun getTeam(): List<TeamDto>
}
