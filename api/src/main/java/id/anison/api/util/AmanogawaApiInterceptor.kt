package id.anison.api.util

import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException


class AmanogawaApiInterceptor(
    private val maxAttempts: Int = 3,
    private val delayMillis: Long = 5000,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var lastException: IOException? = null

        for (attempt in 1..maxAttempts) {
             try {
                val originalRequest = chain.request()
                val requestWithUserAgent = originalRequest
                    .newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .header("Content-Type", "application/json")
                    .build()

                return chain.proceed(requestWithUserAgent)
            } catch (e: IOException) {
                lastException = e
                if (attempt >= maxAttempts) {
                    throw lastException
                }
                Thread.sleep(delayMillis * attempt)
            }
        }
        throw lastException ?: IOException("Unknown error")
    }
}