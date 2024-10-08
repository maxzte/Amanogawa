package id.anison.data.local

import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.anison.api.AmanogawaApi
import id.anison.data.model.DataFilter
import id.anison.data.toTitleDbo
import id.anison.database.model.TitleDbo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExtendedSearchPagingSource(
    private val api: AmanogawaApi,
    private val filter: DataFilter,
) : PagingSource<Int, TitleDbo>() {
    override fun getRefreshKey(state: PagingState<Int, TitleDbo>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TitleDbo> =
        withContext(Dispatchers.IO) {
            val page = params.key ?: 1
            val genresIds: List<Int> = filter.genres.map { it.id }
            val voicesIds: List<Int> = filter.dubers.v.map { it.id }
            val soundsIds: List<Int> = filter.dubers.s.map { it.id }
            val translatorsIds: List<Int> = filter.dubers.t.map { it.id }
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
                        val titles: List<TitleDbo> = dataResponse.data.map { it.toTitleDbo() }

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