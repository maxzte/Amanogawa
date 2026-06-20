package id.anison.amanogawa.core.network.di

import de.jensklingenberg.ktorfit.Ktorfit
import id.anison.amanogawa.core.network.AmanogawaService
import id.anison.amanogawa.core.network.createAmanogawaService
import id.anison.amanogawa.core.network.util.AmanogawaApiInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://amanogawa.space/api/"

@Module
@ComponentScan("id.anison.amanogawa.core.network")
class NetworkModule {

    @Single
    fun json(): Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    @Single
    fun httpClient(json: Json): HttpClient = HttpClient(OkHttp) {
        engine {
            addInterceptor(AmanogawaApiInterceptor())
            config {
                callTimeout(15, TimeUnit.SECONDS)
                readTimeout(15, TimeUnit.SECONDS)
            }
        }

        install(ContentNegotiation) {
            json(json)
        }

        install(Logging) {
            level = LogLevel.INFO
        }
    }

    @Single
    fun ktorfit(client: HttpClient): Ktorfit = Ktorfit.Builder()
        .baseUrl(BASE_URL)
        .httpClient(client)
        .build()

    @Single
    fun amanogawaService(ktorfit: Ktorfit): AmanogawaService = ktorfit.createAmanogawaService()
}
