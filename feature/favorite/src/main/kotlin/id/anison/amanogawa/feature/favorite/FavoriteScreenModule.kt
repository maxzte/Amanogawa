package id.anison.amanogawa.feature.favorite

import cafe.adriel.voyager.core.registry.screenModule
import id.anison.amanogawa.core.navigation.SharedScreen

val favoriteScreenModule = screenModule {
    register<SharedScreen.Favorites> { provider ->
        FavoritesScreen()
    }
}