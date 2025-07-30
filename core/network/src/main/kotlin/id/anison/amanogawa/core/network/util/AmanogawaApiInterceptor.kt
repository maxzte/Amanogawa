package id.anison.amanogawa.core.network.util

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.IOException


class AmanogawaApiInterceptor(
    private val maxAttempts: Int = 3,
    private val delayMillis: Long = 5000,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var lastException: IOException? = null

        repeat(maxAttempts) { attempt ->
            try {
                val originalRequest = chain.request()
                val requestWithUserAgent = originalRequest
                    .newBuilder()
                    .applyDefaultHeaders()
                    .build()

                return chain.proceed(requestWithUserAgent)
            } catch (e: IOException) {
                lastException = e
                if (attempt == maxAttempts) {
                    throw lastException
                }
                Thread.sleep(delayMillis * attempt)
            }
        }
        throw lastException ?: IOException("Unknown error")
    }

    private fun Request.Builder.applyDefaultHeaders(): Request.Builder = this
        .header(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
        )
        .header("Content-Type", "application/json")
}