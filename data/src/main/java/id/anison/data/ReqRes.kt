package id.anison.data

sealed class ReqRes<out E : Any>(open val data: E? = null) {
    class InProgress<E : Any>(data: E? = null) : ReqRes<E>(data)
    class Success<E : Any>(override val data: E) : ReqRes<E>(data)
    class Error<E : Any>(data: E? = null, val error: Throwable? = null) : ReqRes<E>(data)
}
