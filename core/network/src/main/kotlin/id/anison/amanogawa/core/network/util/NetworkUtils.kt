package id.anison.amanogawa.core.network.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope


fun List<Any>.toQueryParam(): String = joinToString(prefix = "[", postfix = "]", separator = ",") { it.toString() }

suspend fun <T> fetchAllPages(
    firstPage: PageDto<T>,
    fetcher: suspend (Int) -> List<T>,
): List<T> {
    if (firstPage.pages <= 1) return firstPage.data

    val otherPagesData = coroutineScope {
        (2..firstPage.pages).map { page ->
            async(Dispatchers.IO) {
                fetcher(page)
            }
        }.awaitAll().flatten()
    }
    return firstPage.data + otherPagesData
}

data class PageDto<T>(
    val pages: Int,
    val data: List<T>,
)
