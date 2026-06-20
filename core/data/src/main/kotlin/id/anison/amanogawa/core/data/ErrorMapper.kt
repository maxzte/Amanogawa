package id.anison.amanogawa.core.data

import id.anison.amanogawa.core.model.AppError
import java.io.IOException


internal fun Throwable.toAppError(): AppError = when (this) {
    is AppError -> this
    is IOException -> AppError.Network(this)
    else -> AppError.Unknown(this)
}
