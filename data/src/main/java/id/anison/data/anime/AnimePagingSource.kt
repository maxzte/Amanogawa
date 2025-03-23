package id.anison.data.anime

import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.anison.api.AmanogawaApi
import id.anison.common.AndroidLogcatLogger
import id.anison.database.anime.entity.AnimeDbo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AnimePagingSource(
    private val api: AmanogawaApi,
    private val log: AndroidLogcatLogger,
    private val isCurrent: Boolean = false,
) : PagingSource<Int, AnimeDbo>() {
    companion object {
        const val TAG_PAGING_SOURCE = "PagingSourceForRemote"
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AnimeDbo> =
        withContext(Dispatchers.IO) {
            log.i(TAG_PAGING_SOURCE, "Work!")
            val page = params.key ?: 1
            log.d(TAG_PAGING_SOURCE, "key is: $page")

            try {
                val titles: List<AnimeDbo>
                var next: Int? = null
                var prev: Int? = if (page > 1) page - 1 else null

                if (isCurrent) {
                    titles = api.getCurrentTitles().getOrThrow().map { it.toAnimeDto().toAnimeDbo() }
                    next = null
                } else {
                    val response = api.getTitlesByPage(page).getOrThrow()
                    titles = response.data.map { it.toAnimeDto().toAnimeDbo() }
                    val totalPage = response.pages
                    next = if (page < totalPage) page + 1 else null
                }

                log.i(TAG_PAGING_SOURCE, "Prev: $prev, Next: $next")
                LoadResult.Page(data = titles, prevKey = prev, nextKey = next)
            } catch (e: Exception) {
                log.e(TAG_PAGING_SOURCE, "ERROR: $e")
                LoadResult.Error(IllegalStateException("Unexpected load result"))
            }
        }

    override fun getRefreshKey(state: PagingState<Int, AnimeDbo>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }
}