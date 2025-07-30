package id.anison.amanogawa.core.common


/**
 * Class that represent data as state
 */
sealed class DataState<out T> {
    data object Loading : DataState<Nothing>()
    data class Success<T>(val data: T) : DataState<T>()
    data class Error(val exception: Throwable?) : DataState<Nothing>()
}

inline fun <T, R> DataState<T>.mapDataState(transform: (T) -> R): DataState<R> = when (this) {
    is DataState.Success -> DataState.Success(transform(data))
    is DataState.Error -> this
    is DataState.Loading -> this
}
