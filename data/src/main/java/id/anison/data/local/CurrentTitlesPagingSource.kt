package id.anison.data.local

import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.anison.api.AmanogawaApi
import id.anison.common.AndroidLogcatLogger
import id.anison.data.toCurrentTitleDbo
import id.anison.database.model.CurrentTitlesDbo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class CurrentTitlesPagingSource(
    private val api: AmanogawaApi,
    private val log: AndroidLogcatLogger,
) : PagingSource<Int, CurrentTitlesDbo>() {
    companion object {
        const val TAG = "CurrentPagingSource"
    }

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, CurrentTitlesDbo> = withContext(Dispatchers.IO) {
        val pageSize = params.loadSize
        val pageIndex = params.key ?: 0
        try {
            val response = api.getCurrentTitles().getOrThrow()
            val currentTitles: List<CurrentTitlesDbo> = response.map { it.toCurrentTitleDbo() }
            LoadResult.Page(
                data = currentTitles,
                prevKey = if (pageIndex == 0) null else pageIndex - 1,
                nextKey = if (currentTitles.size < pageSize) null else pageIndex + 1
            )
        } catch (e: Exception) {
            log.e(TAG, "ERROR: $e")
            LoadResult.Error(IllegalStateException("Unexpected load result"))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CurrentTitlesDbo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}