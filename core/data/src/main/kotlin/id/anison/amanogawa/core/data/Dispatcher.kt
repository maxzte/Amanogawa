package id.anison.amanogawa.core.data

import id.anison.amanogawa.core.model.DataState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun <T> runDataState(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    block: suspend () -> T,
): DataState<T> = withContext(dispatcher) {
    try {
        DataState.Success(data = block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        DataState.Error(exception = e.toAppError())
    }
}
