package id.anison.amanogawa.core.data.favorite

import id.anison.amanogawa.core.model.DataState
import id.anison.amanogawa.core.common.Log
import id.anison.amanogawa.core.database.favorite.FavoriteDao
import id.anison.amanogawa.core.domain.model.Favorite
import id.anison.amanogawa.core.domain.repository.FavoriteRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class FavoriteRepoImpl(
    private val favoriteDao: FavoriteDao,
) : FavoriteRepo {

    override fun getFavorites(): Flow<DataState<List<Favorite>>> = flow {
        emit(DataState.Loading)
        emitAll(
            favoriteDao.getAll().map { list ->
                DataState.Success(list.map { it.toFavorite() })
            }
        )
    }.catch { e ->
        Log.e(tag = TAG) { "Error getting data from database. Cause = $e" }
        emit(DataState.Error(e))
    }

    override fun isFavorite(id: Long): Flow<Boolean> = favoriteDao.isFavorite(id)

    override suspend fun addToFavorite(favorite: Favorite) {
        favoriteDao.insertFavorite(favorite.toFavoriteDbo())
    }

    override suspend fun removeFromFavorite(favorite: Favorite) {
        favoriteDao.deleteFavorite(favorite.toFavoriteDbo())
    }

    override suspend fun clearFavorites() {
        favoriteDao.deleteAllFavorites()
    }

    companion object {
        private const val TAG: String = "Favorite repo"
    }

}