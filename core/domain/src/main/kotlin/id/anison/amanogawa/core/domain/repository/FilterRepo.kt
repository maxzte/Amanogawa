package id.anison.amanogawa.core.domain.repository

import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.domain.model.Filter
import id.anison.amanogawa.core.domain.model.FilterType

interface FilterRepo {

    suspend fun updateFilter(): DataState<Filter>

    suspend fun saveSearchFilter(filter: Filter)

    suspend fun isExist(filterType: FilterType): Boolean


}
