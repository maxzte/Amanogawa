package id.anison.amanogawa.core.model

import java.io.IOException


sealed class AppError(
    message: String,
    override val cause: Throwable? = null,
) : Throwable(message, cause) {

    class Network(cause: IOException) : AppError(
        message = "No internet connection",
        cause = cause,
    )

    class Server(val code: Int, responseBody: String? = null) : AppError(
        message = "Server error ($code)",
    )

    class Parse(cause: Throwable) : AppError(
        message = "Data parse error",
        cause = cause,
    )

    class Unknown(cause: Throwable? = null) : AppError(
        message = cause?.message ?: "Unknown error",
        cause = cause,
    )
}
