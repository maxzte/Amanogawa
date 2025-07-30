package id.anison.amanogawa.feature.anime

import cafe.adriel.voyager.core.registry.screenModule
import id.anison.amanogawa.core.navigation.SharedScreen


val animeDetailsScreenModule = screenModule {
    register<SharedScreen.AnimeDetails> { provider ->
        AnimeDetailsScreen(animeId = provider.animeId)
    }
}