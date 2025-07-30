package id.anison.amanogawa.core.data.filter

import id.anison.amanogawa.core.common.DataState


interface FilterRepo {

    suspend fun updateFilter(): DataState<Filter>

    suspend fun saveSearchFilter(filter: Filter)

    suspend fun isExist(filterType: FilterType): Boolean


}