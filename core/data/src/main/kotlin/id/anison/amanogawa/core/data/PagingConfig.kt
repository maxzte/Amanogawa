package id.anison.amanogawa.core.data

import androidx.paging.PagingConfig

object PageConfig {
    const val PAGE_SIZE = 10
    const val PREFETCH_DISTANCE = PAGE_SIZE * 2
    const val MAX_SIZE = PAGE_SIZE + (PREFETCH_DISTANCE * 2)
}

val config: PagingConfig = PagingConfig(
    enablePlaceholders = true,
    pageSize = PageConfig.PAGE_SIZE,
    prefetchDistance = PageConfig.PREFETCH_DISTANCE,
//    maxSize = PageConfig.MAX_SIZE,
)