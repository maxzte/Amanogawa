package id.anison.amanogawa.core.domain.repository

import id.anison.amanogawa.core.common.DataState
import id.anison.amanogawa.core.domain.model.Favorite
import kotlinx.coroutines.flow.Flow

interface FavoriteRepo {

    fun getFavorites(): Flow<DataState<List<Favorite>>>

    fun isFavorite(id: Long): Flow<Boolean>

    suspend fun addToFavorite(favorite: Favorite)

    suspend fun removeFromFavorite(favorite: Favorite)

    suspend fun clearFavorites()

}