package id.anison.amanogawa.core.data.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.anison.amanogawa.core.data.anime.toAnime
import id.anison.amanogawa.core.data.anime.toAnimeDbo
import id.anison.amanogawa.core.data.anime.toFilterParams
import id.anison.amanogawa.core.network.AmanogawaApi
import id.anison.amanogawa.core.domain.model.Filter
import id.anison.amanogawa.core.database.anime.entity.AnimeDbo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExtendedSearchPagingSource(
    private val api: AmanogawaApi,
    private val filter: Filter,
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

        val params = filter.toFilterParams()

        val response = api.getFilteredTitles(
            page = page,
            genres = params.genres,
            voices = params.voices,
            translators = params.translators,
            sounds = params.sounds,
            years = params.years,
        )

        response.fold(
            onSuccess = { dataResponse ->
                try {
                    val totalPage = dataResponse.pages
                    val titles: List<AnimeDbo> = dataResponse.data.map { it.toAnime().toAnimeDbo() }

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