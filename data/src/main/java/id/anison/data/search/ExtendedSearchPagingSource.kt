package id.anison.data.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.anison.api.AmanogawaApi
import id.anison.data.anime.toAnimeDbo
import id.anison.data.anime.toAnimeDto
import id.anison.database.anime.entity.AnimeDbo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExtendedSearchPagingSource(
    private val api: AmanogawaApi,
    private val filter: FilterDto,
) : PagingSource<Int, AnimeDbo>() {
    override fun getRefreshKey(state: PagingState<Int, AnimeDbo>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, AnimeDbo> = withContext(Dispatchers.IO) {
        val page = params.key ?: 1
        val genresIds = filter.genres.map { it.genreId }
        val voicesIds = filter.dubers.v.map { it.key }
        val soundsIds = filter.dubers.s.map { it.key }
        val translatorsIds = filter.dubers.t.map { it.key }

        val response = api.getFilteredTitles(
            page,
            filter.years,
            genresIds,
            voicesIds,
            soundsIds,
            translatorsIds
        )
        response.fold(
            onSuccess = { dataResponse ->
                try {
                    val totalPage = dataResponse.pages
                    val titles: List<AnimeDbo> = dataResponse.data.map { it.toAnimeDto().toAnimeDbo() }

                    val prev: Int? = if (page > 1) page - 1 else null
                    val next: Int? = if (page < totalPage) page + 1 else null

                    LoadResult.Page(data = titles, prevKey = prev, nextKey = next)
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }
            },
            onFailure = { e -> LoadResult.Error(e) }
        )
    }
}