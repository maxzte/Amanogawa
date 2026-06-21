package id.anison.amanogawa.core.data.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.anison.amanogawa.core.common.Log
import id.anison.amanogawa.core.data.anime.toAnime
import id.anison.amanogawa.core.domain.model.Anime
import id.anison.amanogawa.core.network.AmanogawaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class RandomAnimePagingSource(
    private val api: AmanogawaApi,
) : PagingSource<Int, Anime>() {

    private var totalPages: Int = -1
    private val shuffledPages = mutableListOf<Int>()

    override fun getRefreshKey(state: PagingState<Int, Anime>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Anime> {
        return withContext(Dispatchers.IO) {
            try {
                if (totalPages == -1) {
                    val firstPage = api.getTitlesByPage(1).getOrThrow()
                    totalPages = firstPage.pages
                    shuffledPages.clear()
                    shuffledPages.addAll((1..totalPages).shuffled())
                }

                val index = params.key ?: 0
                if (index >= shuffledPages.size) {
                    return@withContext LoadResult.Page(
                        data = emptyList(),
                        prevKey = if (index > 0) index - 1 else null,
                        nextKey = null
                    )
                }

                val page = shuffledPages[index]
                Log.d(tag = TAG) { "Loading page index $index → page $page" }

                val titles = api.getTitlesByPage(page).getOrThrow().data.shuffled().map { it.toAnime() }

                return@withContext LoadResult.Page(
                    data = titles,
                    prevKey = if (index > 0) index - 1 else null,
                    nextKey = if (index < shuffledPages.lastIndex) index + 1 else null
                )
            } catch (e: Exception) {
                Log.e(tag = TAG) { "Error loading random page or anime: ${e.message}" }
                return@withContext LoadResult.Error(e)
            }
        }
    }

    companion object Companion {
        const val TAG = "RandomAnimePagingSource"
    }

}