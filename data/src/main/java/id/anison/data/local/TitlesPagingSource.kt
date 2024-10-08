package id.anison.data.local

import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.anison.api.AmanogawaApi
import id.anison.common.AndroidLogcatLogger
import id.anison.data.repository.AmanogawaRepository.Companion.TAG
import id.anison.data.toTitleDbo
import id.anison.database.model.TitleDbo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TitlesPagingSource(
    private val api: AmanogawaApi,
    private val log: AndroidLogcatLogger,
) : PagingSource<Int, TitleDbo>() {
    companion object {
        const val TAG_PAGING_SOURCE = "PagingSourceForRemote"
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TitleDbo> =
        withContext(Dispatchers.IO) {
            log.i(TAG_PAGING_SOURCE, "Work!")
            val page = params.key ?: 1
            log.d(TAG, "key is: $page")

            try {
                val response = api.getTitles(page)
                val totalPage = response.getOrThrow().pages
                val titles: List<TitleDbo> = response.getOrThrow().data.map { it.toTitleDbo() }
                val prev: Int? = if (page > 1) page - 1 else null
                log.i(TAG_PAGING_SOURCE, "Prev: $prev")
                val next: Int? = if (page < totalPage) page + 1 else null
                log.i(TAG_PAGING_SOURCE, "Next: $prev")
                LoadResult.Page(data = titles, prevKey = prev, nextKey = next)
            } catch (e: Exception) {
                log.e(TAG_PAGING_SOURCE, "ERROR: $e")
                LoadResult.Error(IllegalStateException("Unexpected load result"))
            }
        }

    override fun getRefreshKey(state: PagingState<Int, TitleDbo>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }
}