package id.anison.amanogawa.core.data.anime

import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.anison.amanogawa.core.common.AndroidLogcatLogger
import id.anison.amanogawa.core.database.anime.entity.AnimeDbo
import id.anison.amanogawa.core.network.AmanogawaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AnimePagingSource(
    private val api: AmanogawaApi,
    private val log: AndroidLogcatLogger,
) : PagingSource<Int, AnimeDbo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AnimeDbo> =
        withContext(Dispatchers.IO) {
            log.i(TAG, "Work!")
            val page = params.key ?: 1
            log.d(TAG, "key is: $page")

            try {
                val titles: List<AnimeDbo>
                var next: Int?
                val prev: Int? = if (page > 1) page - 1 else null


                val response = api.getTitlesByPage(page).getOrThrow()
                titles = response.data.map { it.toAnime().toAnimeDbo() }
                val totalPage = response.pages
                next = if (page < totalPage) page + 1 else null

                log.i(TAG, "Prev: $prev, Next: $next")
                LoadResult.Page(data = titles, prevKey = prev, nextKey = next)
            } catch (e: Exception) {
                log.e(TAG, "ERROR: $e")
                LoadResult.Error(IllegalStateException("Unexpected load result"))
            }
        }

    override fun getRefreshKey(state: PagingState<Int, AnimeDbo>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    companion object {
        const val TAG = "PagingSourceForRemote"
    }

}