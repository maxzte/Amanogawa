package id.anison.amanogawa.core.data

import id.anison.amanogawa.core.model.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun <T> withDispatcherBlock(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    block: suspend () -> T,
): DataState<T> = withContext(dispatcher) {
    try {
        DataState.Success(data = block())
    } catch (e: Exception) {
        DataState.Error(exception = e.cause)
    }
}
